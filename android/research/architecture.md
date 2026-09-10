# 앱 아키텍처 계층·모듈화·convention plugin·의존성 주입·데이터/도메인 계층 조사 노트
조사일: 2026-09-09

## 출처

| # | 조직 | 문서명 | URL | 문서 종류 | 최종 갱신 |
|---|---|---|---|---|---|
| S1 | Google | Guide to app architecture | https://developer.android.com/topic/architecture | 공식 가이드 | 2026-04-14 |
| S2 | Google | Recommendations for Android architecture | https://developer.android.com/topic/architecture/recommendations | 공식 가이드 | 2026-04-26 |
| S3 | Google | Guide to Android app modularization | https://developer.android.com/topic/modularization | 공식 가이드 | 2026-03-05 |
| S4 | Google | Common modularization patterns | https://developer.android.com/topic/modularization/patterns | 공식 가이드 | 2026-03-05 |
| S5 | Google | Dependency injection with Hilt | https://developer.android.com/training/dependency-injection/hilt-android | 공식 가이드 | 2026-09-01 |
| S6 | Google | Data layer | https://developer.android.com/topic/architecture/data-layer | 공식 가이드 | 2026-04-29 |
| S7 | Google | Domain layer | https://developer.android.com/topic/architecture/domain-layer | 공식 가이드 | 2026-03-05 |
| S8 | Google | UI layer | https://developer.android.com/topic/architecture/ui-layer | 공식 가이드 | 2026-07-21 |
| S9 | Google | Migrate your build to version catalogs | https://developer.android.com/build/migrate-to-catalogs | 공식 가이드 | 2026-02-26 |
| S10 | Google/Android | Now in Android — ArchitectureLearningJourney.md | https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md | 오픈소스 docs | main 브랜치 |
| S11 | Google/Android | Now in Android — ModularizationLearningJourney.md | https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md | 오픈소스 docs | main 브랜치 |
| S12 | Google/Android | Now in Android — build-logic/README.md | https://raw.githubusercontent.com/android/nowinandroid/main/build-logic/README.md | 오픈소스 docs | main 브랜치 |
| S13 | Google/Android | Now in Android — README.md | https://raw.githubusercontent.com/android/nowinandroid/main/README.md | 오픈소스 README | main 브랜치 |
| S14 | Gradle | Sharing build logic between subprojects | https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html | 공식 가이드 | current |
| S15 | Uber | RIBs — README | https://raw.githubusercontent.com/uber/RIBs/main/README.md | 오픈소스 README | 0.16.6 |
| S16 | Mobile Native Foundation | Store5 — Concepts | https://store.mobilenativefoundation.org/docs/concepts/store5/ | 오픈소스 문서 | 확인 불가 |
| S17 | Mobile Native Foundation | Store — README | https://raw.githubusercontent.com/MobileNativeFoundation/Store/main/README.md | 오픈소스 README | main 브랜치 |
| S18 | Square | Anvil — README | https://raw.githubusercontent.com/square/anvil/main/README.md | 오픈소스 README | deprecated 공지 |
| S19 | Zac Sweers | Metro — Documentation | https://zacsweers.github.io/metro/latest/ | 오픈소스 문서 | latest |
| S20 | Slack | Circuit — Documentation | https://slackhq.github.io/circuit/ | 오픈소스 문서 | 확인 불가 |
| S21 | Trade Republic | State of Android at TR — 2024 edition | https://traderepublic.substack.com/p/state-of-android-at-tr-2024-edition | 기술 블로그 | 2025-03-10 |
| S22 | Dropbox | Modernizing our Android build system: Part II | https://dropbox.tech/mobile/modernizing-our-android-build-system-part-ii-the-execution | 기술 블로그 | 2019-10-30 |

## 핵심 규칙 (출처별)

### S1 Google — Guide to app architecture
- 최우선 원칙은 관심사 분리. "separating your app into methods, classes, files, packages, modules and layers that have clearly defined responsibilities and boundaries" [S1]
- Activity에 모든 코드를 넣는 것은 대표적 실수다. "It's a common mistake to write all your code in an Activity" [S1]
- UI는 데이터 모델, 되도록 영속 모델에서 구동한다. "drive your UI from data models, preferably persistent models" [S1]
- 새 데이터 타입마다 SSOT를 지정하고 SSOT만 변경 권한을 가진다. "only the SSOT can modify or mutate it" [S1]
- UDF: 상태는 위에서 아래로, 이벤트는 아래에서 위로 흐른다 [S1]
- Android 프레임워크 의존을 줄이고 app component에 데이터를 저장하지 않는다. "Don't store data in app components" [S1]
- 계층 의존 방향은 UI → (Domain) → Data 단방향. Data source는 다른 계층에 의존하지 않는다 [S1]
- 모듈 경계와 노출 범위를 명확히 한다. "Expose as little as possible from each module" [S1]

