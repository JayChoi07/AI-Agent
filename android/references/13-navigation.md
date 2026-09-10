# 13 네비게이션

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

기준선은 Navigation 3 stable 1.1.7이다(NAV3_VERSION). 이 버전에는 결과 반환 API(`ResultEventBus`·`ResultEffect`, 1.2.0-alpha02 이상)와 딥링크 API(`DeepLinkRequest`·`UriDeepLinkMatcher`, 1.2.0-alpha03 이상)가 없으므로 R-13-07·R-13-08의 대체 방식을 쓴다. 모듈 분할 조건은 R-10-13이 소유한다. Nav3를 쓰는 모듈은 compileSdk 36 이상이어야 하고 Fragment·View 혼용 화면에는 쓰지 않는다 [S25](https://developer.android.com/guide/navigation/navigation-3/migration-guide), [S26](https://developer.android.com/guide/navigation/navigation-3/get-started) — 36은 출처가 말하는 하한이고, 이 팩의 확정값은 **compileSdk 37**이다(실빌드 실증 2026-09-09: 확정 버전 조합의 의존성 15개가 API 37을 요구한다). 구현 방법(코드 배치·마이그레이션 절차)은 로컬 navigation-3 스킬을 참조한다.

### R-13-01 목적지 키는 `NavKey`를 구현하고 `@Serializable`을 붙여 그 화면을 소유한 feature 모듈에 둔다
- 규칙: 백스택에 들어가는 모든 키는 `NavKey`를 구현하고 `@Serializable`을 붙인다. 키는 화면을 소유한 `:feature:*` 모듈이 정의하고, 화면에 필요한 인자만 프로퍼티로 갖는다.
- 근거: "Every key in the back stack must implement the `NavKey` interface"이고 "must be marked with the `@Serializable` annotation" — 직렬화가 있어야 구성 변경과 프로세스 사망을 넘겨 백스택이 복원된다 [S22](https://developer.android.com/guide/navigation/navigation-3/save-state). 키는 피처가 소유하는 공개 표면이다 [S24](https://developer.android.com/guide/navigation/navigation-3/modularize)
- 예시:
  ```kotlin
  // Good: :feature:profile 이 자기 키를 소유
  @Serializable data class ProfileKey(val userId: String) : NavKey
  // Bad: 직렬화 없는 키, 인자 대신 객체 전달
  data class ProfileKey(val user: User) : NavKey
  ```
- 체크: 모든 `NavKey` 구현에 `@Serializable`이 있는가. 키 프로퍼티가 직렬화 가능한 원시값·값 객체인가.

### R-13-02 feature는 다른 feature의 NavKey를 직접 참조하지 않는다
- 규칙: feature 모듈은 다른 feature 모듈에 의존하지 않는다. 다른 화면으로의 이동은 `onOpenProfile: (String) -> Unit` 같은 콜백 파라미터로 밖에 내보내고, 콜백을 실제 키 push로 잇는 조합은 `:app`(또는 `:core:navigation`)의 조합층에서만 한다.
- 근거: FEATURE_MODULE_SPLIT 확정값이 단일 `:feature:*` 모듈 + 콜백 + `:app` 조합층이다. 피처 간 직접 의존은 모듈화가 막으려는 결합이며 [S04](https://developer.android.com/topic/modularization/patterns), Nav3 모듈화 가이드도 키와 엔트리를 피처 경계 밖에서 조립하는 구조를 전제한다 [S24](https://developer.android.com/guide/navigation/navigation-3/modularize). 다른 feature가 이 feature의 키를 꼭 참조해야 할 때만 `:api`/`:impl`로 나누며 그 조건은 R-10-13이 소유한다
- 예시:
  ```kotlin
  // Good: feature 는 콜백만 노출
  @Composable fun FeedRoute(onOpenProfile: (String) -> Unit)
  // Bad: feature 가 다른 feature 의 키를 import
  backStack.add(ProfileKey(userId))
  ```
- 체크: `:feature:*` 모듈 빌드 파일에 다른 `:feature:*` 의존이 있는가. 콜백 배선이 `:app`에만 있는가.

### R-13-03 백스택은 `:app` 최상위가 `rememberNavBackStack`으로 소유하고 래퍼 API로만 조작한다
- 규칙: 백스택은 `:app`의 최상위 컴포저블 한 곳에서 `rememberNavBackStack(HomeKey)`로 만들어 `NavDisplay`에 넘긴다. 화면 코드가 리스트를 직접 `add`·`removeLastOrNull` 하지 않고, `navigate()`·`goBack()`만 노출하는 `Navigator` 래퍼를 두고 그 API로만 이동한다. 래퍼는 마지막 키와 같은 값의 키를 다시 push하지 않는다.
- 근거: Nav3는 백스택을 개발자가 소유하고 `NavDisplay`가 그것을 관찰해 렌더링한다 [S21](https://developer.android.com/guide/navigation/navigation-3/basics). `rememberNavBackStack`으로 만든 백스택이 구성 변경·프로세스 사망을 넘겨 복원된다 [S22](https://developer.android.com/guide/navigation/navigation-3/save-state). 마이그레이션 가이드가 `NavigationState`+`Navigator` 래퍼를 제시하며, 리스트 직접 조작은 다중 top-level 백스택이 필요해지는 순간 화면마다 재구현을 부른다 [S25](https://developer.android.com/guide/navigation/navigation-3/migration-guide). 중복 push를 막는 이유는 `contentKey` 기본값이 `key.toString()`과 `key::class.toString()`의 합성이어서 값이 같은 키가 둘 이상 쌓이면 엔트리가 구별되지 않기 때문이다 [S43](https://developer.android.com/jetpack/androidx/releases/navigation3)
- 예시:
  ```kotlin
  // Good
  val backStack = rememberNavBackStack(HomeKey)
  val navigator = remember(backStack) { Navigator(backStack) }
  NavDisplay(backStack = backStack, onBack = { navigator.goBack() }, entryProvider = ...)
  // Bad: 화면에서 리스트를 직접 만진다
  backStack.removeLastOrNull()
  ```
- 체크: `rememberNavBackStack` 호출이 앱 전체에 하나인가. feature 코드에 `backStack.add`가 남아 있는가.

### R-13-04 키→콘텐츠 매핑은 `entryProvider` DSL로 하고 feature는 `EntryProviderScope` 확장 함수로 자기 엔트리를 낸다
- 규칙: `NavDisplay`의 `entryProvider`는 `entryProvider { entry<T> { } }` DSL로 작성하고 `when` 분기 람다를 쓰지 않는다. feature는 `EntryProviderScope<NavKey>.featureEntries(...)` 확장 함수로 자기 엔트리를 정의하고, `:app`은 Hilt `@IntoSet` 멀티바인딩으로 그 함수들을 모아 한 번에 등록한다. 다이얼로그·바텀시트도 별도 화면 구조가 아니라 `entry<T>` + `metadata = DialogSceneStrategy.dialog()`로 표현한다.
- 근거: 키→콘텐츠 해석의 표준 형태가 `entryProvider { entry<T> { ... } }` DSL이다 [S21](https://developer.android.com/guide/navigation/navigation-3/basics). 모듈화 가이드가 엔트리 빌더를 `EntryProviderScope<NavKey>` 확장 함수로 정의하고 Hilt `@IntoSet` + `@InstallIn(ActivityRetainedComponent::class)`로 수집하는 방식을 제시한다 [S24](https://developer.android.com/guide/navigation/navigation-3/modularize). Nav2의 `dialog<T>`는 `entry<T>` + `metadata = DialogSceneStrategy.dialog()`로 대응되고, base route용 `navigation<T>`는 대응 없이 사라진다 [S25](https://developer.android.com/guide/navigation/navigation-3/migration-guide)
- 예시:
  ```kotlin
  // Good: feature 모듈
  fun EntryProviderScope<NavKey>.profileEntries(onBack: () -> Unit) {
      entry<ProfileKey> { key -> ProfileRoute(userId = key.userId, onBack = onBack) }
  }
  // Bad
  entryProvider = { key -> when (key) { is ProfileKey -> ... } }
  ```
- 체크: `entryProvider`에 `when` 분기가 있는가. feature 엔트리 빌더가 `:app`에서 수집되는가. `:app` 하나가 모든 feature를 아는 단일 앱 모듈 구성이면 `@IntoSet` 없이 `{{feature}}Entry()`를 `:app`에서 직접 부르는 것이 기본값이고(템플릿이 그 형태다), 동적 feature나 앱이 여럿일 때 `@IntoSet` 수집으로 올린다.

### R-13-05 `entryDecorators`의 첫 항목은 `rememberSaveableStateHolderNavEntryDecorator()`로 두고 화면 ViewModel은 엔트리에 스코프한다
- 규칙: `NavDisplay`에 데코레이터를 넘길 때 `rememberSaveableStateHolderNavEntryDecorator()`를 반드시 첫 번째에 둔다. 화면 ViewModel은 `rememberViewModelStoreNavEntryDecorator()`로 `NavEntry`에 스코프해 엔트리가 백스택에서 빠질 때 함께 정리되게 한다.
- 근거: 공식 예시가 기본 데코레이터(`rememberSaveableStateHolderNavEntryDecorator()`)를 먼저 넣고 그다음 view model store 데코레이터를 더하는 순서로 쓴다("Add the default decorators … then add the view model store decorator"). ViewModel 스코프는 `rememberViewModelStoreNavEntryDecorator()`가 담당하며 엔트리 제거 시 store가 clear된다 [S22](https://developer.android.com/guide/navigation/navigation-3/save-state), [S23](https://developer.android.com/guide/navigation/navigation-3/naventrydecorators). 여러 엔트리에 공통으로 걸리는 로깅·인증 게이트도 `NavEntryDecorator`로 구현한다 [S23](https://developer.android.com/guide/navigation/navigation-3/naventrydecorators)
- 예시:
  ```kotlin
  // Good
  entryDecorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator(),
      rememberViewModelStoreNavEntryDecorator(),
  )
  // Bad: 순서가 뒤바뀌어 엔트리 상태가 복원되지 않는다
  entryDecorators = listOf(rememberViewModelStoreNavEntryDecorator(), rememberSaveableStateHolderNavEntryDecorator())
  ```
- 체크: 데코레이터 목록 첫 항목이 `rememberSaveableStateHolderNavEntryDecorator()`인가. `entry` 안에서 `hiltViewModel()`로 ViewModel을 얻고 있는가(navigation-3 스킬 레시피, 스크래치 빌드로 확인).

### R-13-06 목적지는 Route 컴포저블과 Screen 컴포저블로 나눈다
- 규칙: `entry<T>`가 부르는 것은 Route 컴포저블이다. Route는 ViewModel 연결, 상태 수집, 콜백 배선만 하고 UI를 그리지 않는다. 실제 UI는 UiState와 람다만 받는 Screen 컴포저블이 그린다. Screen은 ViewModel도 NavKey도 받지 않는다.
- 근거: UI는 상태를 그리기만 하고 비즈니스 로직은 UI 계층에 두지 않는다 [S08](https://developer.android.com/topic/architecture/ui-layer). 상태를 읽고 쓰는 컴포저블의 최소 공통 조상으로 hoist하라는 원칙이 Route/Screen 경계와 같다 [S12](https://developer.android.com/develop/ui/compose/state-hoisting). 상태 수집은 `collectAsStateWithLifecycle`로 한다 [S02](https://developer.android.com/topic/architecture/recommendations)
- 예시:
  ```kotlin
  // Good
  @Composable fun ProfileRoute(viewModel: ProfileViewModel, onBack: () -> Unit) {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      ProfileScreen(uiState = uiState, onRetry = viewModel::retry, onBack = onBack)
  }
  @Composable fun ProfileScreen(uiState: ProfileUiState, onRetry: () -> Unit, onBack: () -> Unit)
  // Bad: Screen 이 ViewModel 을 받는다
  @Composable fun ProfileScreen(viewModel: ProfileViewModel)
  ```
- 체크: Screen 컴포저블 파라미터에 ViewModel·NavKey 타입이 있는가. Route가 UI를 직접 그리는가.

### R-13-07 화면 간 결과는 공유 상태나 상위 ViewModel로 전달한다
- 규칙: 선택 결과 같은 값을 이전 화면에 돌려줄 때는 두 화면이 함께 보는 상위 스코프 ViewModel이나 저장소를 경유한다. `ResultEventBus`·`ResultEffect`·`conflateAsState`는 1.1.7에 없으므로 쓰지 않는다. 복원돼야 하는 값은 메모리 상태가 아니라 저장소에 쓴다.
- 근거: NAV3_VERSION이 1.1.7이고 결과 반환 API는 1.2.0-alpha02 이상에서만 제공된다 [S43](https://developer.android.com/jetpack/androidx/releases/navigation3). 상태형 결과 레시피조차 "The result state does not survive configuration change or process death"라고 한계를 명시하므로 복원이 필요한 값은 저장소를 거쳐야 한다 [S28](https://developer.android.com/guide/navigation/navigation-3/recipes/results-state). 이벤트형 결과는 일회성 전달용이라 같은 유실 문제를 안는다 [S27](https://developer.android.com/guide/navigation/navigation-3/recipes/results-event)
- 예시:
  ```kotlin
  // Good: 두 화면이 함께 보는 상위 ViewModel 이 결과를 소유
  class CheckoutFlowViewModel { val selectedAddress: StateFlow<Address?> }
  // Bad: 1.1.7 에 없는 API
  val resultBus = LocalResultEventBus.current
  ```
- 체크: `ResultEventBus`·`ResultEffect` 참조가 있는가. 결과 값이 프로세스 사망 후에도 필요한데 메모리에만 있는가.

### R-13-08 딥링크는 `DeepLinkPattern`+`KeyDecoder`를 직접 구현하고 매핑 표는 `:app`에 둔다
- 규칙: 1.1.7에는 딥링크 API가 없으므로 URI 패턴과 키 디코더를 직접 정의한다. 매핑 표는 `:app`이 소유하고, 목적지가 하위 화면이면 부모 키를 거슬러 합성 백스택을 만든 뒤 `rememberNavBackStack(*syntheticBackStack.toTypedArray())`로 시드한다. feature 모듈에는 URI 문자열을 두지 않는다.
- 근거: `DeepLinkRequest`·`DeepLinkMatcher`·`UriDeepLinkMatcher`는 1.2.0-alpha03에 도입돼 1.1.7에 없다 [S43](https://developer.android.com/jetpack/androidx/releases/navigation3). Nav3 마이그레이션 가이드도 딥링크를 미지원 기능으로 명시한다 [S25](https://developer.android.com/guide/navigation/navigation-3/migration-guide). 1.1.x용 수동 구현 레시피가 `DeepLinkPattern`+`KeyDecoder`와 합성 백스택 시드를 제시한다 [S57](https://github.com/android/nav3-recipes/blob/main/docs/deeplink-guide.md)
- 예시:
  ```kotlin
  // Good: :app 이 패턴과 디코더를 소유
  val patterns = listOf(DeepLinkPattern("/user/{id}") { args -> ProfileKey(args.getValue("id")) })
  val backStack = rememberNavBackStack(*syntheticBackStack.toTypedArray())
  // Bad: feature 안에 URI 문자열과 파싱이 흩어진다
  const val PROFILE_DEEP_LINK = "myapp://user/{id}"
  ```
- 체크: URI 패턴 문자열이 `:app` 밖에 있는가. 하위 화면 딥링크가 부모 키를 포함한 백스택으로 시드되는가.
