# Kotlin 스타일·함수 설계·코루틴·에러 처리·Compose UI 조사 노트
조사일: 2026-09-09
담당 references: 17(Compose UI), 20(Kotlin 스타일), 21(함수 설계), 22(코루틴·Flow), 23(에러 처리)

## 출처

| # | 조직 | 문서명 | URL | 종류 | 최종 갱신 |
|---|---|---|---|---|---|
| S1 | Google | Kotlin style guide (Android) | https://developer.android.com/kotlin/style-guide | 공식 가이드 | 2023-09-06 (본문 표기) |
| S2 | JetBrains | Coding conventions | https://kotlinlang.org/docs/coding-conventions.html | 공식 가이드 | 확인 불가 |
| S3 | JetBrains | Scope functions | https://kotlinlang.org/docs/scope-functions.html | 공식 가이드 | 확인 불가 |
| S4 | JetBrains | Sequences | https://kotlinlang.org/docs/sequences.html | 공식 가이드 | 확인 불가 |
| S5 | JetBrains | Idioms | https://kotlinlang.org/docs/idioms.html | 공식 가이드 | 확인 불가 |
| S6 | JetBrains | Null safety | https://kotlinlang.org/docs/null-safety.html | 공식 가이드 | 확인 불가 |
| S7 | JetBrains | Exceptions | https://kotlinlang.org/docs/exceptions.html | 공식 가이드 | 확인 불가 |
| S8 | JetBrains | Coroutine exceptions (exception-handling) | https://kotlinlang.org/docs/exception-handling.html | 공식 가이드 | 확인 불가 |
| S9 | JetBrains | KEEP — `kotlin.Result` 제안서 | https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md | 공식 설계 문서 | 확인 불가 |
| S10 | Google | Best practices for coroutines in Android | https://developer.android.com/kotlin/coroutines/coroutines-best-practices | 공식 가이드 | 확인 불가 |
| S11 | Google | StateFlow and SharedFlow | https://developer.android.com/kotlin/flow/stateflow-and-sharedflow | 공식 가이드 | 확인 불가 |
| S12 | Google | Kotlin flows on Android | https://developer.android.com/kotlin/flow | 공식 가이드 | 확인 불가 |
| S13 | Google | UI events (Architecture) | https://developer.android.com/topic/architecture/ui-layer/events | 공식 가이드 | 확인 불가 |
| S14 | Google/AndroidX | Compose API guidelines | https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md | 오픈소스 설계 문서 | androidx-main 기준 |
| S15 | Google/AndroidX | Compose component API guidelines | https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md | 오픈소스 설계 문서 | androidx-main 기준 |
| S16 | Google | Thinking in Compose (mental model) | https://developer.android.com/develop/ui/compose/mental-model | 공식 가이드 | 확인 불가 |
| S17 | Google | Side-effects in Compose | https://developer.android.com/develop/ui/compose/side-effects | 공식 가이드 | 확인 불가 |
| S18 | Google | State and Jetpack Compose | https://developer.android.com/develop/ui/compose/state | 공식 가이드 | 확인 불가 |
| S19 | Google | Compose performance best practices | https://developer.android.com/develop/ui/compose/performance/bestpractices | 공식 가이드 | 확인 불가 |
| S20 | detekt | Complexity rule set (2.0.0-alpha.6) | https://detekt.dev/docs/rules/complexity | 오픈소스 문서 | 2.0.0-alpha.6 |
| S21 | detekt | Style rule set (2.0.0-alpha.6) | https://detekt.dev/docs/rules/style | 오픈소스 문서 | 2.0.0-alpha.6 |
| S22 | detekt | Complexity rule set (1.23.8, 안정판) | https://detekt.dev/docs/1.23.8/rules/complexity | 오픈소스 문서 | 1.23.8 |
| S23 | Kodeco | Kotlin style guide | https://github.com/kodecocodes/kotlin-style-guide | 오픈소스 README | 확인 불가 |
| S24 | Arrow | Working with typed errors | https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/ | 오픈소스 문서 | 확인 불가 |
| S25 | Square/Block | java-code-styles (아카이브됨) | https://github.com/square/java-code-styles | 오픈소스 저장소 | 아카이브·deprecated |
| S26 | Meta | ktfmt README | https://github.com/facebook/ktfmt | 오픈소스 README | 확인 불가 |

## 핵심 규칙 (출처별, 요약)