### S2 Google — Recommendations for Android architecture
- Strongly recommended: 데이터 계층과 UI 계층을 명확히 분리해 정의한다 [S2]
- Strongly recommended: 데이터 계층 노출은 repository를 통해서만 한다 [S2]
- Strongly recommended: 계층 간 통신은 coroutines + Flow로 한다 [S2]
- Recommended in big apps: use case를 갖는 domain 계층을 둔다 (대형 앱 한정) [S2]
- Strongly recommended: UDF 준수, AAC ViewModel 사용, `collectAsStateWithLifecycle`로 수집 [S2]
- Strongly recommended: ViewModel에서 UI로 이벤트를 보내지 않는다 [S2]
- Strongly recommended: ViewModel은 화면 단위로만 두고, 재사용 UI 컴포넌트에는 plain state holder를 쓴다 [S2]
- Recommended: `AndroidViewModel` 사용 금지, `uiState` 단일 `StateFlow` 프로퍼티 노출 [S2]
- Strongly recommended: 생성자 주입 기반 DI, 필요 시에만 컨테이너에 스코프 지정 [S2]
- Recommended: 복잡한 프로젝트(다화면·WorkManager·scoped ViewModel)에서는 Hilt 사용 [S2]
- Strongly recommended: 테스트 더블은 mock보다 fake를 선호 [S2]
- Recommended: 복잡한 앱은 계층별 모델을 분리(모델 per layer) [S2]
- Optional 네이밍: 메서드는 동사구, 프로퍼티는 명사구, 스트림은 `get{Model}Stream()`, 기본 구현은 `Default` 접두, fake는 `Fake` 접두 [S2]

### S3 Google — Guide to Android app modularization
- 모듈은 "loosely coupled and self contained part"이며 각 모듈은 독립적이고 목적이 분명해야 한다 [S3]
- 캡슐화 원칙: "Each part of your code should have the smallest possible amount of knowledge about other parts." [S3]
- 모듈화의 고유 이점은 재사용성·엄격한 가시성 제어·Play Feature Delivery 대응 세 가지 [S3]
- 공개 인터페이스 외에는 `internal`/`private`로 막는다 [S3]
- 과도한 세분화 경고: "Every module brings a certain amount of overhead" → 유지보수 어려운 코드베이스가 된다 [S3]
- 과도한 통합 경고: 결국 "yet another monolith"가 된다 [S3]
- 소규모 프로젝트에는 모듈화가 정당화되지 않는다 [S3]

### S4 Google — Common modularization patterns
- 모듈 종류는 app / feature / data / common(core) / test로 나눈다 [S4]
- data 모듈 3책임: 도메인의 데이터·비즈니스 로직 캡슐화, repository만 외부 API로 노출, 데이터 소스 은닉 [S4]
- feature 모듈은 화면(또는 밀접한 화면군) 단위이고 data 모듈에만 의존한다. 기능 간 직접 의존 금지 [S4]
- feature 간 통신은 app 모듈을 mediator로 삼아 네비게이션으로 처리한다 [S4]
- 네비게이션 인자로 객체를 넘기지 않는다. "use simple ids that features can use to access and load desired resources from the data layer" [S4]
- app 모듈이 진입점이며 root navigation을 제공한다 [S4]
- 저결합: "Modules shouldn't have knowledge of the inner workings of other modules." 고응집: 명확히 정의된 책임 [S4]
- 공개 표면은 최소화하고 "Prefer `implementation` over `api`" (빌드 시간에도 유리) [S4]
- 의존 역전: 추상 모듈 + 구현 모듈 분리, app 모듈이 variant별 구현을 주입(`releaseImplementation`/`debugImplementation`) [S4]
- Android 모듈보다 순수 Kotlin/Java 모듈을 선호한다 (Android 모듈은 오버헤드) [S4]
- 버전 카탈로그와 convention plugin으로 빌드 설정을 공유한다 [S4]

