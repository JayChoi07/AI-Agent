# 30 테스트

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

의존성 추가·소스셋·러너 같은 인프라 세팅은 로컬 공식 스킬 `testing-setup`을 따른다. 이 문서는 무엇을 어디까지 테스트할지만 정한다.

## 테스트 계층

Google은 테스트를 실행 위치(로컬 host-side / 계측 instrumented)와 범위(small·medium·big)로 나눈다 [S30](https://developer.android.com/training/testing/fundamentals). 이 팩은 그 구분만 쓰고 **계층별 비율은 정하지 않는다**(아래 "출처가 침묵하는 것").

| 범위 | 대상 | 소스셋 | 도구 |
|---|---|---|---|
| small | ViewModel·UseCase·Repository·매퍼 | `src/test` | JUnit4 + coroutines-test + Turbine |
| small(스냅숏) | Composable 화면 렌더링 | `src/test` | Roborazzi + Robolectric |
| medium | 화면 조작 + fake 데이터 계층 | `src/androidTest` | Compose Test Rule |
| big | 대표 사용자 플로우 1~2개 | `src/androidTest` | Compose Test Rule |

## 결정 매트릭스 — 스크린샷 도구

| 판단 기준 | 선택지 A: Roborazzi 1.74.0 | 선택지 B: Paparazzi 2.0.0-alpha05 | 기본값 |
|---|---|---|---|
| 안정 릴리스 | 있음 [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md) | 없음(alpha, "pre-AGP 9.0 consumers") [S88](https://raw.githubusercontent.com/cashapp/paparazzi/master/README.md) | A |
| Android 프레임워크·Hilt 주입 | Robolectric으로 동작 [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md) | 미실행 | A |
| 공식 대안 `com.android.compose.screenshot` | 0.0.1-alpha15라 관찰만 [S34](https://developer.android.com/studio/preview/compose-screenshot-testing) | — | A |

## 규칙

### R-30-01 화면 ViewModel마다 로컬 단위 테스트를 둔다
- 규칙: 화면 ViewModel에는 `<ViewModel 이름>Test`를 `src/test`에 만들고 초기 상태·성공 경로·실패 경로를 각각 검증한다. 로직을 Activity·Fragment 같은 프레임워크 클래스에 두지 않아 로컬 테스트로 격리 검증이 가능한 상태를 유지한다.
- 근거: 테스트 가능한 구조란 각 부분을 격리해 검증할 수 있는 구조다 [S30](https://developer.android.com/training/testing/fundamentals). 비즈니스 로직 검증의 기본 위치는 `src/test`의 로컬 테스트이며 의존성은 `testImplementation`으로 넣는다 [S32](https://developer.android.com/training/testing/local-tests).
- 예시:
  ```kotlin
  // Good: src/test/java/.../LoginViewModelTest.kt
  class LoginViewModelTest {
      @get:Rule val mainDispatcherRule = MainDispatcherRule()
      private val repository = FakeLoginRepository()
      private val viewModel = LoginViewModel(repository)

      @Test fun initialState_isIdle() { /* ... */ }
      @Test fun submit_withValidInput_emitsSuccess() { /* ... */ }
      @Test fun submit_whenRepositoryFails_emitsError() { /* ... */ }
  }
  // Bad: ViewModel은 추가하고 테스트는 다음 PR로 미룬다
  ```
- 체크: 새 ViewModel이 들어온 PR에 같은 이름의 Test 클래스가 함께 있는가. 실패 경로를 검증하는 테스트가 하나라도 있는가.

### R-30-02 테스트 더블은 fake가 기본이고 mock은 외부 경계에서만 쓴다
- 규칙: Repository·DataSource·UseCase 같은 우리 코드의 대체물은 손으로 쓴 fake를 쓴다. 모킹 라이브러리는 구현을 우리가 소유하지 않는 외부 경계(플랫폼 API·서드파티 SDK)이거나 호출 여부 자체(인터랙션)를 검증해야 할 때만 쓰고, 그 이유를 테스트에 한 줄로 적는다.
- 근거: 더블 6종 중 "Fakes don't require a mocking framework and are lightweight. They are preferred" [S31](https://developer.android.com/training/testing/fundamentals/test-doubles). 로컬 테스트 문서는 모킹 라이브러리를 선택 의존성으로만 열거한다 [S32](https://developer.android.com/training/testing/local-tests).
- 예시:
  ```kotlin
  // Good
  class FakeUserRepository(private var users: List<User> = emptyList()) : UserRepository {
      override fun observeUsers(): Flow<List<User>> = flowOf(users)
  }
  // Bad: 우리 인터페이스를 모킹해 반환값만 지정한다
  val repository = mock<UserRepository> { on { observeUsers() } doReturn flowOf(emptyList()) }
  ```
- 체크: 모킹 라이브러리를 쓴 테스트의 대상이 외부 경계이거나 인터랙션 검증인가. fake로 대체 가능한데 mock을 쓰지 않았는가.

### R-30-03 Composable Screen마다 스크린샷 테스트를 1개 이상 둔다
- 규칙: 상태를 파라미터로 받는 `*Screen` Composable마다 대표 상태 1개 이상을 캡처하는 `<Screen 이름>ScreenshotTest`를 `src/test`에 둔다. 로딩·비어 있음·에러처럼 눈에 띄게 다른 분기 상태가 있으면 상태마다 1건씩 늘린다.
- 근거: Roborazzi 1.74.0은 JVM 단위 테스트에서 Compose 화면을 캡처한다 [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md). NiA는 `verifyRoborazziDemoDebug`를 CI 게이트에 넣는다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml). Compose 테스트는 시맨틱 기반 상호작용을 다루지만 렌더링 회귀 자체는 잡지 못한다 [S33](https://developer.android.com/develop/ui/compose/testing).
- 예시:
  ```kotlin
  // Good
  @RunWith(AndroidJUnit4::class)
  @GraphicsMode(GraphicsMode.Mode.NATIVE)
  class LoginScreenshotTest {
      @get:Rule val composeRule = createComposeRule()

      @Test fun loginScreen_idle() {
          composeRule.setContent { LoginScreen(uiState = LoginUiState(), onEvent = {}) }
          composeRule.onRoot().captureRoboImage()
      }
  }
  // Bad: Screen을 추가하고 스냅숏은 Preview로만 확인한다
  ```
- 체크: 새 Screen이 들어온 PR에 스크린샷 테스트가 있는가. 화면의 에러·로딩 상태가 캡처 대상에 포함됐는가.

### R-30-04 스크린샷 기준 이미지는 record 태스크로만 갱신하고 저장소에 커밋한다
- 규칙: Roborazzi 테스트 클래스에는 `@GraphicsMode(GraphicsMode.Mode.NATIVE)`와 `@Config(sdk = [35])`를 붙이고 `captureRoboImage()`로 캡처한다. 기준 이미지는 로컬에서 `./gradlew recordRoborazziDebug`로만 갱신해 PNG를 커밋하고, CI는 `verifyRoborazziDebug`로 검증만 한다. 골든 출력 경로는 컨벤션 플러그인이 `<모듈>/src/test/screenshots/`로 옮겨 둔다 — 기본값 `build/outputs/roborazzi`는 커밋 대상이 아니라 CI 체크아웃마다 골든이 없어진다. CI가 기준 이미지를 다시 기록하게 두지 않는다.
- 근거: Roborazzi는 `recordRoborazziDebug`/`verifyRoborazziDebug`/`compareRoborazziDebug` 태스크를 제공하고 Compose 캡처에 NATIVE 그래픽 모드를 요구한다 [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md). NiA CI가 쓰는 것도 verify 쪽이다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml). 실행에는 Robolectric 4.16.1이 필요하다 [S90](https://github.com/robolectric/robolectric/releases). 골든 경로와 렌더링 SDK는 실빌드 실증 2026-09-09 값이다 — 기본 출력 경로로 두면 `verifyRoborazziDebug`가 "The original file was not found"로 100% 실패했고, Robolectric 4.16.1의 SDK 36 이미지는 Java 21을 요구해 JDK 17 확정값에서는 35가 상한이다.
- 예시:
  ```bash
  # Good: 화면을 의도적으로 바꾼 뒤 로컬에서 기준 갱신
  ./gradlew recordRoborazziDebug && git add "**/src/test/screenshots/*.png"
  # Bad: CI 워크플로에 record 태스크를 넣어 매번 기준을 덮어쓴다
  # Bad: 골든을 build/outputs/roborazzi 에 두고 커밋하지 않는다 — CI 에서 verify 가 항상 실패한다
  ```
- 체크: 기준 이미지가 바뀐 PR에 화면을 바꾼 이유가 적혀 있는가. 골든 PNG가 `src/test/screenshots/`에 커밋돼 있는가. CI 로그에 record 태스크가 없는가.

### R-30-05 fake는 테스트 소스셋에만 둔다
- 규칙: fake 구현은 `src/test`에 두고, 여러 모듈이 공유해야 하면 테스트 전용 모듈(`:core:testing`)로 뺀다. 프로덕션 소스셋에 fake를 두거나 프로덕션 코드가 fake를 참조하게 하지 않는다.
- 근거: fake는 테스트용 경량 대체물이며 프로덕션 코드에 포함하지 않는다 [S31](https://developer.android.com/training/testing/fundamentals/test-doubles).
- 예시:
  ```kotlin
  // Good: core/testing/src/main/java/.../FakeUserRepository.kt 를 testImplementation(projects.core.testing) 으로 사용
  // Bad: feature/login/src/main/java/.../FakeUserRepository.kt
  ```
- 체크: `src/main` 아래에 `Fake` 접두 클래스가 있는가. 프로덕션 의존성에 테스트 전용 모듈이 들어가 있는가.

### R-30-06 테스트 클래스 이름은 `<대상>Test`로 통일한다
- 규칙: 테스트 클래스는 대상 이름 뒤에 `Test`를 붙인 PascalCase로 짓고, 테스트 함수는 lowerCamelCase로 `대상_조건_기대` 순서로 쓴다. 백틱을 쓴 문장형 이름은 이 팩이 요구하지도 금지하지도 않는다.
- 근거: Android Kotlin 스타일 가이드는 클래스를 PascalCase 명사, 함수를 lowerCamelCase로 정한다 [S35](https://developer.android.com/kotlin/style-guide). 백틱 이름 허용 여부는 조사한 출처에서 확인하지 못했다.
- 예시:
  ```kotlin
  // Good
  class LoginViewModelTest {
      @Test fun submit_whenPasswordEmpty_showsValidationError() { /* ... */ }
  }
  // Bad
  class TestLogin { @Test fun test1() { /* ... */ } }
  ```
- 체크: 클래스 이름이 대상 + Test인가. 함수 이름만 읽고 무엇을 검증하는지 알 수 있는가.

### R-30-07 `unitTests.returnDefaultValues`를 기본으로 켜지 않는다
- 규칙: `testOptions.unitTests.isReturnDefaultValues`를 기본값 그대로 둔다. 켜야 한다면 대상 모듈 하나로 한정하고 이유를 PR 본문에 남긴다.
- 근거: 이 옵션은 실패해야 할 테스트를 통과시킬 수 있어 최후 수단으로만 쓰라고 안내한다 [S32](https://developer.android.com/training/testing/local-tests).
- 예시:
  ```kotlin
  // Bad: 모든 모듈 공통 convention plugin에서 전역으로 켠다
  testOptions { unitTests.isReturnDefaultValues = true }
  // Good: 프레임워크 클래스 의존을 인터페이스로 감싸 fake로 대체한다
  ```
- 체크: 새로 켠 모듈이 있는가. 켰다면 이유가 적혀 있고 대안(인터페이스 분리)을 검토했는가.

### R-30-08 Flow 검증은 Turbine으로 하고 미소비 이벤트를 남기지 않는다
- 규칙: `Flow`·`StateFlow` 검증은 `flow.test { }` 안에서 `awaitItem()`으로 방출 순서를 그대로 소비하고, 블록을 끝내기 전 `cancelAndIgnoreRemainingEvents()`나 `awaitComplete()`로 정리한다. `first()`·`toList()`로 값 하나만 집어 상태 전이를 건너뛰지 않는다.
- 근거: Turbine 1.2.1이 Flow 테스트 API를 제공한다 [S89](https://raw.githubusercontent.com/cashapp/turbine/trunk/README.md). 버전은 확인 카탈로그 기준이다 [S55](https://raw.githubusercontent.com/android/nowinandroid/main/gradle/libs.versions.toml).
- 예시:
  ```kotlin
  // Good
  viewModel.uiState.test {
      assertEquals(LoginUiState(), awaitItem())
      viewModel.onEvent(LoginEvent.Submit)
      assertTrue(awaitItem().isLoading)
      cancelAndIgnoreRemainingEvents()
  }
  // Bad: 중간 상태를 건너뛰고 최종값만 본다
  assertTrue(viewModel.uiState.first().isSuccess)
  ```
- 체크: 로딩 → 결과 전이가 실제로 검증되는가. 테스트 종료 시 남은 이벤트를 정리했는가.

### R-30-09 디스패처는 주입하고 테스트에서 Main을 교체한다
- 규칙: 상태 홀더와 Repository는 `CoroutineDispatcher`를 생성자로 받는다. ViewModel 테스트는 `Dispatchers.setMain`/`resetMain`을 감싼 `MainDispatcherRule`을 `@get:Rule`로 두고 본문은 `runTest`로 실행한다.
- 근거: 디스패처를 하드코딩하지 않고 주입하면 테스트에서 교체할 수 있다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices). 교체 API는 kotlinx-coroutines-test 1.11.0이 제공한다 [S68](https://github.com/Kotlin/kotlinx.coroutines/releases). `MainDispatcherRule`이라는 이름의 규칙 클래스를 규정한 출처는 없고, 이 팩이 주입 원칙을 JUnit4 규칙으로 구현한 것이다.
- 예시:
  ```kotlin
  // Good
  class MainDispatcherRule(private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) : TestWatcher() {
      override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
      override fun finished(description: Description) = Dispatchers.resetMain()
  }
  // Bad: 테스트 대상이 Dispatchers.IO 를 직접 호출해 교체할 수 없다
  ```
- 체크: 테스트 대상이 디스패처를 직접 참조하는가. ViewModel 테스트에 Main 교체 규칙이 있는가.

### R-30-10 Repository 테스트는 fake DataSource로 조립한다
- 규칙: Repository 단위 테스트는 Remote·Local DataSource 인터페이스의 fake를 주입해 만들고, 실제 네트워크·DB를 쓰지 않는다. 검증 대상은 캐시 우선순위·에러 변환·병합 같은 Repository 자신의 로직으로 한정한다.
- 근거: 더블은 fake 우선 [S31](https://developer.android.com/training/testing/fundamentals/test-doubles), 격리 검증이 테스트 가능한 구조의 목적 [S30](https://developer.android.com/training/testing/fundamentals), 데이터 계층은 Repository가 DataSource를 조합하는 형태다 [S06](https://developer.android.com/topic/architecture/data-layer).
- 예시:
  ```kotlin
  // Good
  val repository = DefaultUserRepository(
      remote = FakeUserRemoteDataSource(users = listOf(user)),
      local = FakeUserLocalDataSource(),
  )
  // Bad: 실제 Retrofit 인스턴스를 만들어 로컬 테스트에서 호출한다
  ```
- 체크: Repository 테스트가 네트워크·DB를 실제로 건드리는가. 검증 대상이 DataSource가 아니라 Repository의 로직인가.

### R-30-11 Compose 상호작용 테스트는 `createComposeRule()`을 기본으로 한다
- 규칙: Compose 테스트는 `createComposeRule()`을 쓰고 Activity가 필요할 때만 `createAndroidComposeRule<T>()`로 올린다. 노드는 시맨틱(`onNodeWithText`·`testTag`)으로 찾는다. `ui-test-junit4`는 계측 테스트면 `androidTestImplementation`, Robolectric 위에서 도는 Roborazzi 스크린샷 테스트면 `testImplementation`으로 추가한다(그 밖의 소스셋에는 넣지 않는다). `ui-test-manifest`는 `debugImplementation`으로만 추가한다.
- 근거: Compose 테스트 문서가 두 규칙의 사용 조건과 의존성 구성을 정하고, 시맨틱이 테스트가 요소를 찾는 수단이라고 설명한다 [S33](https://developer.android.com/develop/ui/compose/testing). 로컬 JVM 테스트로 도는 Roborazzi는 같은 규칙 API를 `test` 소스셋에서 쓰므로 `testImplementation`이 필요하다 — 이 팩의 compose convention plugin이 실제로 그렇게 넣고 스크래치 빌드로 확인했다 [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md).
- 예시:
  ```kotlin
  // Good
  @get:Rule val composeRule = createComposeRule()
  composeRule.onNodeWithText("로그인").performClick()
  // Bad: 실제 Activity가 필요 없는데 createAndroidComposeRule<MainActivity>() 를 쓴다
  ```
- 체크: Activity 없이 되는 테스트에 Activity 규칙을 쓰지 않았는가. `ui-test-manifest`가 debug 구성에만 있는가.

### R-30-12 계측 테스트는 대표 플로우로 한정한다
- 규칙: `src/androidTest`에는 로그인 → 홈처럼 여러 화면을 지나는 대표 플로우 1~2개만 둔다. 분기·에러 조합은 로컬 테스트로 내리고, 계측 테스트를 늘려 커버리지를 채우지 않는다.
- 근거: 범위 구분에서 medium은 둘 이상의 단위 사이 통합, big은 더 큰 플로우다 [S30](https://developer.android.com/training/testing/fundamentals). NiA도 계측 잡을 단위 테스트 잡과 분리해 돌린다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml).
- 예시:
  ```kotlin
  // Good: androidTest 에 로그인 → 홈 도달 1건
  // Bad: 로그인 실패 문구 5종을 계측 테스트로 각각 검증
  ```
- 체크: 새 계측 테스트가 로컬 테스트로 대체 가능한가. 계측 테스트 수가 화면 수만큼 늘어나고 있지 않은가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **피라미드 비율(70/20/10 등)**: [S30](https://developer.android.com/training/testing/fundamentals)에 그림만 있고 수치가 없다. 출처가 비율을 정하지 않으므로 계층별 비율을 규칙화하지 않는다.
- **커버리지 임계값 %**: NiA는 커버리지 리포트를 만들지만 임계값 게이트가 없다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml). 임계값을 정할 근거 출처가 없어 숫자를 쓰지 않는다. 커버리지는 리포트로만 본다.
- **백틱 문장형 테스트 이름**: 허용 문구를 조사 출처에서 확인하지 못해 R-30-06에서 판단을 열어 뒀다.