### S1 Google Kotlin style guide
- 열 제한 100자. 들여쓰기 4 스페이스, 탭 금지. 세미콜론 미사용, 한 줄 한 문장. [S1]
- 임포트는 정렬된 단일 목록, "No wildcard imports". [S1]
- `const` 스칼라 상수·커스텀 게터 없는 깊은 불변 `val`만 `UPPER_SNAKE_CASE`, `object`/최상위에만 정의. [S1]
- 백킹 프로퍼티는 밑줄 접두 + 공개 이름과 정확히 일치(`_table` ↔ `table`). [S1]
- 약어는 camelCase로 취급: `XmlHttpRequest`, `newCustomerId` (`XMLHTTPRequest`, `newCustomerID` 아님). [S1]
- KDoc은 "every `public` type and `public`/`protected` member" 최소 요구. 오버라이드는 예외. [S1]
- 함수 길이·파라미터 수·중첩 깊이에 대한 수치는 **침묵**. [S1]

### S2 JetBrains Coding conventions
- 클래스 본문 순서: 프로퍼티/초기화 블록 → 보조 생성자 → 메서드 → companion object. "put related stuff together". [S2]
- 파일은 "reasonable (not exceeding a few hundred lines)" 범위. `Util` 같은 무의미한 이름 금지. [S2]
- `val` 우선, 불변 컬렉션 타입(`List`/`Set`/`Map`) 우선, `listOf()` 우선. [S2]
- 오버로드보다 기본 인자 값 선호. 같은 원시 타입 다수·`Boolean` 파라미터는 named argument. [S2]
- 루프보다 고차 함수(`filter`, `map`) 선호. 단 `forEach`는 예외로 일반 `for` 루프 선호. [S2]
- 분기 3개 이상은 `when`, 2개는 `if`. 조건문은 표현식형(`return if (x) foo() else bar()`) 선호. [S2]
- 확장 함수는 대상 클래스와 같은 파일 또는 사용처 옆에, 가시성은 local/member/private top-level로 제한. [S2]
- 함수 대신 프로퍼티: 예외를 던지지 않고, 저렴하며, 상태 불변 시 같은 결과일 때. [S2]
- `infix`는 역할이 대칭인 두 객체에만(`and`, `to`, `zip`). 수신자를 변경하면 금지. [S2]
- KDoc에서 "Avoid `@param` and `@return` tags" — 본문 설명에 통합 권장. [S2]
- 라이브러리 코드: 가시성·반환 타입·프로퍼티 타입 항상 명시, public 멤버 KDoc 필수. [S2]
- 스코프 함수 선택은 S3로 위임. 체이닝 "단계 수"는 **침묵**(포맷만 규정). [S2]

### S3 JetBrains Scope functions
- 선택표: `let`(it/람다결과/확장) · `run`(this/람다결과/확장) · `with`(this/람다결과/인자) · `apply`(this/객체/확장) · `also`(it/객체/확장). [S3]
- 용도: `let`=non-null 실행·지역 변수화, `run`=설정+결과 계산, `with`=호출 묶기, `apply`=객체 설정, `also`=부가 효과. [S3]
- "avoid overusing them: it can make your code hard to read and lead to errors". [S3]
- "avoid nesting scope functions and be careful when chaining them". [S3]

### S4 JetBrains Sequences
- Iterable은 단계마다 중간 컬렉션 생성, Sequence는 원소 단위 지연 처리. [S4]
- "the lazy nature of sequences adds some overhead which may be significant when processing smaller collections or doing simpler computations". [S4]
- 컬렉션 크기·단계 수의 **수치 기준은 명시하지 않음(침묵)** — "consider both ... and decide which one is better for your case". [S4]

### S6/S7/S8/S9 Kotlin 널 안전·예외·Result
- `!!`는 "you are confident that a value is not `null` ... but the compiler cannot guarantee this" 상황용. **전면 금지 조항은 없음**. 대안은 `?.`, `?:`, `as?`. [S6]
- Kotlin은 checked exception 없음. `throw`는 `Nothing` 타입 표현식. `try`는 표현식. [S7]
- 예외를 `object` 선언으로 만들지 말고 매번 새 인스턴스 생성. 커스텀 예외 상위 클래스는 `open`. [S7]
- `require()`→`IllegalArgumentException`, `check()`/`error()`→`IllegalStateException`. `error()`는 `when`에서 유용. [S7]
- `catch` 블록은 구체 → 일반 순서. 리소스는 `try-finally`보다 `.use()`. [S7]
- `launch`는 예외 자동 전파, `async`는 `Deferred`에 저장해 `await()` 시 노출. [S8]
- `CoroutineExceptionHandler`는 루트 코루틴에서만 호출됨. 자식은 부모에 위임하므로 자식 컨텍스트의 핸들러는 "never used". [S8]
- `CancellationException`은 모든 핸들러가 무시하므로 디버그 정보 용도로만. 자식 다수 실패 시 "the first exception wins", 나머지는 suppressed. [S8]
- `SupervisorJob`/`supervisorScope`: 자식 실패가 부모·형제로 전파되지 않음. 자식이 스스로 처리해야 함. [S8]
- "The `Result` class is not designed to represent domain-specific error conditions." 로컬 처리 필요·의미 없는 실패는 nullable, 구분이 필요한 도메인 실패는 sealed class. [S9]