### S5 Google — Dependency injection with Hilt
- "All apps that use Hilt must contain an `Application` class that is annotated with `@HiltAndroidApp`." [S5]
- Hilt 주입 대상 Android 클래스에는 `@AndroidEntryPoint`를 붙인다. Activity는 `ComponentActivity` 상속만 지원 [S5]
- Compose 앱은 루트 `ComponentActivity` 하나만 `@AndroidEntryPoint`로 두고 단일 DI 진입점으로 쓴다 [S5]
- "Fields injected by Hilt cannot be private." — 필드 주입 시 private 금지 [S5]
- 모듈에는 `@Module` + `@InstallIn`을 반드시 함께 붙인다 [S5]
- 인터페이스 바인딩은 `@Binds`, 소유하지 않은 타입/빌더 필요 타입은 `@Provides` [S5]
- 컴포넌트/스코프: SingletonComponent-`@Singleton`, ActivityRetainedComponent-`@ActivityRetainedScoped`, ViewModelComponent-`@ViewModelScoped`, ActivityComponent-`@ActivityScoped`, ServiceComponent-`@ServiceScoped` [S5]
- 스코프 남용 경고: "Scoping a binding to a component can be costly" — 컴포넌트 소멸까지 메모리에 남는다 [S5]
- ViewModel은 `@HiltViewModel` + `@Inject constructor`, Compose에서는 `hiltViewModel()` [S5]
- 동일 타입 다중 바인딩은 `@Qualifier`로 구분하고, 한 타입에 qualifier를 도입하면 모든 제공 경로에 붙인다 [S5]
- Hilt 미지원 클래스(ContentProvider 등)는 `@EntryPoint` + `EntryPointAccessors`로 접근 [S5]

### S6 Google — Data layer
- 네이밍: repository는 `[DataType]Repository`, 데이터 소스는 `[DataType][SourceType]DataSource` (Remote/Local 등 일반명 사용) [S6]
- "The entry points to the data layer are always the repository classes." — 다른 계층은 데이터 소스에 직접 접근 금지 [S6]
- 데이터 소스는 파일·네트워크·DB 중 하나의 소스만 담당한다 [S6]
- 노출 데이터는 불변이어야 한다. "The data exposed by this layer should be immutable" [S6]
- 일회성 작업은 suspend 함수, 지속 관찰은 `Flow`로 노출 [S6]
- repository·데이터 소스는 main-safe여야 하며 스스로 적절한 디스패처로 이동한다 [S6]
- 소스별 모델과 도메인 모델을 분리한다 (메모리 절약·경계 분리) [S6]
- 에러는 try/catch와 Flow `catch`로 처리하고 데이터 계층 전용 예외를 노출할 수 있다 [S6]
- 인메모리 캐시는 `Mutex` 등 스레드 안전 수단으로 보호한다 [S6]
- 영속화 선택: 대량 질의형은 Room, 소형 key-value는 DataStore, 대형 객체는 File [S6]
- 프로세스 사망을 넘겨야 하는 business-oriented 작업은 WorkManager로 [S6]
- 테스트는 fake 데이터 소스 주입, 통합 테스트는 in-memory Room + MockWebServer/WireMock [S6]

### S7 Google — Domain layer
- use case 네이밍: `[현재형 동사] + [명사] + UseCase` (예: `GetLatestNewsWithAuthorsUseCase`) [S7]
- use case는 상태를 갖지 않는다. "should not contain mutable data" [S7]
- use case 하나당 공개 함수 하나. "Each use case should only have responsibility over a single functionality" [S7]
- 호출은 `operator fun invoke`로 함수처럼 [S7]
- use case는 main-safe여야 하며 장시간 작업은 스스로 디스패처를 옮긴다 [S7]
- 의존 허용: repository·다른 use case·디스패처. UI 계층(ViewModel/Activity) 의존은 금지 [S7]
- 의존 방향은 Data → Domain → UI [S7]
- use case는 자체 라이프사이클이 없고 사용하는 클래스에 스코프된다 [S7]
- 단순 위임뿐이면 도메인 계층을 만들지 않는다 (복잡도 대비 이득 없음) [S7]

