# 17 Compose UI

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

구현 방법은 로컬 공식 스킬을 따른다. 창 크기·폴더블·다중 창 대응 레이아웃은 `adaptive` 스킬을 본다. 시스템 바 인셋과 화면 가장자리까지 그리는 처리는 `edge-to-edge` 스킬을 본다. 이 문서는 두 주제의 방법이 아니라 컴포저블 작성 규칙만 정한다.

## 결정 매트릭스

상태를 어디에 둘 것인가 (R-17-03·R-17-12)

| 판단 기준 | 선택지 A: 컴포저블 `remember`/`rememberSaveable` | 선택지 B: ViewModel `UiState` | 기본값 |
|---|---|---|---|
| 화면 밖(다른 화면·재진입)에서도 필요한가 | A | B | B |
| 비즈니스 데이터이거나 저장소에서 오는가 | A | B | B |
| 펼침·스크롤 위치 같은 순수 UI 표현인가 | A | B | A |
| 구성 변경·프로세스 재생성을 넘겨야 하는가 | `rememberSaveable` | B | 상황에 따라 |

## 규칙

### R-17-01 Screen 컴포저블은 상태와 콜백만 받는다
- 규칙: `XxxScreen`은 `uiState`와 이벤트 콜백(`onEvent`·`onXxxClick`)만 파라미터로 받는다. ViewModel·`MutableState`·`State<T>`를 파라미터로 넘기지 않는다. ViewModel 주입과 상태 수집은 Route/entry 컴포저블 한 곳에서만 한다.
- 근거: `MutableState<T>` 파라미터는 상태의 공동 소유를 부추기므로 `value: T` + `onValueChange: (T) -> Unit`을 쓴다 [S18](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md), 같은 규정의 컴포넌트 지침 [S19](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md), UI는 상태를 그리고 변경은 상태 홀더에 위임한다 [S08](https://developer.android.com/topic/architecture/ui-layer), 리프 컴포저블은 ViewModel을 모른다는 국내 적용 사례 [S111](https://techblog.lycorp.co.jp/ko/developing-android-ui-with-jetpack-compose)
- 예시:
  ```kotlin
  // Good
  @Composable fun NewsRoute(viewModel: NewsViewModel = hiltViewModel()) {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      NewsScreen(uiState = uiState, onBookmarkClick = viewModel::toggleBookmark)
  }
  @Composable fun NewsScreen(uiState: NewsUiState, onBookmarkClick: (String) -> Unit, modifier: Modifier = Modifier)
  // Bad
  @Composable fun NewsScreen(viewModel: NewsViewModel)
  ```
- 체크: `Screen` 파라미터에 ViewModel이나 `MutableState`가 있는가. 상태 수집이 Route 밖에서 일어나는가.

### R-17-02 모든 Screen에 다크와 폰트 배율 두 종류의 `@Preview`를 붙인다
- 규칙: 각 `XxxScreen`마다 최소 2개의 프리뷰를 둔다. 하나는 `uiMode = UI_MODE_NIGHT_YES`(다크), 하나는 `fontScale = 1.5f`(큰 글자)다. 프리뷰는 ViewModel 없이 더미 `UiState`를 넘겨 만든다. 로딩·에러 같은 주요 상태가 있으면 상태별 프리뷰를 추가한다.
- 근거: 컴포저블의 상태별 미리보기와 라이트/다크(`uiMode = UI_MODE_NIGHT_YES`) 프리뷰를 규약으로 운영한 사례 [S111](https://techblog.lycorp.co.jp/ko/developing-android-ui-with-jetpack-compose), 프리뷰가 스크린샷 테스트의 입력이 된다 [S34](https://developer.android.com/studio/preview/compose-screenshot-testing), 프리뷰 캡처 기반 검증 도구 [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md). 폰트 배율 변형은 접근성 회귀를 프리뷰 단계에서 잡으려는 이 팩의 확정 사항이며, 출처가 뒷받침하는 범위는 프리뷰 의무화와 다크 변형까지다
- 예시:
  ```kotlin
  // Good
  @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
  @Composable private fun NewsScreenDarkPreview() { AppTheme { NewsScreen(previewUiState, {}) } }
  @Preview(fontScale = 1.5f)
  @Composable private fun NewsScreenLargeFontPreview() { AppTheme { NewsScreen(previewUiState, {}) } }
  ```
- 체크: Screen마다 프리뷰가 2개 이상인가. 프리뷰가 ViewModel을 만들지 않고 더미 상태를 쓰는가.

### R-17-03 상태 호이스팅은 3원칙으로 위치를 정한다
- 규칙: 상태는 그 상태를 읽는 모든 컴포저블의 최소 공통 부모, 그 상태를 바꾸는 가장 높은 레벨까지 올린다. 같은 이벤트로 함께 바뀌는 두 상태는 같은 곳에 둔다.
- 근거: 상태 호이스팅 3원칙 [S13](https://developer.android.com/develop/ui/compose/state), 호이스팅 개념과 위치 지침 [S12](https://developer.android.com/develop/ui/compose/state-hoisting)
- 예시:
  ```kotlin
  // Good: 두 자식이 함께 읽는 값은 부모가 소유
  @Composable fun SearchSection(query: String, onQueryChange: (String) -> Unit) {
      SearchField(query, onQueryChange)
      ResultList(query)
  }
  // Bad: 자식이 각자 remember 해서 값이 갈라진다
  @Composable fun SearchField() { var query by remember { mutableStateOf("") } }
  ```
- 체크: 두 컴포저블이 같은 값을 각각 `remember` 하고 있는가. 상태가 필요 이상으로 위에 올라가 있는가.

### R-17-04 기본형은 상태 없는 컴포저블이고 상태 있는 버전은 얇은 래퍼로만 만든다
- 규칙: 재사용 컴포저블은 상태를 받지 않고 값과 콜백만 받는 형태를 기본으로 정의한다. 편의를 위한 stateful 오버로드가 필요하면 `remember` 한 값을 넘기는 얇은 래퍼로만 둔다.
- 근거: stateless 컴포저블과 stateful 래퍼 분리 [S13](https://developer.android.com/develop/ui/compose/state), 리프 컴포저블을 스테이트리스로 만드는 적용 사례 [S111](https://techblog.lycorp.co.jp/ko/developing-android-ui-with-jetpack-compose)
- 예시:
  ```kotlin
  // Good
  @Composable fun Counter(count: Int, onIncrement: () -> Unit, modifier: Modifier = Modifier)
  @Composable fun Counter(modifier: Modifier = Modifier) {
      var count by rememberSaveable { mutableStateOf(0) }
      Counter(count, { count++ }, modifier)
  }
  ```
- 체크: 재사용 컴포저블이 내부에서만 상태를 들고 있어 테스트가 어려운가.

### R-17-05 `modifier: Modifier = Modifier`를 첫 선택 파라미터로 정확히 하나 둔다
- 규칙: UI를 방출하는 모든 공개 컴포저블은 `modifier: Modifier = Modifier` 파라미터 하나를 갖고 이를 루트 레이아웃에 적용한다. 전달받은 modifier 앞에 다른 modifier를 붙이지 않고 뒤에만 이어 붙인다.
- 근거: modifier 파라미터 규정(타입·기본값·개수·루트 적용) [S18](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md), 컴포넌트 지침의 동일 규정 [S19](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md)
- 예시:
  ```kotlin
  // Good
  @Composable fun NewsCard(news: News, modifier: Modifier = Modifier) {
      Column(modifier = modifier.padding(16.dp)) { /* ... */ }
  }
  // Bad: 호출자 modifier를 뒤에 붙여 덮어쓴다
  Column(modifier = Modifier.fillMaxWidth().then(modifier))
  ```
- 체크: modifier 파라미터가 없거나 둘 이상인가. 루트가 아닌 자식에 전달 modifier를 붙였는가.

### R-17-06 파라미터 순서는 필수 → `modifier` → 선택 → 후행 `content` 람다다
- 규칙: 기본값 없는 필수 파라미터를 앞에, 그다음 `modifier`, 그다음 기본값 있는 선택 파라미터, 마지막에 `@Composable` 슬롯 람다를 둔다. 주 슬롯 이름은 `content`로 한다.
- 근거: 컴포넌트 파라미터 순서와 슬롯 규약 [S19](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md), API 지침의 슬롯 명명 [S18](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md)
- 예시:
  ```kotlin
  // Good
  @Composable fun Section(title: String, modifier: Modifier = Modifier, expanded: Boolean = false, content: @Composable () -> Unit)
  // Bad
  @Composable fun Section(modifier: Modifier = Modifier, content: @Composable () -> Unit, title: String)
  ```
- 체크: `modifier`가 첫 선택 파라미터 자리인가. 슬롯 람다가 마지막인가.

### R-17-07 컴포저블은 빠르고 멱등하며 부작용이 없어야 한다
- 규칙: 컴포저블 본문에서 전역·공유 상태를 쓰거나, 실행 순서·호출 횟수·실행 스레드를 가정하지 않는다. 컴포저블은 프레임마다 다시 실행될 수 있고 순서가 보장되지 않으며 병렬로 실행될 수 있다.
- 근거: 컴포저블은 순서 무관·병렬 실행 가능·프레임마다 실행될 수 있으므로 빠르고 멱등하며 부작용이 없어야 한다 [S14](https://developer.android.com/develop/ui/compose/mental-model)
- 예시:
  ```kotlin
  // Bad: 컴포지션 중에 공유 상태를 쓰고 네트워크를 부른다
  @Composable fun NewsList(items: List<News>) {
      analytics.log("shown")
      LazyColumn { items(items) { NewsCard(it) } }
  }
  // Good: 노출 로깅은 effect로
  LaunchedEffect(Unit) { analytics.log("shown") }
  ```
- 체크: 본문에 I/O·로깅·전역 변수 쓰기가 있는가. 컴포저블이 여러 번 실행돼도 결과가 같은가.

### R-17-08 부수효과는 전용 effect API 안에서만 실행한다
- 규칙: 컴포지션 수명과 묶인 작업은 `LaunchedEffect`, 정리가 필요한 등록·해제는 `DisposableEffect`, 사용자 이벤트 콜백에서 시작하는 suspend 작업(애니메이션·스크롤·스낵바)은 `rememberCoroutineScope`, 비 Compose 객체 갱신은 `SideEffect`, Compose 상태를 Flow로 볼 때는 `snapshotFlow`를 쓴다.
- 근거: effect API 종류와 용도 구분 [S15](https://developer.android.com/develop/ui/compose/side-effects), 컴포저블 본문의 부작용 금지 [S14](https://developer.android.com/develop/ui/compose/mental-model)
- 예시:
  ```kotlin
  // Good
  val scope = rememberCoroutineScope()
  Button(onClick = { scope.launch { listState.animateScrollToItem(0) } }) { /* ... */ }
  // Bad: 컴포지션 중에 코루틴을 띄운다
  GlobalScope.launch { listState.animateScrollToItem(0) }
  ```
- 체크: 콜백에서 `LaunchedEffect`를 쓰려 했는가. 컴포지션 중 코루틴을 직접 만들었는가.

### R-17-09 effect의 키는 effect가 읽는 값으로 정한다
- 규칙: `LaunchedEffect`·`DisposableEffect`가 참조하는 값은 key로 넘긴다. 값이 바뀌어도 effect를 재시작하고 싶지 않으면 `rememberUpdatedState`로 감싼다. 재시작이 필요 없을 때만 `Unit`을 key로 쓴다. `DisposableEffect`는 마지막 문장으로 `onDispose`를 반드시 둔다.
- 근거: effect가 쓰는 변수는 key로 넘기거나 `rememberUpdatedState`로 감싸고 `DisposableEffect`는 `onDispose`로 끝나야 한다 [S15](https://developer.android.com/develop/ui/compose/side-effects)
- 예시:
  ```kotlin
  // Good
  LaunchedEffect(userId) { viewModel.load(userId) }
  val currentOnTimeout by rememberUpdatedState(onTimeout)
  LaunchedEffect(Unit) { delay(5_000); currentOnTimeout() }
  // Bad: userId가 바뀌어도 첫 값으로 계속 돈다
  LaunchedEffect(Unit) { viewModel.load(userId) }
  ```
- 체크: key가 `Unit`인데 본문이 외부 값을 읽는가. `DisposableEffect`에 `onDispose`가 있는가.

### R-17-10 UiState와 컴포저블 파라미터는 불변 타입으로 유지한다
- 규칙: 상태로 쓰는 값은 읽기 전용 `List`·`Set`·`Map`과 불변 data class로 정의하고 갱신은 새 인스턴스로 교체한다. `ArrayList`·`MutableList`·`var` 프로퍼티를 UiState나 파라미터에 넣지 않는다.
- 근거: `ArrayList` 같은 비관찰 가변 객체를 상태로 쓰지 말고 불변 리스트를 담은 상태를 쓰라는 지침 [S13](https://developer.android.com/develop/ui/compose/state), 안정성을 높여 불필요한 재구성을 건너뛴다는 성능 개념 [S16](https://developer.android.com/develop/ui/compose/performance), 불변 컬렉션 타입 우선 [S58](https://kotlinlang.org/docs/coding-conventions.html)
- 예시:
  ```kotlin
  // Good
  data class NewsUiState(val items: List<News> = emptyList(), val isLoading: Boolean = false)
  _uiState.update { it.copy(items = it.items + news) }
  // Bad
  data class NewsUiState(val items: MutableList<News> = mutableListOf())
  ```
- 체크: UiState에 `var`나 가변 컬렉션이 있는가. 갱신이 기존 인스턴스를 변형하는가.

### R-17-11 재구성 비용은 `remember`·키·지연 읽기로 줄이고 backwards write를 만들지 않는다
- 규칙: 비싼 계산은 `remember(keys)`로 캐시하거나 컴포저블 밖(ViewModel·백그라운드)으로 옮긴다. lazy 레이아웃 아이템에는 안정적인 `key`를 준다. 자주 바뀌는 값은 `Modifier.offset { }`·`drawBehind { }` 같은 람다형 modifier로 읽기를 늦춘다. 이미 읽은 상태를 같은 컴포지션에서 다시 쓰지 않는다. `derivedStateOf`는 재구성 억제가 목적일 때만 쓴다.
- 근거: `remember`·lazy `key`·`derivedStateOf`·지연 읽기·backwards write 금지 [S17](https://developer.android.com/develop/ui/compose/performance/bestpractices), `derivedStateOf` 오용 경고 [S15](https://developer.android.com/develop/ui/compose/side-effects)
- 예시:
  ```kotlin
  // Good
  LazyColumn { items(items, key = { it.id }) { NewsCard(it) } }
  Box(Modifier.offset { IntOffset(0, scroll.value.roundToInt()) })
  // Bad: 컴포지션 중에 읽은 값을 다시 쓴다
  var count by remember { mutableStateOf(0) }
  Text("$count"); count++
  ```
- 체크: lazy 아이템에 `key`가 있는가. 컴포지션 본문에서 상태를 쓰는 자리가 있는가.

### R-17-12 구성 변경과 프로세스 재생성을 넘겨야 할 UI 상태는 `rememberSaveable`로 둔다
- 규칙: 컴포저블이 소유하는 상태 중 회전·프로세스 재생성 후에도 남아야 하는 값은 `rememberSaveable`을 쓴다. 화면 밖에서도 필요한 값이면 컴포저블이 아니라 ViewModel `UiState`에 둔다(위 결정 매트릭스).
- 근거: 구성 변경·프로세스 재생성 대비 상태 보존 [S13](https://developer.android.com/develop/ui/compose/state), 화면 단위 상태 홀더는 ViewModel이라는 계층 규정 [S08](https://developer.android.com/topic/architecture/ui-layer)
- 예시:
  ```kotlin
  // Good
  var query by rememberSaveable { mutableStateOf("") }
  // Bad: 회전하면 입력이 사라진다
  var query by remember { mutableStateOf("") }
  ```
- 체크: 회전 후 사라지면 곤란한 값이 `remember`에 있는가. 비즈니스 데이터를 컴포저블이 들고 있는가.

### R-17-13 이미지·아이콘은 `contentDescription`을 받아 채우고 장식 요소만 `null`로 둔다
- 규칙: 시각 정보를 전달하는 컴포넌트는 `contentDescription`을 파라미터로 노출하고 호출부가 값을 채운다. 의미 없는 순수 장식일 때만 명시적으로 `null`을 넘긴다. 값을 지우려고 빈 문자열을 쓰지 않는다.
- 근거: `Image`가 `contentDescription`을 필수 파라미터로 요구하며 흔한 접근성 요구는 파라미터로 받으라는 컴포넌트 지침 [S19](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md)
- 예시:
  ```kotlin
  // Good
  Icon(Icons.Default.Bookmark, contentDescription = bookmarkLabel)
  Image(painter = divider, contentDescription = null)   // 장식
  // Bad
  Icon(Icons.Default.Bookmark, contentDescription = "")
  ```
- 체크: 동작을 가리키는 아이콘에 설명이 비어 있는가. 장식 요소에 불필요한 설명이 붙었는가.

### R-17-14 화면은 디자인 시스템 모듈의 컴포넌트를 먼저 쓴다
- 규칙: 버튼·카드·다이얼로그 같은 공통 UI는 `:core:designsystem`(또는 동등한 디자인 시스템 모듈)의 컴포넌트를 쓴다. feature 모듈에서 Material 컴포넌트를 직접 스타일링해 새로 만들지 않고, 두 feature 이상이 쓰게 된 컴포넌트는 디자인 시스템 모듈로 올린다.
- 근거: `designsystem`을 core 모듈로 두는 구성과 "둘 이상이 쓰면 core로" 배치 기준 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md), 공통 코드의 common/core 모듈 패턴 [S04](https://developer.android.com/topic/modularization/patterns)
- 예시:
  ```kotlin
  // Good
  AppButton(text = confirmLabel, onClick = onConfirm)
  // Bad: 화면마다 같은 스타일을 다시 조립한다
  Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))) { Text(confirmLabel) }
  ```
- 체크: feature에 색·모양 리터럴이 있는가. 같은 컴포넌트가 두 feature에 복사됐는가.

### R-17-15 호이스팅한 상태 타입은 `@Immutable`, 관찰 가능한 가변 타입은 `@Stable`로 선언한다
- 규칙: UiState처럼 화면 밖으로 호이스팅한 상태 타입에 애노테이션을 붙인다. 프로퍼티가 전부 `val`이고 컬렉션이 읽기 전용이면 `@Immutable`, 가변이지만 변경을 Compose 런타임이 관찰할 수 있으면(`mutableStateOf` 위임 등) `@Stable`이다. `@Stable` 타입은 두 참조 `a`·`b`에 대해 `a.equals(b)`가 항상 같은 값을 돌려줘야 한다. 계약을 지킬 수 없으면 애노테이션을 붙이지 않는다(잘못된 선언은 화면이 갱신되지 않는 버그가 된다).
- 근거: "`@Immutable` indicates a type where the value of any properties will never change", "`@Stable` … the Compose runtime will be notified if and when any public properties or method behavior would yield different results", `equals()` 일관성 요구, 그리고 "SHOULD declare hoisted state types as `@Stable`" 규정 [S18](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md) — 원문은 이 SHOULD를 프레임워크·라이브러리 개발 대상으로 적었고, UiState가 화면의 호이스팅된 상태라 이 팩은 앱 코드에도 적용한다. 안정성이 불필요한 재구성을 건너뛰게 한다는 성능 근거 [S16](https://developer.android.com/develop/ui/compose/performance)
- 예시:
  ```kotlin
  // Good
  @Immutable
  data class NewsUiState(val items: List<News> = emptyList(), val isLoading: Boolean = false)
  @Stable
  class SearchFieldState { var query by mutableStateOf("") }   // 변경을 런타임이 관찰한다
  // Bad: 계약을 어긴 선언 — 값이 바뀌어도 재구성되지 않는다
  @Immutable
  data class NewsUiState(var items: MutableList<News>)
  ```
- 체크: `@Immutable`을 붙인 타입에 `var`나 가변 컬렉션이 있는가. `@Stable` 타입의 변경이 `mutableStateOf` 같은 관찰 가능한 경로로 일어나는가.