### S10/S11/S12/S13 Android 코루틴·Flow·이벤트
- "Don't hardcode `Dispatchers`" — 주입. suspend 함수는 "main-safe". [S10]
- ViewModel이 코루틴을 만들고 suspend 함수를 노출하지 않음. 가변 타입 노출 금지. [S10]
- data/business 레이어: one-shot은 suspend, 변화 알림은 Flow. 병렬 작업은 `coroutineScope`/`supervisorScope`. [S10]
- `GlobalScope` 회피(스코프 하드코딩). 취소는 협조적이므로 `ensureActive()`로 체크. [S10]
- "Never collect a flow from the UI directly from `launch` or the `launchIn`" — `repeatOnLifecycle(STARTED)` 사용. [S11]
- 노출은 `private MutableStateFlow` + `public StateFlow` 백킹 프로퍼티. cold→hot은 `stateIn`/`shareIn` + `SharingStarted.WhileSubscribed()`. [S11]
- `flow {}` 안에서 다른 컨텍스트로 `emit` 금지 — `flowOn`으로 업스트림 컨텍스트 변경. 예외는 `catch` 연산자. 콜백 소스는 `callbackFlow`. [S12]
- "ViewModel events—should always result in a UI state update." 일회성 이벤트를 Channel/reactive stream으로 내보내는 방식은 전달 보장이 없어 지양, 에러·메시지는 UI State 필드로 모델링. [S13]

### S14/S15/S16/S17/S18/S19 Compose
- Unit 반환 `@Composable`은 PascalCase 명사. `remember {}` 후 가변 객체를 반환하는 팩토리는 `remember` 접두. [S14]
- "MUST NOT expose any single `@Composable` function that both emits tree nodes and returns a value." [S14]
- `modifier: Modifier = Modifier` — 타입은 `Modifier` 자체, 첫 선택 파라미터, 컴포넌트당 1개, 루트 레이아웃에 적용. 뒤에만 연결하고 앞에 붙이지 않음. [S14][S15]
- 파라미터 순서: 필수 → `modifier` → 나머지 선택 → 후행 `@Composable content` 람다. [S15]
- `MutableState<T>` 파라미터는 "discouraged since it promotes joint ownership over a state" — `value: T` + `onValueChange: (T) -> Unit`. `State<T>` 대신 `T` 또는 `() -> T`. [S14][S15]
- 기본값은 최상위 `ComponentDefaults` object에. `null`을 "구현 기본값 사용" 신호로 쓰지 않음. `enabled: Boolean = true` 제공. [S15]
- 컴포저블은 순서 무관·병렬 실행 가능·프레임마다 실행될 수 있음. "keep your composable functions fast, idempotent, and side-effect free". 비싼 작업은 백그라운드 코루틴에서. [S16]
- 부작용은 `LaunchedEffect`/`rememberCoroutineScope`/`DisposableEffect`/`SideEffect`/`produceState`/`snapshotFlow`로. "`DisposableEffect` must include an `onDispose` clause as the final statement". effect가 쓰는 변수는 key로 넘기거나 `rememberUpdatedState`. [S17]
- 상태 호이스팅 3원칙: 읽는 컴포저블들의 최소 공통 부모까지, 변경되는 최고 레벨까지, 같은 이벤트로 바뀌는 두 상태는 함께. [S18]
- `ArrayList` 같은 비관찰 가변 객체를 상태로 쓰지 말고 `State<List<T>>` + 불변 `listOf()`. [S18]
- 비싼 계산은 `remember(keys)` 또는 컴포저블 밖으로. lazy 목록은 안정적 `key` 제공. 자주 바뀌는 값은 람다형 modifier(`offset {}`, `drawBehind {}`)로 읽기 지연. backwards write는 "recomposition to occur on every frame, endlessly". [S19]
- `derivedStateOf`는 재구성 억제 목적일 때만 — 단순 문자열 결합 등에는 "Overhead with no benefit". [S17][S19]

### S20/S21/S22 detekt 기본 임계값 (수치 폴백용)