### S8 Google — UI layer
- UI 상태는 불변 data class로 정의하고 UI에서 직접 수정하지 않는다. "Only sources or owners of data should be responsible for updating the data they expose." [S8]
- 화면 단위 상태 홀더는 ViewModel, 요소 단위는 plain 클래스/컴포저블 상태 [S8]
- 상호 연관된 상태는 단일 UI state 객체로 묶는다 [S8]
- UI는 소유하지 않은 데이터를 변경하지 않고 ViewModel에 위임한다 [S8]
- ViewModel의 모든 작업은 main-safe여야 한다 [S8]

### S9 Google — Version catalogs
- 카탈로그는 루트의 `gradle/libs.versions.toml`에 두고 기본 파일명을 바꾸지 않는다 [S9]
- `[versions]`/`[libraries]`/`[plugins]` 섹션으로 구성하고 type-safe accessor(`libs.androidx.ktx`)로 참조 [S9]
- 별칭은 kebab-case로 작성해 코드 완성 지원을 받는다 [S9]
- 목적은 의존성·버전의 single source of truth 확보 [S9]

### S10 Now in Android — Architecture Learning Journey
- "Higher layers react to changes in lower layers. Events flow down. Data flows up." [S10]
- 3계층(data / domain / UI) 구성, 데이터 계층이 앱 전체의 source of truth [S10]
- "Repositories are the public API for other layers, they provide the only way to access the app data" [S10]
- 오프라인 우선: 읽기는 로컬 저장소에서 수행 [S10]
- use case는 `operator fun invoke` 단일 메서드 [S10]
- UI 상태는 sealed interface + 불변 data class 계층으로 모델링하고 `stateIn`으로 hot flow 변환 [S10]
- 동기화는 WorkManager(`SyncWorker`) + 지수 백오프 [S10]

### S11 Now in Android — Modularization Learning Journey
- feature는 `feature:*:api` / `feature:*:impl`로 분리한다 [S11]
- api 모듈은 다른 feature의 api·impl에 의존하지 않는다. impl 모듈은 다른 feature의 api 모듈에만 의존한다 [S11]
- core 모듈은 feature·app 모듈에 의존하지 않는다 [S11]
- 배치 기준: "If a class is needed only by one feature module, it should remain within that module." 아니면 core로 [S11]
- core 모듈 예: data, database, model, network, ui, designsystem, common, testing, datastore [S11]
- 데이터 계층이 커지면 repository/데이터 소스를 별도 모듈로 더 쪼개는 것을 고려 [S11]
- 정답이 하나는 아니며 사전 계획이 필수 [S11]

### S12·S13 Now in Android — build-logic / README
- convention plugin은 `buildSrc`가 아니라 included build(`build-logic`)에 둔다 [S12]
- plugin은 "additive and composable"하게 설계해 모듈이 필요한 것만 적용한다 [S12]
- plugin id는 `<project>.android.<component>[.<tech>]` 형태 (예: `nowinandroid.android.library.compose`) [S12]
- 일회성 로직에는 convention plugin을 만들지 않고 모듈 build 파일에 직접 둔다 [S12]
- 데이터 계층 컴포넌트는 대부분 인터페이스로 정의하고 구현을 바인딩한다 [S13]
- 모킹 라이브러리를 쓰지 않고 동일 인터페이스를 구현한 테스트 더블을 사용한다 [S13]

### S14 Gradle — Sharing build logic between subprojects
- convention plugin id는 스크립트 파일명에서 확장자를 뺀 값 [S14]
- `subprojects {}` / `allprojects {}`를 통한 cross-project configuration은 잘못된 공유 방식이다 [S14]
- 이유: 주입된 로직이 하위 프로젝트 빌드 스크립트에서 보이지 않고, 구성 시점 결합이 생겨 configuration-on-demand 같은 최적화를 막는다 [S14]

### S15 Uber — RIBs
- RIB = Router / Interactor / Builder. RIB은 뷰가 없어도 되며 앱 계층 구조는 뷰 트리가 아니라 비즈니스 로직이 결정한다 [S15]
- iOS/Android 아키텍처 공유를 목표로 한다 [S15]
- "has proven to scale to hundreds of engineers working on the same codebase" — 대규모 조직·중첩 상태가 많은 앱 대상 [S15]

### S16·S17 Store5
- Store는 "typed repository that mediates data flow between network, memory cache, and local storage" [S16]
- 구성요소: Fetcher(원격 조회), SourceOfTruth(단일 권위 로컬 소스), Converter(네트워크/DB/도메인 모델 변환), Updater(로컬 변경의 원격 반영), Validator(로컬 데이터 유효성), Bookkeeper(동기화 실패 기록) [S16]
- 동시 요청 중복 제거와 오프라인 우선 동작을 제공한다 [S16]

