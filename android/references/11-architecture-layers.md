# 11 아키텍처 계층

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

## 계층별 책임

| 계층 | 패키지 | 구성 요소 | 책임 |
|---|---|---|---|
| UI | `..ui..` | Composable, ViewModel, UiState | 상태를 그리고 사용자 입력을 상태 홀더로 올린다. 데이터를 만들지 않는다 |
| Domain | `..domain..` | UseCase | 여러 Repository를 조합하거나 화면 여럿이 공유하는 규칙. 없으면 만들지 않는다 |
| Data | `..data..` | Repository 인터페이스와 구현, DataSource, DTO, Entity, 매퍼 | 데이터 획득·저장·캐시와 모델 변환. 앱 데이터의 SSOT |
| Model | `..model..` | 도메인 모델, 에러 타입, 결과 타입 | 계층 간에 오가는 값의 정의. 아무것도 참조하지 않는다 |

멀티모듈에서는 `:core:ui`·`:core:domain`·`:core:data`·`:core:model`이 같은 역할을 맡고, 단일 feature 모듈에서는 `{{package}}.feature.{{feature}}.{ui,domain,data,model,di}` 패키지가 같은 경계를 만든다(10-project-structure.md `R-10-06`).

UiState의 형태·개수와 일회성 이벤트 처리는 12-state-management.md가, UseCase 생성 판단은 16이, Repository 내부 구성은 15가 소유한다. 이 문서는 계층 사이의 경계만 다룬다.

## 계층별 금지

| 금지 | 대신 | 근거 |
|---|---|---|
| Data가 UiState·Composable·ViewModel·UseCase를 참조 | 모델 타입만 주고받는다 | S01, S07 |
| Domain이 Android SDK 타입(`Context`, `Uri`, `Bundle`, `Cursor`)에 의존 | 순수 Kotlin 타입으로 표현 | S07, S01 |
| UI가 DataSource·DAO·Retrofit 서비스를 직접 호출 | Repository 인터페이스 경유 | S06, S04 |
| Model이 다른 계층 타입이나 저장·직렬화 애노테이션을 가짐 | DTO·Entity를 data에 따로 둔다 | S06, S02 |

## 모델 3종과 매핑 위치

| 모델 | 정의 위치 | 만들어지는 곳 | 사라지는 곳 |
|---|---|---|---|
| DTO(네트워크 응답) | `..data..`(`:core:network`) | Remote DataSource | Repository 구현의 매퍼 |
| Entity(DB 행) | `..data..`(`:core:database`) | Local DataSource | Repository 구현의 매퍼 |
| 도메인 모델 | `..model..`(`:core:model`) | Repository 구현의 매퍼 | 계층 경계를 넘어 UI까지 그대로 간다 |