| 규칙 | 1.23.8 (안정) [S22][S21] | 2.0.0-alpha.6 [S20][S21] |
|---|---|---|
| LongMethod | `threshold` 60 | `allowedLines` 60 |
| LongParameterList | 함수 6 / 생성자 7, `ignoreDefaultParameters` false | 함수 5 / 생성자 6 |
| NestedBlockDepth | 4 | 4 |
| TooManyFunctions | 파일·클래스·인터페이스·object·enum 각 11 | 각 11 |
| CyclomaticComplexMethod | 15 | 14 |
| LargeClass | 600 | 600 |
| ComplexCondition | 4 | 3 |
| MethodOverloading | 6 | 6 |
| NamedArguments | 3 | 3 |
| ReturnCount | max 2, `excludedFunctions` `['equals']` | 동일 |
| ThrowsCount | max 2 | 동일 |
| MaxLineLength | 120 | 동일 |
| LoopWithTooManyJumpStatements | `maxJumpCount` 1 | 동일 |
| DestructuringDeclarationWithTooManyEntries | 3 | 동일 |
| MagicNumber | `ignoreNumbers` `['-1','0','1','2']` | 동일 |
| ForbiddenComment | `['FIXME:','STOPSHIP:','TODO:']` | 동일 |

### S23/S25/S26 서드파티 스타일
- Kodeco: 줄 길이 100자이나 **블록 들여쓰기 2 스페이스**, 줄바꿈 들여쓰기 4. `!!`는 "only for instance variables that you know will be initialized before use". `when`은 "Always include the else case". 조건문은 항상 중괄호. [S23]
- Square/Block: 산문 형태의 공개 Kotlin 스타일 가이드 없음. `square/java-code-styles`는 아카이브·deprecated이며 "Block / Square has adopted ktfmt with its code style"라고 명시. [S25]
- ktfmt: 기본(Google 계열) 블록 들여쓰기 2 스페이스, KotlinLang 스타일은 4 스페이스. README에 열 제한 수치는 명시되지 않음. [S26]

## 질문별 출처 입장

| 질문 | 출처 입장 | 판정 |
|---|---|---|
| 컬렉션 체이닝 몇 단계까지 | S2는 포맷(점을 다음 줄, 1단계 들여쓰기)만 규정. S4는 "multiple steps"라고만 하고 수치 없음. detekt에 체인 길이 제한 규칙 없음 | **전 출처 침묵** — 팩이 자체 수치를 정해야 함 |
| 함수 길이 상한 | S1·S2·S23 모두 침묵. detekt `LongMethod` 60줄이 유일한 수치 | detekt 60줄만 근거 있음 |
| 파라미터 수 상한 | S1·S2 침묵. detekt 1.23.8 함수 6·생성자 7, 2.0-alpha 5·6 | detekt만 근거, **버전 간 값 불일치** |
| 중첩 깊이 상한 | S1·S2 침묵. detekt `NestedBlockDepth` 4 | detekt 4만 근거 |
| 스코프 함수 선택 | S3에 선택표 + 용도 + "avoid overusing"·"avoid nesting" 명시. S2는 S3로 위임 | 명확한 근거 있음 |
| `!!` 금지 | S6은 금지하지 않고 "컴파일러가 보증 못 할 때" 허용. S23은 "초기화가 보장된 인스턴스 변수에만". S1 침묵. detekt `UnsafeCallOnNullableType`은 이번에 문서 확인 실패 | **전면 금지를 명시한 출처 없음** — 팩 자체 결정 필요 |
| `when` 완전성(`else` 필수) | S23만 "Always include the else case" 명시. S2는 3분기 이상이면 `when` 권장·guard condition 문법만 언급, `else` 의무는 침묵. S1 침묵 | Kodeco만 근거, 공식 출처는 침묵 |
| Result vs 예외 | S9가 가장 강함: `Result`는 도메인 에러용 아님, 로컬 처리는 nullable, 구분 필요하면 sealed class, `Catching` 접미 함수에만. S24는 논리적 실패(도메인)와 기술적 예외를 구분. S7은 `Result`·`runCatching`을 **언급조차 안 함(침묵)** | 근거 충분, S7만 침묵 |
| 줄 길이 | S1 100자, S23 100자, detekt `MaxLineLength` 120 | 100 vs 120 충돌 |

## 출처 간 충돌