### S18 Square — Anvil
- `@ContributesTo`/`@ContributesBinding`/`@MergeComponent`로 Dagger 모듈 병합 보일러플레이트를 제거한다 [S18]
- 상태 공지: "Anvil is deprecated and will only be supported for K1 going forward, and development will end when K2 becomes a requirement." [S18]
- 이 공지는 대체 라이브러리를 지목하지 않는다. README에 별도 `Hilt` 섹션이 있으나 Hilt로 마이그레이션하지 않은 이유를 설명하는 내용이고 권장 대안을 명시하지 않는다 [S18]

### S19 Zac Sweers — Metro
- "a compile-time dependency injection framework that draws heavy inspiration from Dagger, Anvil, and Kotlin-Inject" [S19]
- KMP(JVM/JS/native) 지원. 단 native·Wasm에서 contribution hint 생성은 Kotlin 2.3.20-Beta1 이전 미지원 [S19]
- 안정성 경고: "Kotlin compiler plugins are not a stable API!" [S19]

### S20 Slack — Circuit
- Presenter와 UI는 서로 직접 접근할 수 없다. "They can only communicate through state and event emissions." [S20]
- Presenter/UI 모두 컴포저블 함수 하나이며 Screen이 둘을 잇는 키 [S20]
- 상태는 아래로, 이벤트는 `eventSink`로 위로 흐른다 [S20]
- "Circuit is used in production at Slack and ready for general use" [S20]

### S21 Trade Republic — State of Android at TR (2025-03-10)
- feature를 `:<feature>:public-api` / `:<feature>:impl`로 분리 (impl 안에 presentation·domain·data) [S21]
- `buildSrc` → 프로젝트별 convention plugin 이관. "changes in `buildSrc` affected all classpaths, often requiring unnecessary rebuilds" [S21]
- DI는 Hilt 대신 Anvil 선택. "chose Anvil due to its superior build speed performance and greater flexibility" [S21]
- 아키텍처는 Jetpack ViewModel 기반 MVVM, RxJava2 → Coroutines/Flow 이관 [S21]
- 원격 캐시 포함 로컬 P90 클린 빌드 22.7 → 13.94분 [S21]

### S22 Dropbox — Modernizing our Android build system Part II (2019-10-30)
- 계층 순서를 product → core → base → external 4단으로 강제하고 검증 태스크로 어긴 의존을 잡는다 [S22]
- 검증기를 Python에서 Kotlin(buildSrc)으로 옮겨 Gradle UP-TO-DATE 캐싱을 얻었다 [S22]
- 모듈 75개 이관, 증분 코드 생성 최적화로 P50 로컬 빌드 20% 단축 [S22]

## 출처 간 충돌

| 주제 | 입장 A (출처) | 입장 B (출처) | 팩 추천과 근거 |
|---|---|---|---|
| DI 프레임워크 | Hilt를 표준으로 권장 [S2][S5] | Anvil이 빌드 속도·유연성에서 낫다 [S21] | Hilt. 권장 근거는 Google 문서(S2·S5)뿐이며, S18은 Anvil이 deprecated라는 사실만 제공한다(대체 라이브러리 지목 없음). S21은 2024년 결정이고 Metro[S19]는 컴파일러 플러그인 불안정 경고가 있어 신규 표준으로 부적합 |
| convention plugin 위치 | `buildSrc` 사용을 안내 [S14][S22] | included build(`build-logic`)가 `buildSrc` 함정을 피한다 [S12][S21] | included build. S21이 `buildSrc` 변경이 전체 classpath를 무효화한다고 실측, S12도 같은 이유를 든다 |
| domain 계층 필요성 | 대형 앱에서만 권장(선택) [S2][S7] | 3계층 중 하나로 상시 배치 [S10] | 선택적. 단순 위임 use case는 만들지 않는다는 S7 기준을 채택하고, 2개 이상 ViewModel이 공유하거나 repository 2개 이상을 조합할 때만 추가 |
| feature 모듈 분할 | feature 단일 모듈, data 모듈에 의존 [S4] | `api`/`impl` 2모듈로 분할 [S11][S21] | 기본은 단일 feature 모듈, feature 간 네비게이션 참조가 필요해지면 `api`/`impl` 분할. S3의 과세분화 경고 반영 |
| 화면 상태 홀더 | AAC ViewModel이 화면 단위 상태 홀더 [S2][S8] | 컴포저블 Presenter가 상태를 만든다 [S20] | ViewModel. 공식 권장이며 S20은 Slack 사내 프레임워크 의존을 추가로 요구 |
| 앱 계층 구조 결정 주체 | 화면(뷰) 단위로 feature를 나눈다 [S4] | 비즈니스 로직 트리가 계층을 결정한다 [S15] | 화면 단위. RIBs는 "hundreds of engineers" 규모 전제라 그린필드 기본값으로 과하다 |
| 데이터 계층 구현 | 직접 작성한 repository + Room/DataStore [S6] | Store5가 fetcher/SoT/updater를 표준화 [S16] | 직접 작성 repository를 기본값으로. Store5는 오프라인 동기화 요구가 명확할 때 선택지로만 기재 |

