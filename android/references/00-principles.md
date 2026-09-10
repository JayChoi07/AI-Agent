# 00 원칙과 우선순위

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

이 파일의 규칙 ID는 `R-00-NN`이다. 여기에는 계층·상태·모듈 어디에나 걸리는 원칙만 둔다.
계층 의존의 세부(11), UiState 형태와 상태 홀더(12), 모듈 분할(10), Compose 컴포저블(17)처럼
소유 파일이 정해진 주제는 이 파일에서 다시 조문화하지 않고 참조만 한다.

## 규칙 우선순위 (충돌 시 위가 이긴다)

1. 프로젝트 지침 파일(`CLAUDE.md` / `AGENTS.md`)
2. 강제 장치(Konsist·detekt·ktlint)가 기계로 판정하는 규칙
3. 이 팩의 `references` 규칙
4. 로컬 공식 Android 스킬이 제시하는 구현 방법
5. 일반 관행

근거: 스타일은 스타일 가이드가 최종 권위이고, 규칙이 침묵하면 기존 코드베이스 일관성을 따른다
[S39](https://google.github.io/eng-practices/review/reviewer/standard.html).
집행 순서는 린터(기계 판정) → CI 게이트 → 에이전트 주입(판단이 필요한 규칙) 순으로 둔다
[S112](https://techblog.lycorp.co.jp/ko/using-ast-to-verify-the-code-after-code-linting),
[S114](https://toss.tech/article/52631).

### R-00-01 단방향 데이터 흐름(UDF)을 모든 화면에 적용한다

- 규칙: 상태는 위에서 아래로, 이벤트는 아래에서 위로만 흐른다. UI는 상태를 그리기만 하고 변경은 상태 홀더가 한다.
- 근거: 상태는 아래로 이벤트는 위로 흐르는 것이 앱 아키텍처 가이드의 UDF 정의다 [S01](https://developer.android.com/topic/architecture). UI 계층 가이드는 "데이터를 노출하는 소유자만 그 데이터를 갱신할 책임을 진다"고 못 박고, UI가 소유하지 않은 데이터를 직접 바꾸지 말고 상태 홀더에 위임하라고 한다 [S08](https://developer.android.com/topic/architecture/ui-layer).
- 예시:
  ```kotlin
  // Good: 상태는 파라미터로 내려가고 이벤트는 람다로 올라간다
  @Composable
  fun LoginScreen(uiState: LoginUiState, onEvent: (LoginEvent) -> Unit)

  // Bad: Composable이 ViewModel 내부 필드를 직접 바꾼다
  @Composable
  fun LoginScreen(viewModel: LoginViewModel) {
      viewModel.email = "user@example.com"
  }
  ```
- 체크: Route가 아닌 Composable 파라미터에 ViewModel 타입이 있는가. UI가 상태 객체의 필드를 직접 대입하는 곳이 있는가.

### R-00-02 의존은 바깥에서 안쪽으로만 흐른다 (UI → Domain → Data)

- 규칙: UI가 Domain을, Domain이 Data를 안다. 역방향 의존을 만들지 않고, Data 계층은 어떤 상위 계층 타입도 알지 못한다. **예외: Domain은 선택 계층이므로 USECASE_POLICY에 따라 ViewModel이 Repository를 직접 호출하는 것은 계층 건너뛰기가 아니라 허용된 경로다 (R-16-02).** 그 밖의 건너뛰기 참조(UI가 DataSource·DAO를 직접 부르는 것 등)는 금지다 (R-11-03).
- 근거: 계층 의존은 UI → (Domain) → Data 단방향이고 데이터 소스는 다른 계층에 의존하지 않는다 [S01](https://developer.android.com/topic/architecture). 도메인 계층은 repository·다른 use case·디스패처에만 의존하고 UI 계층 타입 의존은 금지된다 [S07](https://developer.android.com/topic/architecture/domain-layer). 세부 조문은 11이 소유한다.
- 예시:
  ```kotlin
  // Good: 안쪽 계층은 자기 모델만 안다
  class GetUserUseCase @Inject constructor(private val repository: UserRepository) {
      suspend operator fun invoke(id: String): User = repository.getUser(id)
  }

  // Bad: 데이터 계층이 UI 모델과 상태 홀더를 참조한다
  class UserRepository @Inject constructor(private val viewModel: UserViewModel) {
      suspend fun load(id: String): UserUiState = TODO()
  }
  ```
- 체크: Data·Domain 소스 파일에 `ViewModel`·`UiState`·`Composable`·Android UI 타입 import가 있는가. 계층 경계에서 모델을 바꿔 넘기고 있는가.

### R-00-03 상태는 불변 값으로 노출한다

- 규칙: 계층 밖으로 내보내는 상태·데이터는 불변 타입으로 만든다. 가변 컬렉션과 `var` 프로퍼티를 공개 표면에 두지 않고, 변경은 소유자가 새 값을 만들어 교체한다.
- 근거: 데이터 계층이 노출하는 데이터는 불변이어야 한다 [S06](https://developer.android.com/topic/architecture/data-layer). UI 상태는 불변 data class로 정의하고 UI에서 직접 수정하지 않는다 [S08](https://developer.android.com/topic/architecture/ui-layer). UiState의 구체 형태는 12가 소유한다.
- 예시:
  ```kotlin
  // Good: 불변 data class + 읽기 전용 스트림
  data class CartUiState(val items: List<CartItem> = emptyList(), val isLoading: Boolean = false)

  private val _uiState = MutableStateFlow(CartUiState())
  val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

  // Bad: 가변 프로퍼티·가변 컬렉션·가변 스트림을 그대로 공개
  class CartUiState {
      var items: MutableList<CartItem> = mutableListOf()
  }
  val uiState = MutableStateFlow(CartUiState())
  ```
- 체크: 공개 프로퍼티에 `var`·`Mutable*` 타입이 있는가. 노출한 스트림이 `asStateFlow()`·`asSharedFlow()`로 감싸여 있는가.

### R-00-04 의존성은 생성자로 명시한다

- 규칙: 협력 객체는 생성자 파라미터로 받는다. 클래스 안에서 직접 `new` 하거나 싱글턴·전역 접근자로 끌어오지 않는다. 필드 주입은 프레임워크가 인스턴스를 만드는 클래스에만 쓴다.
- 근거: 아키텍처 권장사항은 생성자 주입 기반 DI를 strongly recommended로 두고 스코프는 필요할 때만 지정하라고 한다 [S02](https://developer.android.com/topic/architecture/recommendations). Hilt도 ViewModel을 `@HiltViewModel` + `@Inject constructor`로 정의하고, 프레임워크 클래스에 한해 필드 주입을 허용한다 [S05](https://developer.android.com/training/dependency-injection/hilt-android). Hilt 사용법은 14가 소유한다.
- 예시:
  ```kotlin
  // Good: 필요한 것이 시그니처에 다 드러난다
  @HiltViewModel
  class LoginViewModel @Inject constructor(
      private val authRepository: AuthRepository,
  ) : ViewModel()

  // Bad: 의존이 본문에 숨어 테스트에서 교체할 수 없다
  class LoginViewModel : ViewModel() {
      private val authRepository = AuthRepository(ServiceLocator.api)
  }
  ```
- 체크: 클래스 본문에서 협력 객체를 직접 생성하거나 전역 접근자로 가져오는 곳이 있는가. 테스트에서 그 의존을 fake로 바꿔 끼울 수 있는가.

### R-00-05 요청받지 않은 추상화·기능을 만들지 않는다

- 규칙: 지금 요구된 것만 만든다. 구현이 하나뿐인 인터페이스, 위임만 하는 계층, 쓰이지 않는 확장 지점, 예상만으로 나눈 모듈을 미리 만들지 않는다. 필요가 실제로 생기면 그때 승격한다. **예외: Repository·DataSource의 인터페이스 경계는 구현이 하나여도 둔다.** R-11-02가 Repository 인터페이스를, R-30-10이 fake DataSource로 조립하는 Repository 테스트를 요구하므로, 이 두 경계의 인터페이스는 '지금 필요한 것'에 해당한다(테스트 대역이 두 번째 구현이다).
- 근거: 모듈은 저마다 오버헤드가 있어 과도한 세분화는 유지보수를 어렵게 하고 소규모 프로젝트에서는 모듈화가 정당화되지 않는다 [S03](https://developer.android.com/topic/modularization). 단순 위임만 하는 use case는 복잡도 대비 이득이 없어 만들지 않는다 [S07](https://developer.android.com/topic/architecture/domain-layer). 도메인 계층 자체가 대형 앱에 한해 권장되는 선택 사항이다 [S02](https://developer.android.com/topic/architecture/recommendations). UseCase 승격 조건은 16, 모듈 분할 조건은 10이 소유한다.
- 예시:
  ```kotlin
  // Good: 로직이 없으면 ViewModel이 repository를 직접 쓴다
  @HiltViewModel
  class ProfileViewModel @Inject constructor(
      private val userRepository: UserRepository,
  ) : ViewModel()

  // Bad: 호출 하나를 감싸기만 하는 UseCase
  class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
      suspend operator fun invoke(id: String): User = userRepository.getUser(id)
  }
  ```
- 체크: 새로 만든 추상화에 현재 구현·호출자가 둘 이상 있는가. 없다면 왜 지금 필요한지 한 문장으로 답할 수 있는가.

### R-00-06 규칙에 없는 결정은 사용자에게 묻는다

- 규칙: 이 팩과 프로젝트 지침이 침묵하는 선택지는 추측으로 굳히지 않는다. 먼저 기존 코드베이스의 방식을 따르고, 그래도 갈리거나 되돌리기 어려운 결정이면 선택지를 제시해 답을 받은 뒤 진행한다. 확정된 답은 규칙이나 결정 매트릭스로 남긴다.
- 근거: 코드 리뷰 표준은 스타일의 최종 권위를 스타일 가이드에 두고, 규칙이 다루지 않는 부분은 기존 코드베이스와의 일관성으로 판단하며 동등한 대안이면 작성자 선택을 존중하라고 한다 [S39](https://google.github.io/eng-practices/review/reviewer/standard.html). 에이전트 운영 규약 사례도 도구가 지원하지 않는 영역은 추측하지 말고 보고한 뒤 대기하도록 명문화한다 [S113](https://techblog.lycorp.co.jp/ko/android-cli-for-ai-agents-at-scale).
- 예시:
  ```kotlin
  // Good: 기존 값을 유지하고 결정이 필요한 지점을 드러낸다
  // TODO(결정 필요): 페이지 크기 20 vs 50. 기존 목록 화면은 모두 20이라 20을 유지한다.
  private const val PAGE_SIZE = 20

  // Bad: 규칙에 없다는 이유로 새 방식을 말없이 도입한다
  private val uiState = MutableLiveData<ProfileUiState>()
  ```
- 체크: 팩·프로젝트 지침·기존 코드 어디에도 없는 방식을 새로 들여왔는가. 들여왔다면 그 결정이 기록으로 남아 있는가.

## 그 밖의 원칙 (조문은 소유 파일에 있다)

여기 있는 문장은 한 줄 원칙이다. 판정 가능한 조문은 괄호 안 파일이 소유하므로 이 파일에서 다시 쓰지 않는다.

- 원칙: 데이터 타입마다 단일 진실 공급원(SSOT)을 지정하고 SSOT만 그 데이터를 바꾼다 [S01](https://developer.android.com/topic/architecture), [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md). (15 데이터)
- 원칙: 공개 API는 main-safe다. 각 타입이 스스로 적절한 디스패처로 옮기고 호출자에게 스레드 책임을 떠넘기지 않는다 [S06](https://developer.android.com/topic/architecture/data-layer), [S07](https://developer.android.com/topic/architecture/domain-layer), [S08](https://developer.android.com/topic/architecture/ui-layer). (15·16·22)
- 원칙: 테스트 더블은 mock보다 같은 인터페이스를 구현한 fake를 쓴다 [S02](https://developer.android.com/topic/architecture/recommendations), [S54](https://raw.githubusercontent.com/android/nowinandroid/main/README.md). (30 테스트)
- 원칙: 모듈·클래스의 공개 표면을 최소화하고 나머지는 `internal`·`private`로 막는다 [S01](https://developer.android.com/topic/architecture), [S03](https://developer.android.com/topic/modularization), [S04](https://developer.android.com/topic/modularization/patterns). (10 구조)
- 원칙: Android 프레임워크 의존은 진입점 컴포넌트에 가두고 앱 데이터를 그 안에 저장하지 않는다 [S01](https://developer.android.com/topic/architecture). (11 계층)

## 결정 매트릭스 형식 (다른 문서가 재사용)

규칙 하나로 답이 정해지지 않고 상황에 따라 갈리는 주제는 조문 대신 아래 표로 적는다.
판단 기준은 관찰 가능한 사실 하나로 쓰고, 기본값 열에는 근거가 없을 때 고를 값을 적는다.

| 판단 기준 | 선택지 A | 선택지 B | 기본값 |
|---|---|---|---|
| (관찰 가능한 조건 한 줄) | (A를 고르는 경우) | (B를 고르는 경우) | (A 또는 B) |

형식 예시 — 조문은 16이 소유한다.

| 판단 기준 | 선택지 A | 선택지 B | 기본값 |
|---|---|---|---|
| 로직을 ViewModel 2개 이상이 공유하거나 repository 2개 이상을 조합하는가 | UseCase로 승격 | ViewModel이 repository 직접 호출 | B |