| 주제 | 입장 A | 입장 B | 팩 추천과 근거 |
|---|---|---|---|
| 블록 들여쓰기 | 4 스페이스 (S1, S2) | 2 스페이스 (S23 Kodeco, S26 ktfmt 기본) | **4 스페이스**. Android 공식(S1)과 Kotlin 공식(S2)이 일치하고 Android Studio 기본과도 맞음 |
| 최대 줄 길이 | 100자 (S1, S23) | 120자 (S21 detekt 기본) | **100자**. 공식 스타일 가이드 우선, detekt `MaxLineLength`를 100으로 재설정 |
| 파라미터 수 상한 | 함수 6·생성자 7 (S22, detekt 1.23.8) | 함수 5·생성자 6 (S20, detekt 2.0-alpha) | **함수 5·생성자 6**. 상위 버전이 더 엄격한 방향이고 alpha 값이 향후 기본이 됨. 안정판 사용 시 명시 설정 필요 |
| 복합 조건 수 | 4 (S22) | 3 (S20) | **3**. 위와 같은 근거 |
| 순환 복잡도 | 15 (S22) | 14 (S20) | **14**. 위와 같은 근거 |
| KDoc `@param`/`@return` | 블록 태그 순서를 규정하고 사용 전제 (S1) | "Avoid `@param` and `@return` tags" (S2) | **본문 통합 우선, 설명이 길 때만 태그**. S2가 더 구체적 지침이고 S1은 태그 사용 시의 형식만 규정하므로 실질 충돌이 아님 |
| 컬렉션 순회 | 루프보다 고차 함수 선호 (S2) | `forEach`는 예외로 일반 `for` 선호 (S2 동일 문서) | **체인 중간이거나 수신자가 nullable일 때만 `forEach`**, 단독 순회는 `for` |
| 일회성 이벤트 전달 | Channel/SharedFlow로 이벤트 전달 (S11의 SharedFlow 예시) | 일회성 이벤트를 UI State로 환원 (S13) | **UI State 환원**. S13이 이벤트 전달 주제를 직접 다루고 전달 보장 문제를 명시 |

## 팩 규칙 초안