## 팩 규칙 초안

| 후보 ID | 규칙 한 줄 | 근거 출처 | 확신도 |
|---|---|---|---|
| R-00-01 | 관심사 분리를 최우선 원칙으로 삼고 Activity/Fragment에 비즈니스 로직을 두지 않는다 | S1 | 높음 |
| R-00-02 | 모든 데이터 타입에 단일 진실 공급원(SSOT)을 지정하고 SSOT만 변경한다 | S1, S10 | 높음 |
| R-00-03 | 단방향 데이터 흐름을 지킨다. 상태는 아래로, 이벤트는 위로 흐른다 | S1, S8, S10, S20 | 높음 |
| R-00-04 | 계층 간 노출 데이터는 불변 타입으로 한다 | S6, S8 | 높음 |
| R-00-05 | Android 프레임워크 타입 의존을 app component로 한정하고 앱 데이터를 그 안에 저장하지 않는다 | S1 | 높음 |
| R-00-06 | 각 타입이 자신의 동시성 정책을 책임지고 공개 API는 main-safe여야 한다 | S1, S6, S7, S8 | 높음 |
| R-00-07 | 공개 표면을 최소화하고 나머지는 `internal`/`private`로 막는다 | S1, S3, S4 | 높음 |
| R-00-08 | 테스트 더블은 mock 대신 인터페이스를 구현한 fake를 쓴다 | S2, S13 | 높음 |
| R-10-01 | 모듈은 app / feature / core(common) / data / test 유형으로만 만든다 | S4, S11 | 높음 |
| R-10-02 | feature 모듈끼리 직접 의존하지 않는다. 연결은 app 모듈이 네비게이션으로 중재한다 | S4, S11 | 높음 |
| R-10-03 | core 모듈은 feature·app 모듈에 의존하지 않는다 | S11 | 높음 |
| R-10-04 | 한 feature만 쓰는 코드는 그 모듈에 두고, 둘 이상이 쓰면 core로 승격한다 | S11 | 높음 |
| R-10-05 | 모듈 의존은 기본 `implementation`으로 선언하고 `api`는 전이 노출이 의도된 경우로 제한한다 | S4 | 높음 |
| R-10-06 | UI·엔트리포인트가 아닌 모듈은 순수 Kotlin/JVM 모듈로 만든다 | S4 | 중간 |
| R-10-07 | 네비게이션 인자는 원시 ID만 전달하고 도메인 객체를 넘기지 않는다 | S4 | 높음 |
| R-10-08 | 루트 네비게이션과 variant별 구현 바인딩은 app 모듈이 소유한다 | S4 | 높음 |
| R-10-09 | 모듈 수를 늘리기 전에 오버헤드를 비교한다. 소규모 앱은 모듈화를 미룬다 | S3 | 중간 |
| R-10-10 | 구현 교체가 필요한 영역은 추상 모듈과 구현 모듈로 분리해 의존 역전을 적용한다 | S4 | 중간 |
| R-10-11 | feature 간 참조가 필요해지면 `feature:x:api` / `feature:x:impl`로 분할하고 impl은 다른 feature의 api에만 의존한다 | S11, S21 | 중간 |
| R-10-12 | 모든 의존성과 플러그인 버전은 `gradle/libs.versions.toml` 버전 카탈로그에서 관리한다 | S9, S4 | 높음 |
| R-10-13 | 버전 카탈로그 별칭은 kebab-case로 작성한다 | S9 | 중간 |
| R-10-14 | 공통 빌드 설정은 `build-logic` included build의 convention plugin으로 공유하고 `buildSrc`에 두지 않는다 | S12, S14, S21 | 높음 |
| R-10-15 | convention plugin은 합성 가능하게 쪼개고 id는 `<project>.android.<component>[.<tech>]` 규칙을 따른다 | S12 | 중간 |
| R-10-16 | 일회성 빌드 로직은 convention plugin으로 만들지 않고 해당 모듈 build 파일에 둔다 | S12 | 중간 |
| R-10-17 | `subprojects {}` / `allprojects {}` 로 하위 프로젝트에 설정을 주입하지 않는다 | S14 | 높음 |
| R-10-18 | 모듈 계층 순서(app → feature → core/data → external)를 정의하고 CI 검증 태스크로 위반을 차단한다 | S22, S4 | 중간 |
| R-11-01 | 앱은 UI 계층과 데이터 계층을 반드시 분리하고, 필요 시 그 사이에 도메인 계층을 둔다 | S2, S1 | 높음 |
| R-11-02 | 의존 방향은 UI → Domain → Data 단방향이며 역방향 의존을 금지한다 | S1, S7 | 높음 |
| R-11-03 | 계층 간 통신은 coroutines와 Flow로 한다 | S2, S10 | 높음 |
| R-11-04 | 화면 단위 상태 홀더는 AAC ViewModel로 하고 재사용 컴포넌트는 plain state holder를 쓴다 | S2, S8 | 높음 |
| R-11-05 | ViewModel은 단일 `uiState: StateFlow`를 노출하고 관련 상태를 하나의 불변 객체로 묶는다 | S2, S8, S10 | 높음 |
| R-11-06 | ViewModel에서 UI로 일회성 이벤트를 push하지 않는다 | S2 | 중간 |
| R-11-07 | `AndroidViewModel`을 사용하지 않고 ViewModel을 Android 라이프사이클에서 독립시킨다 | S2 | 높음 |
| R-11-08 | UI 상태 수집은 `collectAsStateWithLifecycle` 등 라이프사이클 인지 API로 한다 | S2 | 높음 |
| R-11-09 | 복잡한 앱은 계층별 모델을 분리하고 경계에서 매핑한다 | S2, S6 | 중간 |
| R-11-10 | 네이밍 규칙: 메서드는 동사구, 프로퍼티는 명사구, 스트림은 `get{Model}Stream()`, 기본 구현은 `Default` 접두, fake는 `Fake` 접두 | S2 | 중간 |
| R-14-01 | Application 클래스에 `@HiltAndroidApp`을 붙이고 Hilt를 표준 DI로 사용한다 | S5, S2 | 높음 |
| R-14-02 | 의존성은 생성자 주입을 기본으로 하고 필드 주입은 프레임워크 클래스에 한정한다 | S2, S5 | 높음 |
| R-14-03 | Hilt 주입 대상 Android 클래스에 `@AndroidEntryPoint`를 붙이고, Compose 앱은 루트 ComponentActivity 하나만 진입점으로 둔다 | S5 | 높음 |
| R-14-04 | 모듈에는 `@Module`과 `@InstallIn`을 항상 함께 선언한다 | S5 | 높음 |
| R-14-05 | 인터페이스 바인딩은 `@Binds`, 외부 타입·빌더 필요 타입은 `@Provides`로 제공한다 | S5 | 높음 |
| R-14-06 | 스코프는 필요한 최소 컴포넌트에만 지정한다. 무분별한 `@Singleton`을 금지한다 | S5 | 높음 |
| R-14-07 | ViewModel은 `@HiltViewModel` + `@Inject constructor`로 정의하고 Compose에서 `hiltViewModel()`로 얻는다 | S5 | 높음 |
| R-14-08 | 동일 타입 다중 바인딩은 `@Qualifier`로 구분하고, 도입 시 모든 제공 경로에 적용한다 | S5 | 중간 |
| R-14-09 | Hilt가 지원하지 않는 클래스는 `@EntryPoint` + `EntryPointAccessors`로만 접근한다 | S5 | 높음 |
| R-14-10 | Hilt로 주입되는 필드를 private으로 선언하지 않는다 | S5 | 높음 |
| R-14-11 | CoroutineDispatcher는 하드코딩하지 않고 qualifier로 주입해 테스트에서 교체 가능하게 한다 | S5, S6, S7 | 중간 |
| R-15-01 | 데이터 계층의 유일한 진입점은 repository이며 다른 계층은 데이터 소스에 직접 접근하지 않는다 | S6, S10 | 높음 |
| R-15-02 | repository는 `[DataType]Repository`, 데이터 소스는 `[DataType][Remote\|Local]DataSource`로 명명한다 | S6 | 높음 |
| R-15-03 | 데이터 소스 하나는 하나의 소스(네트워크·DB·파일)만 담당한다 | S6 | 높음 |
| R-15-04 | 일회성 작업은 suspend 함수로, 지속 관찰은 `Flow`로 노출한다 | S6, S2 | 높음 |
| R-15-05 | repository와 데이터 소스는 main-safe해야 하며 스스로 `withContext`로 디스패처를 옮긴다 | S6 | 높음 |
| R-15-06 | 데이터 계층 인터페이스와 `Default*` 구현을 분리하고 DI로 바인딩한다 | S13, S2 | 중간 |
| R-15-07 | 오프라인 우선을 기본으로 하고 읽기의 SSOT를 로컬 저장소로 둔다 | S10, S6 | 중간 |
| R-15-08 | 저장소 선택: 질의형 대량 데이터는 Room, 소형 key-value는 DataStore, 대형 객체는 File | S6 | 높음 |
| R-15-09 | 프로세스 사망을 넘겨야 하는 작업은 WorkManager로 처리한다 | S6, S10 | 높음 |
| R-15-10 | 인메모리 캐시는 `Mutex` 등으로 스레드 안전하게 보호한다 | S6 | 중간 |
| R-15-11 | 에러는 try/catch와 Flow `catch`로 계층 경계에서 처리하고 도메인 예외로 변환한다 | S6 | 중간 |
| R-15-12 | 데이터 계층 테스트는 fake 데이터 소스, 통합 테스트는 in-memory Room과 MockWebServer로 구성한다 | S6 | 높음 |
| R-16-01 | 도메인 계층은 선택 사항이며 단순 위임만 하는 use case는 만들지 않는다 | S7, S2 | 높음 |
| R-16-02 | use case는 `[현재형 동사][명사]UseCase`로 명명한다 | S7 | 높음 |
| R-16-03 | use case는 공개 함수 하나만 가지며 `operator fun invoke`로 노출한다 | S7, S10 | 높음 |
| R-16-04 | use case는 상태를 갖지 않는다. 가변 데이터를 보관하지 않는다 | S7 | 높음 |
| R-16-05 | use case는 repository와 다른 use case에만 의존하고 UI 계층 타입에 의존하지 않는다 | S7 | 높음 |
| R-16-06 | use case는 main-safe해야 하며 장시간 작업은 주입된 디스패처로 옮긴다 | S7 | 높음 |
| R-16-07 | 여러 ViewModel이 공유하거나 repository 2개 이상을 조합하는 로직일 때 use case로 승격한다 | S7, S2 | 중간 |

