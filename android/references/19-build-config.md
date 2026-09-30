# 19 빌드 설정

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

0에서 앱을 세울 때 한 번 정하고 오래 가는 값만 다룬다. 빌드 로직을 어디에 두는지는 10-project-structure.md가 소유하고(`R-10-09`~`R-10-14`), CI 워크플로와 시크릿 주입 경로는 31-ci-cd.md, R8을 켜는 이유와 keep 규칙 관리는 40-performance-security.md가 소유한다.

## 확정값

| 항목 | 값 | 근거 |
|---|---|---|
| compileSdk / targetSdk | 37 | AGP 9.4.0 최대 지원 API. `R-19-01` |
| minSdk | 26 | 팩 확정값. `R-19-02` |
| JDK / jvmTarget | 17 | AGP 9.4.0 최소·기본 JDK. `R-19-11` |
| buildType | debug, release | `R-19-04`, `R-19-05` |
| product flavor | 없음 | `R-19-06` |

값은 모듈 빌드 파일에 흩지 않고 `enforcement/build-logic/convention/src/main/kotlin/KotlinAndroid.kt`의 `COMPILE_SDK`·`MIN_SDK` 상수 한 곳에서 준다(`R-10-10`).

## 규칙

### R-19-01 compileSdk와 targetSdk를 37로 명시한다
- 규칙: `compileSdk`는 convention plugin 상수로, `targetSdk`는 `:app`의 `defaultConfig`에 같은 값으로 명시한다. targetSdk를 비워 두고 기본값에 맡기지 않는다.
- 근거: AGP 9.4.0의 최대 지원 API는 37이고 최소·기본 JDK는 17이다 [S46](https://developer.android.com/build/releases/gradle-plugin). AGP 9.0부터 `android.sdk.defaultTargetSdkToCompileSdkIfUnset`이 기본 `true`라 targetSdk를 안 적으면 compileSdk를 따라가고, 릴리스 노트는 "Explicitly specify target SDK"를 요구한다 [S130](https://developer.android.com/build/releases/past-releases/agp-9-0-0-release-notes). Play는 2026-08-31부터 신규 앱·업데이트에 API 36 이상을 요구하므로 37은 그 위다 [S136](https://developer.android.com/google/play/requirements/target-sdk). 37로 고정한 직접 이유는 Compose BOM 2026.08.00·lifecycle 2.11.0·androidx.hilt 1.4.0의 AAR 메타데이터가 API 37 이상 컴파일을 요구하기 때문이다(실빌드 실증 2026-09-09, `KotlinAndroid.kt` 주석).
- 예시:
  ```kotlin
  // Good — build-logic: 상수 한 곳
  internal const val COMPILE_SDK = 37
  // Good — :app/build.gradle.kts 는 convention plugin 이 준 값을 쓴다
  android { defaultConfig { targetSdk = 37 } }
  // Bad — targetSdk 를 적지 않고 compileSdk 상향에 딸려 가게 둔다
  android { compileSdk = 37 }
  // Bad — 모듈마다 숫자를 복사한다
  android { compileSdk = 36 }
  ```
- 체크: 모듈 빌드 파일에 `compileSdk =` 리터럴이 직접 적혀 있는가. `targetSdk`가 명시돼 있고 compileSdk와 같은가.

### R-19-02 minSdk는 26으로 시작한다
- 규칙: 새 앱의 `minSdk`는 26으로 잡는다. 더 내려야 하면 core library desugaring과 구버전 분기 비용을 먼저 계산하고, 내린 이유를 `MIN_SDK` 상수 옆 주석에 남긴다.
- 근거: API 26(Android 8.0)은 플랫폼 경계가 몰린 지점이다 — 알림은 "Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel" [S137](https://developer.android.com/develop/ui/views/notifications/channels), 런처 아이콘은 `res/mipmap-anydpi-v26/ic_launcher.xml`로 적응형 아이콘을 쓴다 [S138](https://developer.android.com/develop/ui/views/launch/icon_design_adaptive), `MethodHandle.invoke`·`invokeExact`를 쓰는 코드는 "you need to specify `minSdkVersion 26` or higher"다 [S139](https://developer.android.com/studio/write/java8-support). 그 아래를 지원하려면 누락 API 구현을 담은 별도 DEX 파일을 앱에 넣는 desugaring이 필요하다 [S139](https://developer.android.com/studio/write/java8-support). 26이라는 숫자 자체를 권고한 출처는 없다 — 이 팩의 확정값이다.
- 예시:
  ```kotlin
  // Good — build-logic: 상수 한 곳
  internal const val MIN_SDK = 26
  // Good — 내려야 한다면 이유를 남긴다
  internal const val MIN_SDK = 24 // 관공서 납품 단말(Android 7) 지원, desugaring 켬
  // Bad — 모듈마다 다른 minSdk
  android { defaultConfig { minSdk = 21 } }
  ```
- 체크: `minSdk`가 상수 한 곳에서 오는가. 26 미만으로 내렸다면 이유와 desugaring 설정이 함께 있는가.

### R-19-03 `namespace`와 `applicationId`를 둘 다 명시하고 섞어 쓰지 않는다
- 규칙: 모든 Android 모듈에 `namespace`를 빌드 파일로 적고(매니페스트 `package` 속성을 쓰지 않는다) 모듈마다 다른 값을 준다. `applicationId`는 `:app`의 `defaultConfig`에만 두고 출시 후 바꾸지 않는다. 라이브러리 모듈에는 `applicationId`를 두지 않는다.
- 근거: namespace는 "the Kotlin or Java package name for its generated `R` and `BuildConfig` classes"이고, applicationId는 "uniquely identifies your app on the device and in the Google Play Store"다. "Once you publish your app, you should never change the application ID." [S126](https://developer.android.com/build/configure-app-module). AGP 8.0부터 namespace는 매니페스트가 아니라 모듈 빌드 스크립트에서 설정해야 한다 [S132](https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes). AGP 9.0은 `android.uniquePackageNames`를 기본 `true`로 올려 라이브러리마다 서로 다른 패키지 이름을 요구한다 [S130](https://developer.android.com/build/releases/past-releases/agp-9-0-0-release-notes).
- 예시:
  ```kotlin
  // Good — :app/build.gradle.kts
  android {
      namespace = "com.example.app"
      defaultConfig { applicationId = "com.example.app" }
  }
  // Good — :core:network/build.gradle.kts (applicationId 없음)
  android { namespace = "com.example.app.core.network" }
  // Bad — 라이브러리 모듈이 applicationId 를 갖는다
  android { namespace = "com.example.app.core.network"; defaultConfig { applicationId = "com.example.app" } }
  // Bad — 매니페스트로 패키지를 정한다
  // <manifest package="com.example.app">
  ```
- 체크: `:core:*`·`:feature:*` 빌드 파일에 `applicationId`가 있는가. 두 모듈의 `namespace`가 같은 값인가. 매니페스트에 `package` 속성이 남아 있는가.

### R-19-04 buildType은 debug·release 둘만 두고 release에서 `optimization { enable = true }`로 켠다
- 규칙: buildType을 임의로 늘리지 않는다. release 최적화는 `optimization { enable = true }` 한 줄로 켜고 `isMinifyEnabled`·`isShrinkResources`·`getDefaultProguardFile(...)`을 따로 적지 않는다. 이 설정은 `:app` 하나만 쓰므로 convention plugin으로 올리지 않는다(`R-10-14`). Baseline Profile 생성 변이가 필요해지면 `R-40-03`이 정한 형태로만 추가한다.
- 근거: Android Studio는 새 모듈에 debug·release 두 buildType을 만든다 [S127](https://developer.android.com/build/build-variants). AGP 9.3+ 의 updated optimization DSL은 "Turning on optimization enables both code optimization and optimized resource shrinking"이고 "You no longer need to specify the default Android keep rules file"이다 [S131](https://developer.android.com/build/releases/agp-9-3-0-release-notes). DSL 형태는 `optimization { enable = true }`이며 기본 keep 규칙이 `proguard-android-optimize.txt`와 동등하게 포함되고, 커스텀 규칙은 `src/<variant>/keepRules/*.keep`에 둔다 [S129](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization). AGP 9.3 미만의 레거시 DSL은 `isMinifyEnabled`+`isShrinkResources` 쌍이다 [S128](https://developer.android.com/build/shrink-code). R8을 켜는 이유와 keep 규칙을 좁히는 규칙은 `R-40-04`다.
- 예시:
  ```kotlin
  // Good — :app/build.gradle.kts (AGP 9.4.0)
  android {
      buildTypes {
          release { optimization { enable = true } }
      }
  }
  // Bad — AGP 9.3+ 에서 레거시 DSL 을 그대로 옮겨 적는다
  release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
  }
  // Bad — staging·qa 처럼 buildType 을 늘려 변이를 배로 만든다
  buildTypes { create("staging") { initWith(getByName("debug")) } }
  ```
- 체크: buildType이 debug·release 둘뿐인가. release에 `optimization { enable = true }`가 있는가. 레거시 세 줄이 남아 중복되지 않는가.

### R-19-05 debug 변이에 `applicationIdSuffix = ".debug"`를 준다
- 규칙: debug buildType에 `applicationIdSuffix = ".debug"`와 `versionNameSuffix = "-debug"`를 넣어 릴리스 빌드와 한 기기에 함께 깔리게 한다. debug에 `isDebuggable`·서명 설정을 따로 적지 않는다.
- 근거: buildType의 `applicationIdSuffix`는 변이별 application ID를 만든다 — "the application ID for the 'free debug' build variant is 'com.example.myapp.free.debug'" [S127](https://developer.android.com/build/build-variants). debug는 도구가 이미 `debuggable true`와 generic debug keystore 서명을 붙이므로 손댈 것이 없다 [S127](https://developer.android.com/build/build-variants). 출시된 앱과 같은 application ID를 쓰면 기기에서 서로를 덮어쓰는데, 이를 피하는 수단이 suffix라는 것은 이 팩의 선택이다.
- 예시:
  ```kotlin
  // Good
  buildTypes {
      debug {
          applicationIdSuffix = ".debug"
          versionNameSuffix = "-debug"
      }
  }
  // Bad — debug 가 이미 갖고 있는 설정을 다시 적는다
  buildTypes { debug { isDebuggable = true; signingConfig = signingConfigs.getByName("debug") } }
  ```
- 체크: debug APK의 application ID가 릴리스와 다른가. debug 블록에 도구 기본값을 다시 적은 줄이 있는가.

### R-19-06 product flavor는 만들지 않는 것이 기본이다
- 규칙: flavor 없이 시작한다. 같은 코드베이스로 **배포 단위가 다른 앱**(별도 applicationId로 스토어에 따로 올리는 앱)을 내야 할 때만 flavor를 만들고 그때 `flavorDimensions`를 함께 선언한다. 서버 환경 전환·기능 토글은 flavor가 아니라 `BuildConfig` 필드(`R-19-14`)로 푼다.
- 근거: flavor를 만들면 "All flavors must now belong to a named flavor dimension"이라 차원 선언이 강제되고, 변이 수가 buildType × flavor로 곱해진다 [S127](https://developer.android.com/build/build-variants). flavor마다 소스셋(`src/<flavor>/`)이 생겨 같은 화면의 코드가 디렉터리로 갈라진다 [S127](https://developer.android.com/build/build-variants). 출처는 flavor의 용도만 정하고 도입 문턱을 정하지 않는다 — "기본은 만들지 않는다"는 이 팩의 선택이고, 모듈을 미리 늘리지 않는 `R-10-04`와 같은 이유다.
- 예시:
  ```kotlin
  // Good — 시작 형태: productFlavors 블록 자체가 없다
  android { buildTypes { release { optimization { enable = true } } } }
  // Good — 조건을 충족했을 때만
  android {
      flavorDimensions += "distribution"
      productFlavors {
          create("consumer") { dimension = "distribution" }
          create("biz") { dimension = "distribution"; applicationIdSuffix = ".biz" }
      }
  }
  // Bad — 서버 환경을 flavor 로 가른다
  productFlavors { create("dev") { dimension = "env" }; create("prod") { dimension = "env" } }
  ```
- 체크: flavor마다 별도 applicationId로 배포되는가. flavor 소스셋에 실제 파일이 있는가, 아니면 `BuildConfig` 상수 하나면 끝나는가.

### R-19-07 카탈로그 별칭은 하이픈으로만 끊고 예약어를 피한다
- 규칙: `gradle/libs.versions.toml`의 별칭은 하이픈으로 끊고(`androidx-compose-bom`), `extensions`·`class`·`convention`을 별칭으로 쓰지 않으며 `bundles`·`versions`·`plugins`를 첫 구간에 두지 않는다. convention plugin에서 `libs.findLibrary("androidx.compose.bom")`처럼 문자열로 찾을 때는 하이픈을 점으로 바꿔 1:1로 맞춘다.
- 근거: 별칭은 "identifiers separated by a dash (`-`) or underscore (`_`)"이고 각 하이픈이 접근자의 점이 된다(`ktor-client-core` → `libs.ktor.client.core`). `extensions`·`class`·`convention`은 예약어이며 `bundles`·`versions`·`plugins`는 첫 하위 구간이 될 수 없다 [S140](https://docs.gradle.org/current/userguide/version_catalogs.html). `gradle/libs.versions.toml`은 Gradle이 자동으로 읽는 기본 카탈로그다 [S140](https://docs.gradle.org/current/userguide/version_catalogs.html). 좌표를 카탈로그 한 곳에 모으는 규칙 자체는 `R-10-12`이고, 이 규칙은 그 안의 이름 짓기만 정한다.
- 예시:
  ```kotlin
  // Good — libs.versions.toml
  // androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
  // Good — convention plugin 이 찾는 문자열은 점 표기
  libs.findLibrary("androidx.compose.bom").get()
  // Bad — 예약어 별칭 (Gradle 이 거부한다)
  // versions-dependency = { ... }
  // Bad — 카멜케이스 별칭과 점 표기 조회가 어긋난다
  // androidxComposeBom = { ... }  →  findLibrary("androidx.compose.bom") 실패
  ```
- 체크: 별칭에 대문자·점이 섞여 있는가. `findLibrary` 문자열이 별칭의 하이픈을 점으로 바꾼 값과 정확히 같은가.

### R-19-08 bundle은 항상 함께 붙는 묶음에만 만든다
- 규칙: `[bundles]`는 한 모듈에 늘 같이 들어가는 의존 묶음에만 만든다. 한 곳에서만 쓰거나 모듈마다 구성이 달라지는 조합은 bundle로 묶지 않는다. Compose 의존처럼 convention plugin이 BOM과 함께 붙이는 것은 bundle을 따로 두지 않는다.
- 근거: "Bundles group multiple library aliases, so they can be referenced together in the build script." — 함께 참조되는 것이 bundle의 정의다 [S140](https://docs.gradle.org/current/userguide/version_catalogs.html). 이 팩의 Compose 의존은 `convention.android.*.compose` 플러그인이 BOM·material3·tooling을 한 번에 붙이므로 `enforcement/build-logic/libs.versions.toml.snippet`에 `[bundles]` 항목이 없다. 사용처가 하나인 묶음을 미리 만들지 않는 것은 `R-10-14`와 같은 이유다.
- 예시:
  ```kotlin
  // Good — 세 모듈 이상이 늘 같이 쓰는 묶음만
  // [bundles]
  // roborazzi = ["roborazzi", "roborazzi-compose", "roborazzi-junit-rule"]
  implementation(libs.bundles.roborazzi)
  // Bad — :app 한 곳에서만 쓰는 조합을 bundle 로 만든다
  // [bundles]
  // app-only = ["hilt-android", "kotlinx-serialization-json"]
  ```
- 체크: bundle마다 사용처가 둘 이상인가. bundle의 구성원 중 일부만 필요한 모듈이 있는가.

### R-19-09 `gradle.properties`에 JVM 메모리를 명시한다
- 규칙: 루트 `gradle.properties`에 `org.gradle.jvmargs`를 적어 데몬 힙과 metaspace를 고정한다. 기본값에 맡기지 않고, 힙을 올릴 때는 `-XX:MaxMetaspaceSize`도 함께 올린다.
- 근거: `org.gradle.jvmargs` 기본값은 `-Xmx512m "-XX:MaxMetaspaceSize=384m"`다 [S142](https://docs.gradle.org/current/userguide/build_environment.html). Android 빌드 속도 가이드는 GC가 빌드 시간의 15%를 넘으면 힙을 4·6·8GB 중에서 올리고, 기본 메모리 한도를 바꿀 때는 `-XX:MaxMetaspaceSize=1g`를 같이 지정하라고 안내하며 `-Xmx6g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -XX:+UseParallelGC -XX:MaxMetaspaceSize=1g`를 예시로 든다 [S135](https://developer.android.com/build/optimize-your-build). 어떤 수치를 쓸지는 측정으로 정하라는 것이 출처의 입장이라 상한을 규칙으로 쓰지 않는다.
- 예시:
  ```properties
  # Good — gradle.properties
  org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+UseParallelGC -Dfile.encoding=UTF-8
  # Bad — 기본 512m 에 맡기고 OOM 이 날 때만 손댄다
  # Bad — 힙만 올리고 metaspace 는 384m 로 둔다
  org.gradle.jvmargs=-Xmx6g
  ```
- 체크: `org.gradle.jvmargs` 줄이 있는가. 힙을 올렸는데 `MaxMetaspaceSize`가 빠져 있는가.

### R-19-10 configuration cache를 켜고 문제는 경고로 받는다
- 규칙: `gradle.properties`에 `org.gradle.configuration-cache=true`와 `org.gradle.configuration-cache.problems=warn`을 넣고, 로컬과 CI가 같은 설정으로 돈다.
- 근거: Gradle 9.7.1에서 `org.gradle.configuration-cache` 기본값은 `false`지만 [S142](https://docs.gradle.org/current/userguide/build_environment.html) configuration cache는 Gradle 9.0.0부터 preferred mode of execution이고 "This feature will be enabled by default in Gradle 10"이다 [S141](https://docs.gradle.org/current/userguide/configuration_cache.html). Android 빌드 속도 가이드도 같은 두 프로퍼티를 제시하며 일부 플러그인이 아직 완전 호환이 아닐 수 있으니 `problems=warn`을 함께 쓰라고 적는다 [S135](https://developer.android.com/build/optimize-your-build). 31-ci-cd.md가 "출처가 침묵"으로 미뤄 둔 항목인데, 그 보류 근거는 setup-gradle 문서에 언급이 없다는 것이었고 위 두 출처가 그 자리를 채운다.
- 예시:
  ```properties
  # Good — gradle.properties
  org.gradle.configuration-cache=true
  org.gradle.configuration-cache.problems=warn
  # Bad — gradle.properties 에는 없고 개발자가 명령마다 손으로 켠다
  #   ./gradlew --configuration-cache assembleDebug
  # Bad — CI 가 공통 설정을 플래그로 덮어써 캐시 문제를 PR 에서 못 잡는다
  #   ./gradlew --no-configuration-cache assembleDebug
  ```
- 체크: 두 프로퍼티가 커밋된 `gradle.properties`에 있는가. CI가 같은 파일을 쓰는가(별도 플래그로 덮어쓰지 않는가).

### R-19-11 Java와 Kotlin의 JVM 타깃을 17로 함께 고정한다
- 규칙: 모든 Android 모듈에서 `compileOptions`의 `sourceCompatibility`·`targetCompatibility`를 `VERSION_17`로, Kotlin `compilerOptions.jvmTarget`을 `JVM_17`로 명시한다. 세 값은 convention plugin에서 한 번에 주고 모듈에서 개별로 바꾸지 않는다.
- 근거: AGP 9.4.0의 최소·기본 JDK는 17이다 [S46](https://developer.android.com/build/releases/gradle-plugin). AGP 9.0부터 기본 source/target이 Java 11로 바뀌었으므로 적지 않으면 17이 아니다 [S130](https://developer.android.com/build/releases/past-releases/agp-9-0-0-release-notes). jdks 가이드는 "we recommend that you always explicitly specify these values or use a Java toolchain"이라 하고 "`sourceCompatibility` and `targetCompatibility` should generally use the same value"라고 정한다 [S133](https://developer.android.com/build/jdks). Kotlin 2.2 이상은 `kotlinOptions`가 아니라 `compilerOptions`로 설정한다 [S133](https://developer.android.com/build/jdks) — 이 팩의 Kotlin은 2.4.20이다.
- 예시:
  ```kotlin
  // Good — build-logic/KotlinAndroid.kt
  ext.compileOptions.apply {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
  }
  extensions.configure<KotlinAndroidProjectExtension> {
      compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
  }
  // Bad — Kotlin 2.2+ 에서 제거된 경로
  android { kotlinOptions { jvmTarget = "17" } }
  // Bad — Java 만 17 로 올리고 Kotlin 은 기본값에 둔다
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17 }
  ```
- 체크: Java 두 값과 Kotlin `jvmTarget`이 같은 숫자인가. 모듈 빌드 파일에서 이 세 값을 덮어쓴 곳이 있는가.

### R-19-12 서명 자료는 `keystore.properties`로 분리하고 커밋하지 않는다
- 규칙: 로컬 릴리스 서명은 저장소 루트의 `keystore.properties`(`.gitignore` 등록)에서 읽어 `signingConfigs`에 넣는다. 키스토어 경로·비밀번호를 빌드 파일에 직접 쓰지 않는다. CI 주입은 `R-31-08`을 따르고, 배포 키는 Play App Signing에 맡긴다. `:app`만 쓰는 설정이라 convention plugin으로 올리지 않는다(`R-10-14`).
- 근거: "If you are working in a team or sharing your code publicly, you should keep your signing information secure by removing it from the build files and storing it separately." 이고, 권고 형태가 루트의 `keystore.properties`를 빌드 스크립트에서 읽어 `signingConfigs`에 넣는 것이다 [S134](https://developer.android.com/studio/publish/app-signing). Play App Signing은 앱 서명 키를 Google이 보관하고 개발자는 업로드 키만 관리하므로 저장소에 둘 키는 업로드 키뿐이다 [S134](https://developer.android.com/studio/publish/app-signing).
- 예시:
  ```kotlin
  // Good — :app/build.gradle.kts
  val keystoreProps = Properties().apply {
      rootProject.file("keystore.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
  }
  signingConfigs {
      create("upload") {
          storeFile = keystoreProps["storeFile"]?.let { file(it as String) }
          storePassword = keystoreProps["storePassword"] as String?
      }
  }
  // Bad — 값을 빌드 파일에 그대로 적는다
  signingConfigs { create("upload") { storePassword = "hunter2" } }
  ```
- 체크: `.gitignore`에 `keystore.properties`와 `*.jks`가 있는가. 저장소 이력에 비밀번호 문자열이 남아 있는가.

### R-19-13 `buildFeatures`는 실제로 쓰는 모듈에서만 켠다
- 규칙: `buildConfig`·`resValues`·`shaders`·`aidl`은 그 기능을 쓰는 모듈의 `buildFeatures`에서만 `true`로 켠다. 루트나 convention plugin에서 전역으로 켜지 않는다.
- 근거: "AGP 8.0 doesn't generate `BuildConfig` by default. You need to specify this option using the DSL in the projects where you need it." [S132](https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes). AGP 9.0은 `android.defaults.buildfeatures.resvalues`·`shaders` 기본값을 `true`→`false`로 내렸고 `aidl`·`renderscript` 전역 프로퍼티를 아예 제거해 모듈 DSL로만 켜게 했다 [S130](https://developer.android.com/build/releases/past-releases/agp-9-0-0-release-notes). 전역 주입 금지는 `R-10-11`과 같은 이유다.
- 예시:
  ```kotlin
  // Good — :app 만 BuildConfig 를 쓴다
  android { buildFeatures { buildConfig = true } }
  // Bad — 모든 모듈에 켜서 쓰지 않는 클래스를 생성한다
  // convention plugin: ext.buildFeatures.buildConfig = true
  // Bad — 제거된 전역 프로퍼티에 기대어 gradle.properties 로 켠다
  // android.defaults.buildfeatures.aidl=true
  ```
- 체크: `buildConfig = true`인 모듈이 실제로 `BuildConfig`를 참조하는가. `gradle.properties`에 `android.defaults.buildfeatures.*` 줄이 남아 있는가.

### R-19-14 `BuildConfig` 필드는 `:app`에만 두고 다른 모듈에는 주입으로 넘긴다
- 규칙: `buildConfigField`로 만드는 값(엔드포인트·키·플래그)은 `:app`에서만 선언한다. `:core:*`·`:feature:*`는 `BuildConfig`를 직접 읽지 않고 `:app`의 Hilt 모듈이 넘긴 값을 생성자로 받는다. 값 자체를 소스에 상수로 두지 않는 것은 `R-40-11`, CI 주입 경로는 `R-31-08`이다.
- 근거: `BuildConfig`는 모듈의 `namespace` 아래에 생성되므로 [S126](https://developer.android.com/build/configure-app-module) 모듈마다 다른 클래스가 생기고, 같은 값을 여러 모듈에서 읽으면 정의가 흩어진다. AGP 8.0부터 생성 자체가 모듈별 opt-in이라 켠 모듈이 곧 그 값의 소유 모듈이다 [S132](https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes). `:core:*`는 `:app`을 참조할 수 없으므로(`R-10-03`) 전달 수단은 주입뿐이고, 변이별 값을 `:app`이 소유하는 것은 `R-10-08`과 같은 이유다.
- 예시:
  ```kotlin
  // Good — :app/build.gradle.kts
  defaultConfig { buildConfigField("String", "BASE_URL", "\"https://api.example.com\"") }
  // Good — :app 의 di/ 에서 값을 넘긴다
  @Provides @BaseUrl fun provideBaseUrl(): String = BuildConfig.BASE_URL
  // Bad — :core:network 가 :app 의 BuildConfig 를 직접 읽는다
  class ApiClient { private val url = BuildConfig.BASE_URL }
  ```
- 체크: `:app` 밖에서 `BuildConfig.` 참조가 있는가. 같은 값이 두 모듈의 `buildConfigField`에 각각 선언돼 있는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **카탈로그 섹션 순서**: Gradle 문서는 TOML 형식을 lenient하다고 설명하고 `[versions]`·`[libraries]`·`[bundles]`·`[plugins]` 순서를 강제하지 않는다 [S140](https://docs.gradle.org/current/userguide/version_catalogs.html). 팩 스니펫의 순서는 문서 표기 순서를 따른 것이고 규칙이 아니다.
- **데몬 힙의 구체 수치**: 가이드가 "4, 6, 8GB 중에서 올려 측정하라"까지만 정하므로 숫자를 규칙으로 고정하지 않는다 [S135](https://developer.android.com/build/optimize-your-build). `R-19-09`는 "명시한다"까지만 정한다.
- **versionCode·versionName 부여 방식**: 출처는 여전히 방식을 정하지 않는다. versionCode는 팩 결정으로 `R-31-11`(저장소의 카운터 파일 + 릴리스 스크립트)이 소유하고(2026-09-30), versionName 형식은 프로젝트 지침 파일에서 정할 자리다
- **`org.gradle.parallel`·`org.gradle.caching`**: 기본값은 둘 다 `false`지만 [S142](https://docs.gradle.org/current/userguide/build_environment.html) configuration cache가 켜지면 프로젝트 내 병렬 실행이 항상 켜지므로 [S141](https://docs.gradle.org/current/userguide/configuration_cache.html) 중복 설정을 규칙으로 두지 않는다.