| 후보 ID | 규칙 | 근거 | 확신도 |
|---|---|---|---|
| R-20-01 | 최대 줄 길이 100자 | S1, S23 | 높음 |
| R-20-02 | 들여쓰기 4 스페이스, 탭 금지 | S1, S2 | 높음 |
| R-20-03 | 와일드카드 import 금지, 정렬된 단일 import 목록 | S1 | 높음 |
| R-20-04 | 세미콜론 금지, 한 줄에 한 문장 | S1, S2 | 높음 |
| R-20-05 | 패키지 소문자·언더스코어 금지, 타입 PascalCase, 함수/프로퍼티 camelCase | S1, S2 | 높음 |
| R-20-06 | 상수는 `const` 또는 커스텀 게터 없는 최상위/`object` `val`만 `UPPER_SNAKE_CASE` | S1, S2 | 높음 |
| R-20-07 | 백킹 프로퍼티는 `_` 접두 + 공개 이름과 동일 | S1, S2 | 높음 |
| R-20-08 | 약어는 단어로 취급(`XmlHttpRequest`, `newCustomerId`) | S1, S2, S23 | 높음 |
| R-20-09 | Unit 반환 `@Composable`은 PascalCase 명사 | S1, S2, S14 | 높음 |
| R-20-10 | 클래스 본문 순서: 프로퍼티/init → 보조 생성자 → 메서드 → companion | S2 | 높음 |
| R-20-11 | public API는 가시성·반환 타입·프로퍼티 타입 명시 | S1, S2 | 높음 |
| R-20-12 | 모든 `public`/`protected` 타입·멤버에 KDoc(자명한 것·오버라이드 제외) | S1, S2 | 높음 |
| R-20-13 | KDoc은 본문 설명 우선, `@param`/`@return`은 길 때만 | S2 | 중간 |
| R-20-14 | 선언부에 trailing comma 사용 | S2 | 중간 |
| R-20-15 | `val` 우선, 불변 컬렉션 타입 우선(`listOf()`) | S2, S23 | 높음 |
| R-20-16 | 오버로드보다 기본 인자 값 | S2 | 높음 |
| R-20-17 | 같은 원시 타입 다중 파라미터·`Boolean` 인자는 named argument (인자 3개 초과 시 강제) | S2, S20 | 중간 |
| R-20-18 | 문자열 연결 대신 템플릿, `\n` 대신 멀티라인 + `trimIndent()` | S2 | 높음 |
| R-20-19 | 반열린 범위는 `..<` 사용(`0..n - 1` 금지) | S2 | 높음 |
| R-20-20 | 파일은 수백 줄 이내, `Util` 등 무의미한 파일명 금지 | S2 | 중간 |
| R-20-21 | modifier 키워드 순서는 S2 표를 따름 | S2 | 중간 |
| R-20-22 | 포매터를 CI에 고정(ktfmt KotlinLang 스타일 또는 4-스페이스 설정), 수동 정렬 금지 | S25, S26 | 중간 |
| R-20-23 | 마커 주석 `TODO:`/`FIXME:`/`STOPSHIP:`은 머지 전 제거 | S21 | 중간 |
| R-21-01 | 함수 본문 60줄 초과 금지 | S22, S20 | 중간 |
| R-21-02 | 함수 파라미터 5개·생성자 6개 초과 금지(기본값 파라미터도 계산) | S20, S22 | 중간 |
| R-21-03 | 중첩 블록 깊이 4 초과 금지 | S22, S20 | 중간 |
| R-21-04 | 함수당 `return` 2개 이하 — 가드 절은 초과 시 함수 분리 신호 | S21 | 중간 |
| R-21-05 | 함수당 `throw` 2개 이하 | S21 | 중간 |
| R-21-06 | 복합 조건 3개 초과 금지, 초과 시 이름 붙인 `val`로 분해 | S20 | 중간 |
| R-21-07 | 파일·클래스·인터페이스·object·enum당 함수 11개 이하 | S22, S20 | 중간 |
| R-21-08 | 순환 복잡도 14 이하 | S20, S22 | 중간 |
| R-21-09 | 클래스 600줄 초과 금지 | S22 | 중간 |
| R-21-10 | 오버로드 6개 초과 금지(기본 인자로 대체) | S20, S2 | 중간 |
| R-21-11 | 단일 표현식 함수는 expression body 사용 | S2, S21 | 높음 |
| R-21-12 | 루프보다 `filter`/`map` 등 고차 함수, 단독 순회는 `for`(`forEach` 아님) | S2 | 높음 |
| R-21-13 | 체이닝 단계 상한은 팩이 자체 규정(출처 침묵) — 초과 시 이름 붙인 중간 `val`로 분해 | 없음(S2·S4 침묵) | 낮음 |
| R-21-14 | 다단계 체인 + 큰 컬렉션에서만 `Sequence`, 소규모·단순 연산은 `Iterable` (수치 기준은 출처 침묵) | S4 | 중간 |
| R-21-15 | 스코프 함수는 S3 선택표대로: `apply`=설정, `also`=부가효과, `let`=non-null 실행, `with`=호출 묶기, `run`=설정+결과 | S3 | 높음 |
| R-21-16 | 스코프 함수 중첩 금지, 체이닝은 최소화 | S3 | 높음 |
| R-21-17 | 확장 함수는 대상 클래스와 같은 파일 또는 사용처 옆에 두고 가시성 제한 | S2 | 높음 |
| R-21-18 | `infix`는 역할이 대칭인 두 객체에만, 수신자를 변경하면 금지 | S2 | 높음 |
| R-21-19 | 예외 없음·저비용·상태 불변이면 함수 대신 프로퍼티 | S2 | 중간 |
| R-21-20 | 짧고 비중첩 람다는 `it`, 중첩 람다는 파라미터 명시 | S2 | 높음 |
| R-21-21 | 람다에서 레이블 return 남용 금지, 마지막 문장에는 사용 금지 | S2 | 중간 |
| R-21-22 | 분기 3개 이상은 `when`, 2개는 `if`; 조건은 표현식형으로 | S2 | 높음 |
| R-21-23 | 입력 검증은 `require()`, 상태 검증은 `check()`, 도달 불가 분기는 `error()`로 가드 | S7 | 높음 |
| R-21-24 | 구조 분해 3개 초과 금지 | S21 | 중간 |
| R-21-25 | 루프 내 `break`/`continue` 1개 이하 | S21 | 중간 |
| R-21-26 | 매직 넘버 금지(`-1`,`0`,`1`,`2` 제외), 이름 붙인 상수 사용 | S21 | 높음 |
| R-21-27 | 팩토리 함수는 클래스와 같은 이름을 피하고, 다중 생성자 오버로드보다 우선 | S2 | 중간 |
| R-22-01 | `Dispatchers` 하드코딩 금지, 생성자로 주입 | S10 | 높음 |
| R-22-02 | 모든 `suspend` 함수는 main-safe (블로킹은 `withContext`로 이동) | S10 | 높음 |
| R-22-03 | ViewModel은 코루틴을 생성하고 `suspend` 함수를 노출하지 않음 | S10 | 높음 |
| R-22-04 | 가변 타입 노출 금지 — `private MutableStateFlow` + `public StateFlow` | S10, S11 | 높음 |
| R-22-05 | data/business 레이어는 one-shot=`suspend`, 변화 알림=`Flow` | S10 | 높음 |
| R-22-06 | `GlobalScope` 사용 금지, 필요 시 외부 `CoroutineScope` 주입 | S10 | 높음 |
| R-22-07 | 장시간 루프·블로킹 작업은 `ensureActive()`로 협조적 취소 보장 | S10 | 높음 |
| R-22-08 | `CancellationException`을 잡지 말고 구체 예외만 catch | S10, S8 | 높음 |
| R-22-09 | 레이어 내부 병렬 작업은 `coroutineScope`/`supervisorScope`로 감쌈 | S10 | 높음 |
| R-22-10 | 테스트는 `TestDispatcher` 주입 + `runTest` | S10 | 높음 |
| R-22-11 | UI에서 Flow 수집은 `repeatOnLifecycle(STARTED)`/`flowWithLifecycle`, `launch`/`launchIn` 직접 수집 금지 | S11 | 높음 |
| R-22-12 | cold Flow의 hot 변환은 `stateIn`/`shareIn` + `SharingStarted.WhileSubscribed()` | S11 | 높음 |
| R-22-13 | `flow {}` 안에서 `withContext`로 `emit` 금지, 업스트림 컨텍스트는 `flowOn` | S12 | 높음 |
| R-22-14 | Flow 예외는 `catch` 연산자로 처리, 수집부 try/catch 남용 금지 | S12 | 높음 |
| R-22-15 | 콜백 기반 소스는 `callbackFlow` + `trySend`/`awaitClose` | S12 | 높음 |
| R-22-16 | `CoroutineExceptionHandler`는 루트 코루틴에만 설치(자식에 달아도 무시됨) | S8 | 높음 |
| R-22-17 | `async` 예외는 `await()` 시점에 발생 — 반드시 await하거나 스코프로 감쌈 | S8 | 높음 |
| R-22-18 | 형제 실패 격리가 필요하면 `SupervisorJob`/`supervisorScope` | S8 | 높음 |
| R-22-19 | `StateFlow`는 초기값이 필수이므로 Loading 등 초기 상태를 명시적으로 설계 | S11 | 중간 |
| R-23-01 | `!!` 사용은 컴파일러가 증명 불가한 경우로 한정, 기본은 `?.`·`?:`·`as?` (전면 금지는 어느 출처도 명시 안 함) | S6, S23 | 중간 |
| R-23-02 | `kotlin.Result`를 일반 함수의 반환 타입으로 쓰지 않음 | S9 | 높음 |
| R-23-03 | 의미 없는 단순 실패·부재는 nullable 타입으로 표현 | S9 | 높음 |
| R-23-04 | 구분·분기가 필요한 도메인 실패는 sealed class 계층으로 모델링 | S9, S7 | 높음 |
| R-23-05 | 도메인 논리 실패와 기술적 예외를 타입 수준에서 분리 | S24, S9 | 높음 |
| R-23-06 | 예외를 `object`로 선언하지 말고 매번 새 인스턴스 생성 | S7 | 높음 |
| R-23-07 | 커스텀 예외 상위 클래스는 `open`, 계층은 sealed로 | S7 | 중간 |
| R-23-08 | `catch` 블록은 구체 → 일반 순서로 배치 | S7 | 높음 |
| R-23-09 | 리소스 정리는 `try-finally`보다 `.use()` | S7 | 높음 |
| R-23-10 | Java에서 호출되는 public API는 `@Throws`로 예외 선언 | S7 | 중간 |
| R-23-11 | 에러는 UI State의 필드(`userMessage`, `errorMessage`)로 모델링하고 소비 후 `null`로 되돌림 | S13 | 높음 |
| R-23-12 | ViewModel 이벤트는 항상 UI State 갱신으로 귀결, Channel 기반 일회성 이벤트 지양 | S13 | 높음 |
| R-23-13 | 미구현 지점은 `TODO()`로 명시(무음 기본값 반환 금지) | S5, S7 | 중간 |
| R-17-01 | Unit 반환 `@Composable`은 PascalCase 명사, 값 반환은 camelCase | S14 | 높음 |
| R-17-02 | 내부에서 `remember`하고 가변 객체를 반환하는 팩토리는 `remember` 접두 | S14 | 높음 |
| R-17-03 | 한 컴포저블이 노드 방출과 값 반환을 동시에 하지 않음 | S14 | 높음 |
| R-17-04 | `modifier: Modifier = Modifier`를 첫 선택 파라미터로 정확히 1개, 루트 레이아웃에 적용 | S14, S15 | 높음 |
| R-17-05 | 전달받은 modifier 앞에 다른 modifier를 붙이지 않음(뒤에만 연결) | S14 | 높음 |
| R-17-06 | 파라미터 순서: 필수 → `modifier` → 선택 → 후행 `content` 람다 | S15 | 높음 |
| R-17-07 | 주 슬롯 이름은 `content`, 마지막 파라미터 위치 | S14, S15 | 높음 |
| R-17-08 | `MutableState<T>`/`State<T>` 파라미터 금지 — `value: T` + `onValueChange: (T) -> Unit` | S14, S15 | 높음 |
| R-17-09 | 활성/비활성 지원 컴포넌트는 `enabled: Boolean = true` 제공 | S15 | 중간 |
| R-17-10 | 기본값은 최상위 `XxxDefaults` object에 모음 | S15 | 높음 |
| R-17-11 | `null`을 "구현 기본값 사용" 신호로 쓰지 않음(부재 의미일 때만 nullable) | S15 | 높음 |
| R-17-12 | `CompositionLocal` 키 이름에 `Local`/`CompositionLocal` 접미 금지 | S14 | 중간 |
| R-17-13 | 컴포저블은 멱등·부작용 없음·빠름 — 컴포지션 중 공유 상태 쓰기 금지 | S16 | 높음 |
| R-17-14 | 컴포저블 실행 순서·스레드·호출 횟수에 의존하지 않음 | S16 | 높음 |
| R-17-15 | 비싼 계산은 `remember(keys)` 또는 컴포저블 밖(ViewModel/백그라운드)으로 | S19, S16 | 높음 |
| R-17-16 | lazy 레이아웃 아이템에 안정적 `key` 제공 | S19 | 높음 |
| R-17-17 | 자주 바뀌는 상태는 람다형 modifier(`offset {}`, `drawBehind {}`)로 읽기 지연 | S19 | 높음 |
| R-17-18 | backwards write 금지 — 상태 쓰기는 이벤트 람다 안에서만 | S19 | 높음 |
| R-17-19 | 상태 호이스팅 3원칙(최소 공통 부모·변경 최고 레벨·동반 상태 함께) 준수 | S18 | 높음 |
| R-17-20 | `ArrayList`·가변 data class 등 비관찰 객체를 상태로 쓰지 않음 | S18 | 높음 |
| R-17-21 | 부작용은 전용 effect API에서만 실행 | S17 | 높음 |
| R-17-22 | `DisposableEffect`는 마지막 문장으로 `onDispose` 필수 | S17 | 높음 |
| R-17-23 | effect가 참조하는 값은 key로 전달하거나 `rememberUpdatedState`로 감쌈 | S17 | 높음 |
| R-17-24 | `derivedStateOf`는 재구성 억제 목적에만 사용(단순 파생 값 금지) | S17, S19 | 높음 |
| R-17-25 | Compose State를 Flow로 관측할 때는 `snapshotFlow` 사용 | S17 | 중간 |
| R-17-26 | 상태를 갖지 않는(stateless) 컴포저블을 기본형으로, stateful은 얇은 래퍼로만 | S18 | 높음 |
| R-17-27 | 구성 변경·프로세스 재생성을 넘겨야 할 UI 상태는 `rememberSaveable` | S18 | 높음 |