## 미확인·접근 불가

- Reddit 엔지니어링 블로그의 Android 모듈화·아키텍처 글: `redditinc.com` 도메인이 크롤러 차단이고 `r/RedditEng` 글도 검색 결과에서 원문 URL을 확보하지 못해 읽지 못함. 내용 인용 없음
- Netflix 기술 블로그의 2023년 이후 Android 아키텍처·모듈화 글: 검색으로 해당 글을 특정하지 못함. Netflix 관련 서술 없음
- Dropbox의 2023년 이후 모듈화 글: 확인된 최신 관련 글이 2019년 Part II라 그대로 사용. 2023년 이후 글은 미확인
- Uber RIBs 위키(https://github.com/uber/RIBs/wiki): 별도 위키 페이지는 미조회. README 범위만 반영
- Store5 README(S17)는 개념 설명이 없어 개념 페이지(S16)로 대체. Store의 `MemoryPolicy`·`StoreReadResponse.origin` 세부 API는 미확인
- Metro의 개별 애노테이션(`@DependencyGraph`, `@ContributesBinding` 등) 상세와 정식 버전 번호는 홈페이지에서 확인 불가
- Slack Circuit 문서의 버전 번호 미확인
- Now in Android `build-logic/convention` 소스 트리(plugin 클래스 목록)는 README 범위까지만 확인
