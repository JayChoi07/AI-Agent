# 10 프로젝트 구조

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

## 모듈 의존 그래프

| 모듈 | 의존해도 되는 모듈 | 의존하면 안 되는 모듈 |
|---|---|---|
| `:app` | 모든 `:feature:*`, 모든 `:core:*` | 없음(최상위 진입점) |
| `:feature:<name>` | `:core:domain`, `:core:data`, `:core:model`, `:core:ui`, `:core:designsystem`, `:core:common` | 다른 `:feature:*`, `:app`, `:core:network`, `:core:database`, `:core:datastore` |
| `:core:ui` | `:core:designsystem`, `:core:model`, `:core:common` | `:feature:*`, `:app`, `:core:data`, `:core:domain` |
| `:core:designsystem` | `:core:common` | 그 밖 전부 |
| `:core:domain` | `:core:data`, `:core:model`, `:core:common` | `:feature:*`, `:app`, `:core:ui`, `:core:designsystem` |
| `:core:data` | `:core:model`, `:core:network`, `:core:database`, `:core:datastore`, `:core:common` | `:feature:*`, `:app`, `:core:ui`, `:core:domain` |
| `:core:network` / `:core:database` / `:core:datastore` | `:core:model`, `:core:common` | `:feature:*`, `:app`, `:core:ui`, `:core:domain`, `:core:data` |
| `:core:model` | 없음(순수 Kotlin/JVM 모듈) | 전부 |
| `:core:common` | 없음 | 전부 |
| `:core:testing` | 테스트 대상 모듈(`testImplementation` 전용) | 프로덕션 소스셋 어디에서도 참조 금지 |

의존은 한 방향으로만 흐른다. `:feature:*` → `:core:domain` → `:core:data` → `:core:model`이며, 도메인 계층은 선택이므로 UseCase가 없는 화면은 `:feature:*`가 `:core:data`를 바로 본다(11-architecture-layers.md `R-11-01`). Repository는 인터페이스와 구현이 모두 `:core:data`에 있고 다른 계층은 인터페이스 타입만 쓴다(`R-11-02`). 구현을 인터페이스에 붙이는 `@Binds` 모듈은 그 구현을 소유한 모듈의 `di/`에 둔다(14-di.md `R-14-04`, `R-14-05`).

