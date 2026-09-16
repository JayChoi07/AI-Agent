# 조사 노트 — 앱 부트스트랩 빌드 설정

조사일: 2026-09-16. 대상 문서: `references/19-build-config.md`(R-19-01 ~ R-19-14).
조사 방식: 아래 모든 URL을 WebFetch로 직접 열어 확인했다. 기억으로 쓴 내용은 없다.
확정 전제: AGP 9.4.0 / Gradle 래퍼 9.7.1 / Kotlin 2.4.20 / JDK 17 (`enforcement/build-logic/libs.versions.toml.snippet`, `KotlinAndroid.kt`).

## 확인한 문서와 확인 내용

| 문서 | 확인한 내용 |
|---|---|
| Configure your app module (`/build/configure-app-module`) | namespace = 생성 `R`·`BuildConfig`의 패키지. applicationId = 기기·Play에서의 앱 식별자. "Once you publish your app, you should never change the application ID." 기본적으로 둘은 같은 값이며, 다르면 빌드 도구가 최종 매니페스트에 applicationId를 복사해 넣는다. 이 페이지는 compileSdk/minSdk/targetSdk/buildTypes/flavors를 **설명하지 않는다**(코드 예시에만 등장). |
| Configure build variants (`/build/build-variants`) | debug·release 두 buildType이 새 모듈의 기본. debug는 도구가 `debuggable true` + generic debug keystore 서명을 설정한다. buildType·flavor의 `applicationIdSuffix`·`versionNameSuffix` 동작(예: `com.example.myapp.free.debug`). "All flavors must now belong to a named flavor dimension." 소스셋 우선순위 4단계. |
| Shrink, obfuscate, optimize (`/build/shrink-code`) | AGP 9.3+ 는 `optimization { enable = true }`, 그 미만은 `isMinifyEnabled` + `isShrinkResources` + `proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))`. "We recommend that you always enable both settings." `proguard-android.txt`는 `-dontoptimize` 때문에 지원 중단. |
| Enable app optimization (`/topic/performance/app-optimization/enable-app-optimization`) | `optimization { enable = true }` 정확한 DSL 형태 확인. 기본 keep 규칙이 `proguard-android-optimize.txt`와 동등하게 포함돼 따로 지정할 필요 없음. 커스텀 규칙은 `src/<variant>/keepRules/*.keep`. release 변이에만 켜라는 권고. |
| AGP 9.3.0 릴리스 노트 | updated optimization DSL 도입. "Turning on optimization enables both code optimization and optimized resource shrinking." 레거시 DSL은 계속 지원. **`minifyEnabled`·`shrinkResources`를 deprecated로 명시하지는 않는다**. |
| AGP 9.0.0 릴리스 노트 | 기본값 변경 다수 확인: `builtInKotlin` false→true, `newDsl` false→true, `defaults.buildfeatures.resvalues`·`shaders` true→false, `aidl`·`renderscript` 전역 프로퍼티 제거, `sdk.defaultTargetSdkToCompileSdkIfUnset` false→true(+ "Explicitly specify target SDK"), `enableAppCompileTimeRClass` false→true(앱도 non-final R), `uniquePackageNames` false→true, `proguard.failOnMissingFiles` false→true, `r8.proguardAndroidTxt.disallowed` true, `r8.optimizedResourceShrinking` true, `r8.strictFullModeForKeepRules` true, 최소 JDK 17 / **기본 source·target Java 11**. |
| AGP 8.0.0 릴리스 노트 | `android.defaults.buildfeatures.buildconfig` true→false. "AGP 8.0 doesn't generate `BuildConfig` by default." `namespace`를 매니페스트가 아닌 모듈 빌드 스크립트에서 설정해야 함. |
| AGP 릴리스 노트 (`/build/releases/gradle-plugin`) | AGP 9.4: Gradle 최소·기본 9.6.0, SDK Build Tools 36.0.0, JDK 최소·기본 17, **Max API Level 37**. |
| Java versions in Android builds (`/build/jdks`) | `sourceCompatibility`/`targetCompatibility`의 의미. "we recommend that you always explicitly specify these values or use a Java toolchain." "In practice, `sourceCompatibility` and `targetCompatibility` should generally use the same value." Kotlin 2.2 미만만 `kotlinOptions.jvmTarget` 필요. |
| Sign your app (`/studio/publish/app-signing`) | 루트 `keystore.properties`를 빌드 스크립트에서 읽어 `signingConfigs`에 넣는 형태. "you should keep your signing information secure by removing it from the build files and storing it separately." Play App Signing = 앱 서명 키는 Google 보관, 개발자는 업로드 키만. |
| Optimize your build speed (`/build/optimize-your-build`) | `org.gradle.jvmargs=-Xmx6g …` 예시, 힙은 4·6·8GB 중 측정해 선택, 기본 한도를 바꾸면 `-XX:MaxMetaspaceSize=1g`도 지정(Gradle #19750). `org.gradle.configuration-cache=true` + `org.gradle.configuration-cache.problems=warn` 권고. `org.gradle.parallel`·`org.gradle.caching`은 이 절에서 다루지 않음. |
| Play target API 요구사항 (`/google/play/requirements/target-sdk`) | 2026-08-31부터 신규·업데이트 앱 API 36+ (Wear/Automotive 35+, TV/XR 34+). 기존 앱은 35+ 라야 상위 OS 신규 사용자에게 노출. 연장 신청 2026-11-01. |
| Notification channels (`/develop/ui/views/notifications/channels`) | "Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel." targetSdk 26+ 는 채널 구현 필수. |
| Adaptive icons (`/develop/ui/views/launch/icon_design_adaptive`) | "save your adaptive icon to `res/mipmap-anydpi-v26/ic_launcher.xml`" — v26 한정자 확인. 페이지가 "API 26에서 도입"이라고 문장으로 쓰지는 않는다. |
| Java 8+ API desugaring (`/studio/write/java8-support`) | 누락 API 구현을 담은 **별도 DEX 파일**을 앱에 넣는 방식. "If your source code or one of your module dependencies uses one of these methods, you need to specify `minSdkVersion 26` or higher."(MethodHandle.invoke/invokeExact) |
| Gradle Version catalogs (`/userguide/version_catalogs.html`) | 4개 섹션은 선택적이고 TOML 형식은 lenient. 별칭은 `-`/`_` 구분, 하이픈이 접근자의 점이 됨. 예약어 `extensions`·`class`·`convention`, 첫 구간 불가 `bundles`·`versions`·`plugins`. `gradle/libs.versions.toml`은 자동 임포트. bundle 정의. |
| Gradle Configuration cache (`/userguide/configuration_cache.html`) | 문서 판본 Gradle **9.7.1**. 기본 비활성. "This feature will be enabled by default in Gradle 10." Gradle 9.0.0부터 preferred mode of execution. 활성 시 프로젝트 내 병렬 태스크 실행이 항상 켜지고 `--no-parallel`로 끌 수 없음. |
| Gradle Build environment (`/userguide/build_environment.html`) | `org.gradle.jvmargs` 기본 `-Xmx512m "-XX:MaxMetaspaceSize=384m"`. `org.gradle.parallel` 기본 false, `org.gradle.caching` 기본 false, `org.gradle.configuration-cache` 기본 false. `gradle.properties` 위치·우선순위. |

## 판단이 갈린 지점

1. **R8을 어느 DSL로 적을 것인가.** `/build/shrink-code`가 AGP 9.3+ 용 `optimization { enable = true }`와 레거시 `isMinifyEnabled`/`isShrinkResources`를 나란히 제시한다. 팩의 AGP가 9.4.0이므로 새 DSL을 규칙으로 잡았다(R-19-04). 다만 AGP 9.3 릴리스 노트는 레거시 DSL이 "continues to be supported"라고만 하고 deprecated 표기를 하지 않는다 — 기존 40-performance-security.md `R-40-04`의 `isMinifyEnabled = true` 표현이 틀린 것은 아니다. **R-19-04는 "어느 DSL로 적나", R-40-04는 "왜 켜고 keep 규칙을 어떻게 좁히나"로 소유를 갈랐다.** 40 쪽 예시를 새 DSL로 맞출지는 팀 리드 판단 영역으로 남긴다.

2. **minSdk 26의 근거.** 특정 minSdk 값을 권고한 공식 문서는 없다. 대신 API 26이 플랫폼 경계인 사실 세 건(알림 채널 필수, 적응형 아이콘 `mipmap-anydpi-v26`, `MethodHandle.invoke` 최소 26)과 그 아래를 지원할 때 드는 비용(desugaring 별도 DEX)을 근거로 붙이고, **26이라는 숫자 자체는 팩 확정값**임을 규칙 본문에 적었다. 값은 지시대로 바꾸지 않았다.

3. **configuration cache가 31-ci-cd.md와 어긋난다.** 31-ci-cd.md의 "출처가 침묵하는 것"은 configuration cache 활성화를 보류해 뒀고 그 근거는 "setup-gradle 확인본에 언급이 없다"였다. 이번에 Gradle 공식 문서(preferred mode / Gradle 10 기본값)와 Android 빌드 속도 가이드(두 프로퍼티 예시)를 확인해 R-19-10으로 규칙화했다. **31-ci-cd.md의 해당 불릿은 이제 근거가 없으므로 병합 시 삭제 대상**이다.

4. **카탈로그 섹션 순서.** 브리프가 요청한 항목이지만 Gradle 문서가 형식을 lenient라 하고 순서를 강제하지 않는다. 규칙으로 만들지 않고 "출처가 침묵하는 것"에 내렸다. 별칭 네이밍(예약어·하이픈→점 매핑)만 규칙화했다(R-19-07) — 이쪽은 문서가 명시적으로 정한다.

5. **`org.gradle.parallel`·`org.gradle.caching`.** 켜라고 쓰고 싶었으나 Android 가이드가 이 두 프로퍼티를 다루지 않고, configuration cache가 켜지면 프로젝트 내 병렬 실행이 이미 강제되므로 규칙을 만들지 않았다.

6. **debug `applicationIdSuffix`의 목적.** 출처는 suffix의 *동작*만 정하고 "릴리스와 동시 설치를 위해 붙여라"고 말하지 않는다. 규칙 근거 줄에 그 부분이 팩의 선택임을 적었다.

7. **`namespace` = 앱 패키지 기본값.** `/build/configure-app-module`은 "For a simpler workflow, keep your namespace the same as your application ID"라고 권한다. 반면 멀티모듈에서는 AGP 9의 `uniquePackageNames`가 모듈마다 다른 패키지를 요구한다. 두 진술이 충돌하지 않도록 R-19-03을 "`:app`은 둘을 같게, 라이브러리 모듈은 각자 다른 namespace"로 썼다.

## 기존 R-ID 인용만 하고 넘어간 주제 (새 규칙 없음)

| 주제 | 기존 소유 | 인용 위치 |
|---|---|---|
| build-logic included build / `buildSrc` 금지 | `R-10-09` | 문서 머리말 |
| convention plugin 6종·id 규칙 | `R-10-10` | 확정값 절, R-19-01 |
| `subprojects{}`·`allprojects{}` 금지 | `R-10-11` | R-19-13 |
| 좌표·버전을 카탈로그 한 곳에서 | `R-10-12` | R-19-07 |
| 일회성 빌드 로직은 모듈 파일에 | `R-10-14` | R-19-04, R-19-08, R-19-12 |
| 미리 공유로 올리지 않기 | `R-10-04` | R-19-06 |
| variant별 구현 주입은 `:app` 소유 | `R-10-08` | R-19-14 |
| core는 app을 참조하지 않는다 | `R-10-03` | R-19-14 |
| 키·서명 자료 CI 시크릿 주입 | `R-31-08` | R-19-12, R-19-14 |
| R8 켠 릴리스로 검증 | `R-31-09`, `R-40-01` | (중복 회피, 규칙 없음) |
| 릴리스는 R8을 켠다 / keep 규칙 좁히기 | `R-40-04` | R-19-04 |
| Baseline Profile 생성 변이의 minify | `R-40-03` | R-19-04 |
| 시크릿을 소스에 커밋하지 않기 | `R-40-11` | R-19-14 |

## 신규 출처 표

| S126 | Google | Configure your app module | https://developer.android.com/build/configure-app-module | 공식 가이드 |
| S127 | Google | Configure build variants | https://developer.android.com/build/build-variants | 공식 가이드 |
| S128 | Google | Shrink, obfuscate, and optimize your app | https://developer.android.com/build/shrink-code | 공식 가이드 |
| S129 | Google | Enable app optimization | https://developer.android.com/topic/performance/app-optimization/enable-app-optimization | 공식 가이드 |
| S130 | Google | AGP 9.0.0 릴리스 노트 | https://developer.android.com/build/releases/past-releases/agp-9-0-0-release-notes | 공식 문서 |
| S131 | Google | AGP 9.3.0 릴리스 노트 | https://developer.android.com/build/releases/agp-9-3-0-release-notes | 공식 문서 |
| S132 | Google | AGP 8.0.0 릴리스 노트 | https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes | 공식 문서 |
| S133 | Google | Java versions in Android builds | https://developer.android.com/build/jdks | 공식 가이드 |
| S134 | Google | Sign your app | https://developer.android.com/studio/publish/app-signing | 공식 가이드 |
| S135 | Google | Optimize your build speed | https://developer.android.com/build/optimize-your-build | 공식 가이드 |
| S136 | Google | Meet Google Play's target API level requirement | https://developer.android.com/google/play/requirements/target-sdk | 공식 정책 |
| S137 | Google | Create and manage notification channels | https://developer.android.com/develop/ui/views/notifications/channels | 공식 가이드 |
| S138 | Google | Create adaptive icons | https://developer.android.com/develop/ui/views/launch/icon_design_adaptive | 공식 가이드 |
| S139 | Google | Java 8+ API desugaring support | https://developer.android.com/studio/write/java8-support | 공식 가이드 |
| S140 | Gradle | Version catalogs | https://docs.gradle.org/current/userguide/version_catalogs.html | 공식 가이드 |
| S141 | Gradle | Configuration cache | https://docs.gradle.org/current/userguide/configuration_cache.html | 공식 가이드 |
| S142 | Gradle | Build environment (Gradle properties) | https://docs.gradle.org/current/userguide/build_environment.html | 공식 가이드 |

조사 노트: app-bootstrap-build (2026-09-16). 기존 URL 재사용: S46(AGP 릴리스 노트), S38(보안 권장사항) — 새 번호를 부여하지 않았다.

2026-09-16 astra 리뷰 반영: R-19-04(`optimization { enable = true }`)가 R-40-01·R-40-03·R-40-04·R-31-09의 `isMinifyEnabled` 문구·예시와 충돌해 40·31은 "최적화 켬" 요구만 남기고 DSL 소유를 R-19-04로 통일(R-40-03 예시는 `optimization { enable = false }`로 바꿨고 이 DSL 조합은 실빌드 미실증). R-19-10 Bad를 실제 명령으로 교체. S130의 `defaultTargetSdkToCompileSdkIfUnset` 인용은 2026-09-16 재확인됨(astra 보류 해소).