### R-11-01 계층 의존은 UI → Domain → Data 한 방향으로만 흐른다
- 규칙: UI는 domain과 data를, domain은 data를 본다. data가 의존하는 계층은 model뿐이고, model은 아무것도 의존하지 않는다. 도메인 계층은 선택이므로 UseCase가 없는 화면은 ViewModel이 Repository를 직접 부른다.
- 근거: 의존 방향은 UI → (Domain) → Data 단방향이고 데이터 소스는 다른 계층에 의존하지 않는다 [S01](https://developer.android.com/topic/architecture). 도메인 계층은 UI와 데이터 사이에 놓이며 UI 계층 타입에 의존하지 않는다 [S07](https://developer.android.com/topic/architecture/domain-layer). use case를 두는 도메인 계층은 대형 앱 권장 사항이라 상시 필수가 아니다 [S02](https://developer.android.com/topic/architecture/recommendations). NiA도 domain이 data를 향하고 모델은 별도 자리에 둔다 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md), [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md).
- 예시:
  ```kotlin
  // Good — ui가 data의 인터페이스를 본다(UseCase가 없는 화면)
  package com.example.app.feature.home.ui
  import com.example.app.feature.home.data.UserRepository
  // Bad — data가 위 계층을 import
  package com.example.app.feature.home.data
  import com.example.app.feature.home.domain.GetUserUseCase
  ```
- 체크: data 패키지 import에 `.ui.`·`.domain.`이 있는가. model 패키지 import에 다른 계층이 있는가.

### R-11-02 Repository 인터페이스와 구현은 모두 data 계층에 둔다
- 규칙: `UserRepository` 인터페이스와 `DefaultUserRepository` 구현을 함께 `..data..`(`:core:data` 또는 feature의 `data/`)에 둔다. UI와 domain은 인터페이스 타입만 쓰고 구현 클래스를 직접 참조하지 않는다. 구현 선택은 DI가 한다(14-di.md `R-14-05`).
- 근거: 데이터 계층의 진입점은 항상 repository 클래스이며 다른 계층은 데이터 소스에 직접 접근하지 않는다 [S06](https://developer.android.com/topic/architecture/data-layer). data 모듈은 repository만 외부 API로 노출하고 데이터 소스를 은닉한다 [S04](https://developer.android.com/topic/modularization/patterns). 데이터 계층 컴포넌트는 대부분 인터페이스로 정의하고 구현을 바인딩한다 [S54](https://raw.githubusercontent.com/android/nowinandroid/main/README.md). 인터페이스를 domain으로 올리면 data가 domain을 참조하게 되어 R-11-01의 방향이 깨진다 [S01](https://developer.android.com/topic/architecture).
- 예시:
  ```kotlin
  // Good — 둘 다 ..data..
  package com.example.app.feature.home.data
  interface UserRepository { fun getUserStream(id: String): Flow<User> }
  internal class DefaultUserRepository @Inject constructor(
      private val remote: UserRemoteDataSource,
  ) : UserRepository
  // Bad — 구현 클래스를 ViewModel이 직접 받음
  class HomeViewModel @Inject constructor(private val repo: DefaultUserRepository)
  ```
- 체크: `*Repository` 인터페이스와 구현이 모두 `..data..`에 있는가(테스트의 `Fake*`는 예외). 상위 계층이 `Default*`를 import 하는가.

### R-11-03 UI 계층은 Repository까지만 부른다
- 규칙: ViewModel과 Composable은 Repository 인터페이스(로직이 있으면 UseCase)만 부른다. DAO, Retrofit 서비스, DataStore, `SharedPreferences`를 UI 계층에서 직접 다루지 않는다.
- 근거: 데이터 계층의 진입점은 repository뿐이고 데이터 소스는 은닉 대상이다 [S06](https://developer.android.com/topic/architecture/data-layer). data 모듈은 repository만 외부 API로 노출한다 [S04](https://developer.android.com/topic/modularization/patterns).
- 예시:
  ```kotlin
  // Good
  class HomeViewModel @Inject constructor(private val repository: UserRepository)
  // Bad
  class HomeViewModel @Inject constructor(private val dao: UserDao)
  ```
- 체크: ViewModel 생성자 파라미터 타입이 `*Repository`·`*UseCase` 밖으로 나가는가. feature 모듈이 `:core:database`·`:core:network`에 의존하는가(10-project-structure.md 모듈 그래프).

### R-11-04 domain 계층은 Android 타입에 의존하지 않는다
- 규칙: `..domain..`의 UseCase는 `android.*`·`androidx.*` 타입을 파라미터·반환·필드로 쓰지 않는다. 리소스 문자열이 필요하면 도메인은 식별자나 에러 타입만 반환하고 UI가 문자열로 바꾼다. `..model..`은 한 걸음 더 나아가 Android 타입을 전혀 쓰지 않는다. 두 계층 모두 별도 JVM 모듈 분리는 선택이고, feature 모듈 안의 `domain`·`model` 패키지도 이 규칙을 만족한다.
- 근거: use case의 의존 허용 범위는 repository·다른 use case·디스패처이고 UI 계층 의존은 금지된다 [S07](https://developer.android.com/topic/architecture/domain-layer). Android 프레임워크 의존을 줄이라는 최상위 원칙 [S01](https://developer.android.com/topic/architecture). UI·엔트리포인트가 아닌 모듈은 순수 Kotlin/Java 모듈을 선호한다 [S04](https://developer.android.com/topic/modularization/patterns).
- 예시:
  ```kotlin
  // Good — ..model..
  sealed interface LoginError { data object InvalidCredential : LoginError }
  // Bad
  class LoginUseCase(private val context: Context)
  ```
- 체크: domain·model 소스에 `import android.`이 있는가. 별도 모듈로 뽑았다면 `:core:model` 빌드 파일이 `kotlin("jvm")`인가.

### R-11-05 data 계층은 위 계층 타입을 참조하지 않는다
- 규칙: Repository 구현과 DataSource는 UiState·Composable·ViewModel·UseCase를 참조하지 않는다. 화면 표시용 가공(포맷·정렬·문자열화)은 UI 계층에서 한다.
- 근거: 데이터 소스는 다른 계층에 의존하지 않는다 [S01](https://developer.android.com/topic/architecture). 도메인 계층이 데이터 계층 위에 놓이므로 아래에서 위를 참조하면 방향이 뒤집힌다 [S07](https://developer.android.com/topic/architecture/domain-layer). 노출 데이터는 불변이어야 한다는 데이터 계층 규칙과도 이어진다 [S06](https://developer.android.com/topic/architecture/data-layer).
- 예시:
  ```kotlin
  // Good
  suspend fun getUser(id: String): User
  // Bad — data가 화면 상태를 만든다
  suspend fun getUser(id: String): HomeUiState
  ```
- 체크: data 패키지 import에 `.ui.`·`.domain.`·`androidx.compose`가 있는가. Repository 반환 타입이 `*UiState`인가.

### R-11-06 계층 간 통신은 suspend 함수와 `Flow`로 한다
- 규칙: 일회성 작업은 `suspend` 함수로, 지속 관찰은 `Flow`로 노출한다. 콜백 인터페이스나 `LiveData`를 계층 경계에 두지 않는다.
- 근거: 계층 간 통신은 coroutines와 Flow로 하라는 강한 권고 [S02](https://developer.android.com/topic/architecture/recommendations). 일회성은 suspend, 지속 관찰은 Flow라는 데이터 계층 규칙 [S06](https://developer.android.com/topic/architecture/data-layer). NiA도 같은 형태를 쓴다 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md).
- 예시:
  ```kotlin
  // Good
  interface UserRepository {
      fun getUserStream(id: String): Flow<User>
      suspend fun refresh(id: String)
  }
  // Bad
  interface UserRepository { fun getUser(id: String, callback: (User) -> Unit) }
  ```
- 체크: 계층 경계 API에 콜백 파라미터나 `LiveData` 반환이 있는가.

### R-11-07 모델은 종류별로 나누고 매핑은 Repository 구현에서 한다
- 규칙: 네트워크 DTO와 DB Entity는 `..data..`에, 도메인 모델과 에러·결과 타입은 `..model..`에 정의하고 서로 상속·재사용하지 않는다. 변환 함수는 Repository 구현 옆(data 계층)에 두고, 경계 밖으로는 모델 타입만 내보낸다.
- 근거: 소스별 모델과 도메인 모델을 분리하라는 데이터 계층 지침이며 메모리 절약과 경계 분리가 이유다 [S06](https://developer.android.com/topic/architecture/data-layer). 복잡한 앱은 계층별 모델을 분리하라는 권고 [S02](https://developer.android.com/topic/architecture/recommendations). NiA도 모델을 별도 자리로 뺀다 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md).

| 판단 기준 | 선택지 A(모델 하나 재사용) | 선택지 B(종류별 분리) | 기본값 |
|---|---|---|---|
| 서버 응답 필드가 화면에 필요한 필드와 같은가 | 같다 | 다르거나 곧 갈라진다 | B |
| DB 스키마를 서버 스키마와 함께 바꿔야 하는가 | 상관없다 | 따로 바꿔야 한다 | B |
| 화면·저장소가 각각 하나뿐인 임시 프로토타입인가 | 그렇다 | 아니다 | 프로토타입만 A, 근거를 남긴다 |

- 예시:
  ```kotlin
  // Good — ..data.. 의 매퍼
  internal fun UserDto.toModel(): User = User(id = id, name = displayName.orEmpty())
  // Bad — DTO를 그대로 UI까지 올림
  fun getUserStream(id: String): Flow<UserDto>
  ```
- 체크: `..model..`의 타입에 `@SerialName`·`@Entity` 같은 저장·직렬화 애노테이션이 붙어 있는가. 매퍼가 UI 계층에 있는가.

### R-11-08 `AndroidViewModel`을 쓰지 않는다
- 규칙: ViewModel은 `androidx.lifecycle.ViewModel`을 상속하고 `Application`을 받지 않는다. 리소스·시스템 서비스가 필요하면 그 접근을 인터페이스로 감싸 생성자로 주입한다.
- 근거: `AndroidViewModel` 사용 금지는 공식 권고이며 ViewModel을 Android 프레임워크에서 떼어 테스트 가능하게 만든다 [S02](https://developer.android.com/topic/architecture/recommendations). 앱 컴포넌트에 데이터를 저장하지 말라는 원칙과 같은 방향이다 [S01](https://developer.android.com/topic/architecture).
- 예시:
  ```kotlin
  // Good
  @HiltViewModel
  class HomeViewModel @Inject constructor(private val repository: UserRepository) : ViewModel()
  // Bad
  class HomeViewModel(app: Application) : AndroidViewModel(app)
  ```
- 체크: `AndroidViewModel`을 상속한 클래스가 있는가. ViewModel 생성자에 `Context`·`Application`이 있는가.

### R-11-09 계층별 네이밍을 고정한다
- 규칙: 메서드는 동사구, 프로퍼티는 명사구로 쓴다. 스트림 반환은 `get{Model}Stream()`, 인터페이스의 기본 구현은 `Default` 접두, 테스트 더블은 `Fake` 접두를 쓴다. Repository는 `[DataType]Repository`, 데이터 소스는 `[DataType][Remote|Local]DataSource`로 쓴다.
- 근거: 동사구·명사구, `get{Model}Stream()`, `Default`·`Fake` 접두는 공식 네이밍 권고 [S02](https://developer.android.com/topic/architecture/recommendations). repository와 데이터 소스 이름 규칙은 데이터 계층 가이드 [S06](https://developer.android.com/topic/architecture/data-layer). 언어 차원의 이름 규칙은 20-naming.md가 따로 소유한다.
- 예시:
  ```kotlin
  // Good
  interface NewsRepository { fun getNewsStream(): Flow<List<News>> }
  internal class DefaultNewsRepository(...) : NewsRepository
  class FakeNewsRepository : NewsRepository
  // Bad
  interface NewsRepo { fun news(): Flow<List<News>> }
  class NewsRepositoryImpl(...) : NewsRepo
  ```
- 체크: 구현 클래스 이름이 `*Impl`인가(`Default*`여야 한다). `Flow`를 반환하는 함수 이름이 `Stream`으로 끝나는가.