기능 하나를 5개 모듈로 쪼개는 방식([S115](https://toss.tech/article/slash23-iOS))은 채택하지 않는다. 700모듈 규모를 전제한 iOS 사례이고, 그린필드 기본값으로는 [S03](https://developer.android.com/topic/modularization)의 과세분화 경고에 정면으로 걸린다.

### R-10-01 모듈 유형을 `:app`·`:core:*`·`:feature:*` 셋으로 한정한다
- 규칙: 새 모듈은 `:app`(진입점 하나), `:core:{common,model,domain,data,network,database,datastore,ui,designsystem,testing}`, `:feature:<name>` 중 하나로만 만든다. 세 유형에 안 맞는 코드는 새 모듈 대신 기존 모듈의 패키지로 둔다.
- 근거: Google 모듈화 패턴이 app·feature·data·common(core)·test 유형을 제시한다 [S04](https://developer.android.com/topic/modularization/patterns). NiA는 core 하위를 data·database·model·network·ui·designsystem·common·testing·datastore로 둔다 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md). 모델·에러·결과 타입을 담는 `:core:model`을 따로 두는 것도 같은 목록에서 왔고, 아무 계층에도 의존하지 않는 바닥 모듈이 있어야 data가 domain을 거꾸로 참조하지 않는다 [S01](https://developer.android.com/topic/architecture). 유형을 늘릴수록 모듈당 오버헤드가 쌓여 유지보수가 어려워진다 [S03](https://developer.android.com/topic/modularization).
- 예시:
  ```kotlin
  // Good — settings.gradle.kts
  include(":app")
  include(":core:common", ":core:model", ":core:domain", ":core:data", ":core:ui", ":core:designsystem")
  include(":feature:login", ":feature:home")
  // Bad — 유형 밖 모듈
  include(":util", ":helpers", ":shared")
  ```
- 체크: 추가된 모듈 경로가 `:app`·`:core:`·`:feature:` 중 하나로 시작하는가. 이름이 `util`·`common2`·`shared`처럼 책임을 말하지 않는가.

### R-10-02 `:feature:*`끼리 직접 의존하지 않는다
- 규칙: feature 모듈의 빌드 파일에 다른 feature 모듈을 넣지 않는다. 화면 사이 연결은 콜백 람다로 위에 올리고 `:app`이 조합해 목적지를 정한다.
- 근거: feature 모듈은 서로 의존하지 않고 app 모듈이 mediator로 네비게이션을 중재한다 [S04](https://developer.android.com/topic/modularization/patterns). NiA도 impl이 다른 feature의 api에만 의존하도록 제한한다 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md). 확정 결정 FEATURE_MODULE_SPLIT은 단일 feature 모듈에서 시작하고 feature 간 직접 이동을 두지 않는다.
- 예시:
  ```kotlin
  // Good — :feature:home/build.gradle.kts
  implementation(project(":core:domain"))
  // Composable은 목적지를 모른다
  @Composable fun HomeScreen(onDetailClick: (String) -> Unit)
  // Bad
  implementation(project(":feature:detail"))
  ```
- 체크: feature 빌드 파일에 `project(":feature:` 문자열이 있는가. 화면 함수가 다른 feature의 NavKey 타입을 파라미터로 받는가.

### R-10-03 `:core:*`는 `:feature:*`·`:app`에 의존하지 않는다
- 규칙: core 모듈의 의존은 다른 core 모듈까지만이다. core에서 feature나 app의 타입이 필요해지면 그 타입을 core로 내리거나 인터페이스로 뒤집는다.
- 근거: core 모듈은 feature·app 모듈에 의존하지 않는다 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md). 모듈은 다른 모듈의 내부 동작을 몰라야 한다는 저결합 원칙과 같은 이야기다 [S04](https://developer.android.com/topic/modularization/patterns).
- 예시:
  ```kotlin
  // Good — :core:ui가 필요한 것은 모델뿐
  implementation(project(":core:model"))
  // Bad — core가 화면을 안다
  implementation(project(":feature:home"))
  ```
- 체크: `:core:` 하위 빌드 파일에 `:feature:`·`:app` 참조가 있는가.

### R-10-04 코드 배치는 그 코드를 쓰는 모듈 수로 정한다
- 규칙: 한 feature만 쓰는 코드는 그 feature 모듈 안에 둔다. 둘 이상의 모듈이 쓰게 되는 시점에 `:core:*`로 올린다. "언젠가 공유할 것 같아서" 미리 올리지 않는다. 모듈 간 배치는 이 규칙, 모듈 내부 패키지 분리는 R-10-06이다.
- 근거: 한 feature 모듈만 필요로 하는 클래스는 그 모듈에 남기고 아니면 core로 옮기라는 NiA 배치 기준 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md). 모듈을 미리 늘리면 오버헤드만 남는다 [S03](https://developer.android.com/topic/modularization).
- 예시:
  ```kotlin
  // Good — :feature:login 안에서만 쓰는 검증기
  internal class EmailValidator
  // Bad — 쓰는 곳이 하나인데 :core:common에 미리 올림
  class EmailValidator
  ```
- 체크: core로 옮긴 타입의 실제 사용처가 둘 이상인가.

### R-10-05 모듈 의존은 `implementation`으로 선언한다
- 규칙: `api`는 그 모듈의 공개 API가 전이 노출을 의도할 때만 쓴다. 기본은 `implementation`이다.
- 근거: 공개 표면을 최소화하고 `implementation`을 선호하라는 권고이며 빌드 시간에도 유리하다 [S04](https://developer.android.com/topic/modularization/patterns). 각 부분은 다른 부분을 가능한 한 적게 알아야 한다 [S03](https://developer.android.com/topic/modularization).
- 예시:
  ```kotlin
  // Good
  implementation(project(":core:domain"))
  // Bad — 이유 없이 전이 노출
  api(project(":core:network"))
  ```
- 체크: `api(` 선언마다 전이 노출이 필요한 이유가 주석이나 PR 설명에 있는가.

### R-10-06 모듈 안 패키지는 계층 이름으로 가른다
- 규칙: feature 모듈은 `{{package}}.feature.{{feature}}.{ui,domain,data,model,di}` 5종, core 모듈은 `{{package}}.core.{{name}}`을 루트로 쓴다. 모델·에러·결과 타입은 `model`에 두고, 이 패키지 밖으로 나갈 필요가 없는 타입은 `internal`로 선언한다. 이 규칙은 모듈 내부 패키지 분리만 다룬다. 어느 모듈에 둘지는 R-10-04다.
- 근거: 계층 이름(ui·domain·data)은 공식 아키텍처 가이드의 계층 명칭을 그대로 쓴 것이고, 계층 의존이 한 방향으로 흐르려면 모델이 어느 계층에도 속하지 않는 자리에 있어야 한다 [S01](https://developer.android.com/topic/architecture). NiA도 모델을 별도 `model`로 뺀다 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md). 공개 인터페이스 외에는 `internal`/`private`로 막아 가시성을 제어하라는 모듈화 원칙에서 왔다 [S03](https://developer.android.com/topic/modularization).
- 예시:
  ```kotlin
  // Good
  package com.example.app.feature.login.ui
  package com.example.app.feature.login.data
  package com.example.app.feature.login.model
  // Bad — 계층이 아니라 타입 종류로 가름
  package com.example.app.feature.login.utils
  ```
- 체크: 패키지 마지막 segment가 `ui`·`domain`·`data`·`model`·`di` 중 하나인가. feature 밖에서 쓰지 않는 클래스가 public인가.

### R-10-07 네비게이션 인자로 도메인 객체를 넘기지 않는다
- 규칙: 화면 간에는 ID 같은 원시 값만 전달하고, 받는 화면이 그 ID로 데이터 계층에서 다시 읽는다.
- 근거: 네비게이션에는 객체 대신 단순 id를 써서 대상 화면이 데이터 계층에서 리소스를 로드하게 하라는 권고 [S04](https://developer.android.com/topic/modularization/patterns).
- 예시:
  ```kotlin
  // Good
  data class ArticleDetailKey(val articleId: String) : NavKey
  // Bad
  data class ArticleDetailKey(val article: Article) : NavKey
  ```
- 체크: NavKey 프로퍼티 타입이 원시 타입·문자열·enum 밖으로 나가는가.

### R-10-08 루트 네비게이션과 구현 바인딩은 `:app`이 소유한다
- 규칙: 백스택 정의, feature 조합, variant별 구현 주입(`debugImplementation`/`releaseImplementation`)은 `:app`에만 둔다.
- 근거: app 모듈이 진입점이자 root navigation 제공자이며, 추상 모듈과 구현 모듈을 variant별로 붙이는 것도 app 모듈의 일이다 [S04](https://developer.android.com/topic/modularization/patterns).
- 예시:
  ```kotlin
  // Good — :app/build.gradle.kts
  debugImplementation(project(":core:data-fake"))
  releaseImplementation(project(":core:data-network"))
  // Bad — feature가 variant별로 구현 모듈을 골라 붙임
  releaseImplementation(project(":core:data-network"))
  ```
- 체크: `:app` 밖에서 variant 한정 의존(`debugImplementation` 등)을 선언한 모듈이 있는가.

### R-10-09 공통 빌드 설정은 `build-logic` included build에 둔다
- 규칙: 여러 모듈이 공유하는 빌드 설정은 `build-logic` included build의 convention plugin으로 만든다. `buildSrc`는 쓰지 않는다.
- 근거: NiA는 convention plugin을 `buildSrc`가 아니라 included build에 둔다 [S53](https://raw.githubusercontent.com/android/nowinandroid/main/build-logic/README.md). Trade Republic은 `buildSrc` 변경이 모든 classpath를 무효화해 불필요한 재빌드를 유발한다고 실측하고 이관했다 [S109](https://traderepublic.substack.com/p/state-of-android-at-tr-2024-edition). Gradle도 빌드 로직 공유의 정석을 convention plugin으로 안내한다 [S69](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html).
- 예시:
  ```kotlin
  // Good — settings.gradle.kts
  pluginManagement { includeBuild("build-logic") }
  // Good — build-logic/settings.gradle.kts (ktlint-gradle 는 Maven Central 에 없다)
  dependencyResolutionManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
  // Bad — buildSrc/src/main/kotlin/AndroidLibraryConventionPlugin.kt
  ```
- 체크: 저장소 루트에 `buildSrc/`가 있는가. `includeBuild("build-logic")`이 선언돼 있는가. included build 저장소에 `gradlePluginPortal()`이 있는가(실빌드 실증 2026-09-09: 없으면 ktlint-gradle 14.2.0 해석 실패).

### R-10-10 convention plugin은 6종으로 쪼개고 id는 `convention.android.*`를 따른다
- 규칙: `convention.android.application`, `convention.android.application.compose`, `convention.android.library`, `convention.android.library.compose`, `convention.android.feature`, `convention.android.hilt` 여섯 개만 두고, 각 모듈은 필요한 것만 골라 적용한다. `*.compose`는 `application`/`library` 계열과 짝으로 쓴다 — 단독으로는 compileSdk·JVM·품질 설정이 없다.
- 근거: plugin은 additive·composable하게 설계해 모듈이 필요한 것만 적용하고, id는 `<project>.android.<component>[.<tech>]` 형태로 짓는다 [S53](https://raw.githubusercontent.com/android/nowinandroid/main/build-logic/README.md). 이 팩은 `<project>` 자리에 `convention`을 쓴다. plugin id는 스크립트 파일명에서 확장자를 뺀 값이라는 Gradle 규칙과도 맞춘다 [S69](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html). `application.compose`를 따로 둔 이유는 `library.compose`가 `com.android.library`를 적용해 `:app`에 쓸 수 없기 때문이다(실빌드 실증 2026-09-09).
- 예시:
  ```kotlin
  // Good — :app/build.gradle.kts
  plugins { id("convention.android.application"); id("convention.android.application.compose") }
  // Good — :feature:login/build.gradle.kts
  plugins { id("convention.android.feature"); id("convention.android.library.compose") }
  // Bad — 모듈마다 android {} 블록을 복사
  ```
- 체크: 모듈 빌드 파일에 compileSdk·JDK·Compose 설정이 직접 복사돼 있는가. plugin id가 여섯 개 목록 밖인가.

### R-10-11 `subprojects {}`·`allprojects {}`로 설정을 주입하지 않는다
- 규칙: 루트 빌드 파일에서 하위 프로젝트를 일괄 설정하지 않는다. 공유가 필요하면 R-10-10의 convention plugin을 만든다.
- 근거: cross-project configuration은 잘못된 공유 방식이다. 주입된 로직이 하위 빌드 스크립트에서 보이지 않고, 구성 시점 결합이 생겨 configuration-on-demand 같은 최적화를 막는다 [S69](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html).
- 예시:
  ```kotlin
  // Good — 각 모듈이 plugins { id("convention.android.library") }
  // Bad — 루트 build.gradle.kts
  subprojects { apply(plugin = "org.jetbrains.kotlin.android") }
  ```
- 체크: 루트 빌드 파일에 `subprojects {`·`allprojects {` 블록이 있는가.

### R-10-12 의존성과 플러그인 버전은 버전 카탈로그 한 곳에서 관리한다
- 규칙: 모든 라이브러리·플러그인 좌표와 버전은 루트 `gradle/libs.versions.toml`에 두고 모듈에서는 `libs.` 접근자로만 참조한다. 별칭은 kebab-case로 쓴다. 파일명은 바꾸지 않는다.
- 근거: 카탈로그는 루트의 `gradle/libs.versions.toml`에 두고 기본 파일명을 유지하며, `[versions]`/`[libraries]`/`[plugins]` 구성과 kebab-case 별칭으로 type-safe 접근자를 얻는다. 목적은 버전의 single source of truth다 [S11](https://developer.android.com/build/migrate-to-catalogs). 모듈화 가이드도 버전 카탈로그와 convention plugin으로 빌드 설정을 공유하라고 안내한다 [S04](https://developer.android.com/topic/modularization/patterns).
- 예시:
  ```kotlin
  // Good
  implementation(libs.androidx.lifecycle.runtime.compose)
  // Bad
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
  ```
- 체크: 빌드 파일에 문자열 좌표 리터럴이 있는가. 같은 라이브러리 버전이 두 곳에 적혀 있는가.

### R-10-13 `:feature:*`는 단일 모듈로 시작하고 조건이 맞을 때만 나눈다
- 규칙: feature는 `:feature:<name>` 하나로 만든다. 다른 feature가 이 feature의 NavKey를 직접 참조해야 하는 상황이 실제로 생겼을 때만 그 feature를 `:feature:<name>:api` / `:feature:<name>:impl`로 나누고, impl은 다른 feature의 api에만 의존한다.
- 근거: 기본형은 feature 단일 모듈이다 [S04](https://developer.android.com/topic/modularization/patterns). api/impl 분할은 NiA [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md)와 Trade Republic [S109](https://traderepublic.substack.com/p/state-of-android-at-tr-2024-edition)가 쓰지만 모듈 수가 두 배가 되므로 [S03](https://developer.android.com/topic/modularization)의 과세분화 경고를 근거로 조건부로만 적용한다. 확정 결정 FEATURE_MODULE_SPLIT과 같다.

| 판단 기준 | 선택지 A(단일 모듈) | 선택지 B(api/impl 분할) | 기본값 |
|---|---|---|---|
| 다른 feature가 이 feature의 키·모델을 직접 참조하나 | 참조 없음 | 참조 필요 | 참조가 실제로 생기기 전까지 A |
| 빌드 시간이 이 feature 때문에 눈에 띄게 느린가 | 아니오 | 예(impl 변경이 다른 모듈 재컴파일 유발) | 측정값이 없으면 A |
| 구현을 variant·테스트별로 바꿔 끼우나 | 아니오 | 예 | A |

- 예시:
  ```kotlin
  // Good — 시작 형태
  include(":feature:login")
  // 조건 충족 후에만
  include(":feature:login:api", ":feature:login:impl")
  ```
- 체크: 분할한 feature마다 분할 이유(다른 feature의 직접 참조)가 기록돼 있는가. impl이 다른 feature의 impl을 참조하는가.

### R-10-14 일회성 빌드 로직은 convention plugin으로 만들지 않는다
- 규칙: 한 모듈에서만 쓰는 빌드 설정은 그 모듈의 빌드 파일에 그대로 둔다. 두 번째 사용처가 생길 때 convention plugin으로 올린다.
- 근거: 일회성 로직에는 convention plugin을 만들지 않고 모듈 build 파일에 두라는 NiA 지침 [S53](https://raw.githubusercontent.com/android/nowinandroid/main/build-logic/README.md).
- 예시:
  ```kotlin
  // Good — :app만 쓰는 서명 설정은 :app/build.gradle.kts에
  // Bad — 사용처가 하나인데 convention.android.signing 플러그인을 만듦
  ```
- 체크: 새로 만든 convention plugin의 적용 모듈이 둘 이상인가.