## 미확인·접근 불가

- ktlint 코드 스타일 문서: `https://pinterest.github.io/ktlint/latest/rules/code-styles/` 와 `https://pinterest.github.io/ktlint/latest/rules/configuration-ktlint/` 및 `https://pinterest.github.io/ktlint/latest/` 모두 HTTP 404. `ktlint_official`/`intellij_idea`/`android_studio` 스타일별 `max_line_length` 기본값을 확인하지 못했다. 팩에서 ktlint 수치를 인용하려면 URL 재확인 필요.
- detekt exceptions 룰셋(`SwallowedException`, `TooGenericExceptionCaught`, `PrintStackTrace`)과 style 룰셋의 `UnsafeCallOnNullableType`, `SpreadOperator`, `UnnecessaryAbstractClass`: 이번에 조회한 페이지 응답에 포함되지 않아 기본값·존재 여부 미확인. "예외 삼키기 금지", "`!!` 정적 금지"를 규칙으로 올리려면 `https://detekt.dev/docs/rules/exceptions` 및 style 룰셋 재조회 필요.
- Square/Cash App 산문 Kotlin 스타일 가이드: 두 차례 검색(일반 검색 + Cash App 엔지니어링 블로그 대상)에서 공개 문서를 찾지 못했다. `square/java-code-styles`는 아카이브되었고 IntelliJ XML 설정만 포함하며 ktfmt로 이관되었다고 명시. **공개 산문 가이드는 존재하지 않는 것으로 판단**한다.
- ktfmt README에 스타일별 열 제한(100 등) 수치가 명시되어 있지 않아 인용하지 못했다. 필요 시 ktfmt 소스의 `FormattingOptions` 확인 필요.
- Arrow `https://arrow-kt.io/learn/typed-errors/` 인덱스 페이지는 응답이 얇아 타입 선택 지침을 얻지 못했고, 하위 페이지 `working-with-typed-errors/`에서만 논리적 실패 vs 예외 구분을 확보했다. `kotlin.Result`에 대한 Arrow의 비판은 **두 페이지 모두 언급 없음**.
- S1(Google Kotlin style guide)에서 "예외를 잡고 무시하지 말라" 조항을 확인하지 못했다. Java 스타일 가이드의 동일 조항과 혼동하지 말 것.
- 각 kotlinlang.org 페이지의 최종 갱신일은 페이지에 노출되지 않아 확인하지 못했다(S1만 2023-09-06 표기).
