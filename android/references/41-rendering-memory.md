# 41 렌더링·안정성·메모리와 성능 측정

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md·92-sources-2.md.

화면 이동·첫 진입의 버벅임, 재구성 비용, 메모리를 다룬다(2026-10-15 추가, 공식 원문 대조와 확인하지 못한 것은 research/performance-rendering-memory.md). 시작 시간·R8·Baseline Profile의 최소선은 40번이, `remember`·lazy `key`·람다 modifier 같은 재구성 기본은 17번(R-17-11)이, 애니메이션을 그리기 단계에서 하는 규칙은 28번(R-28-16)이 소유하며 여기서 다시 쓰지 않는다.
근거 줄의 **공식**은 Android·Kotlin 문서가 말하는 것이고, **제안**은 공식 문서가 값이나 방법을 정하지 않아 이 팩이 둔 것이다(사용자 위임, 2026-10-15, 91-decisions의 PERF_* 행). 제안 값은 첫 실기기 측정 뒤 다시 본다.

## 측정과 통과선

### R-41-01 "버벅임 없음"은 프레임 예산과 세 가지 통과선으로 판정한다
- 규칙: 프레임 예산은 기기 주사율의 프레임 간격이다(60Hz 16.7ms·90Hz 11ms·120Hz 8ms). 화면 이동·첫 진입·탭 전환은 동작마다 세 선을 모두 지켜야 "버벅임 없음"이다. ① 700ms를 넘는 프레임(frozen)은 0건. ② 예산을 넘긴 프레임이 전체의 5% 이하 — `frameOverrunMs` P95가 0 이하. ③ 가장 느린 1%도 예산의 2배 이내 — `frameOverrunMs` P99가 프레임 간격 이하. 시작은 콜드 500ms·웜 200ms·핫 150ms를 목표로 하고(R-40-02의 5초·2초·1.5초는 회귀 한계다) `timeToInitialDisplayMs` 중앙값으로 잰다. 전후 비교는 janky 프레임 개수가 아니라 프레임 시간 분포(P50·P90·P95·P99)로, 같은 기기·같은 OS에서 한다. 첫 프레임이 16ms를 넘는 것은 공식도 정상으로 보므로 ②·③이 그 허용 범위를 정한다.
- 근거: 공식 — 슬로 프레임은 "UI frames that take longer than 16ms to render", 프로즌은 "UI frames that take longer than 700ms to render"이고 "No frames in your app should ever take longer than 700ms to render"다. 같은 문서가 "During app startup or while transitioning to a different screen, it's normal for the initial frame to take longer than 16ms to draw"라고 적는다 [S310](https://developer.android.com/google/play/vitals/render). 예산은 "If you are trying to achieve 90 fps, then this window drops to 11ms, and for 120 fps it's 8ms" [S311](https://developer.android.com/topic/performance/issues/render). `frameOverrunMs`는 "Positive numbers indicate a dropped frame accompanied by visible jank or stutter"이고 P50·P90·P95·P99로 나오며(Android 12+) 최악은 P95·P99를 본다 [S314](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics). "The `RenderTime` metrics ... are more important than the count of janky frames for identifying regressions or improvements"이고 시작 목표는 콜드 500ms·웜 200ms·핫 150ms다 [S312](https://developer.android.com/topic/performance/measuring-performance). JankStats는 기본으로 "a frame taking twice as long to render as the current refresh rate"를 jank로 본다 [S315](https://developer.android.com/topic/performance/jankstats). Android vitals의 bad behavior 임계 표(충돌·ANR·배터리·메모리·DEX)에는 슬로 렌더링이 없다 [S331](https://developer.android.com/google/play/vitals). 제안(사용자 위임, 2026-10-15) — ②의 5%는 P95라는 측정 출력에 맞춘 값이고 ③의 2배는 JankStats 기본 정의에서 가져왔다. 두 값을 정한 공식 문서는 확인하지 못했다.
- 예시:
  ```text
  # Good — 한 동작을 10회 반복해 나온 분포로 판정한다 (120Hz, 예산 8.3ms)
  frameOverrunMs  P50 -6.1, P90 -2.0, P95 -0.5, P99 4.2    # P95 <= 0, P99 <= 8.3 → 통과
  # Bad — 평균 한 줄이나 "janky 프레임이 3개 줄었다"로 판정한다
  ```
- 체크: 판정에 쓴 값이 P95·P99 분포인가. 700ms 초과 프레임이 0인가. 비교가 같은 기기·OS의 전후인가. 통과선을 바꿨다면 91-decisions의 PERF_FRAME 행을 고쳤는가.

### R-41-02 프레임 측정은 동작마다 릴리스 상당 빌드·실기기·같은 조건에서 반복한다
- 규칙: 대상 동작은 화면 푸시, 팝(뒤로), 탭 전환(탭마다 첫 진입과 재진입), 콜드 스타트 뒤 첫 화면, 고빈도 값이 갱신되는 화면을 5초 유지하기이고 동작마다 따로 잰다(앱 전체를 한 수치로 합치지 않는다). 변이는 R8을 켠 non-debuggable·profileable 변이(`benchmark`, 플러그인이 만드는 `benchmarkRelease`는 R-40-03)여야 하고 디버그 수치로 판정하지 않는다(R-40-01). 실기기에서만 잰다. 컴파일 상태를 정하고 보고에 적는다 — 기본 `CompilationMode.DEFAULT`는 Baseline Profile이 있으면 설치하고, `Full`로 한 번 더 재면 인터프리터·JIT 영향을 가를 수 있다. 콜드 스타트 뒤 첫 화면은 `StartupMode.COLD`로 재고(런치 프레임이 들어간다), 화면·탭의 첫 진입은 `startupMode = null`로 두고 setup에서 `killProcess()`로 프로세스를 새로 띄워 시작 화면까지 연 뒤 measure에는 이동 동작만 넣는다. 재진입은 같은 프로세스에서 한 번 돌려 본 뒤 잰다. 반복은 동작마다 10회 이상(제안)이고 첫 진입과 재진입 값은 따로 보고한다. 화면 밝기를 고정하고 발열을 안정시킨 뒤 같은 기기에서 A/B로 비교하고, 비교 방법은 R-41-18이다.
- 근거: 공식 — 점검할 핵심 동작은 시작 흐름·스크롤 화면·"Transitions between screens"이고 매끄러워야 할 전환으로 "interactions such as switching between tabs or loading a new activity"를 든다 [S312](https://developer.android.com/topic/performance/measuring-performance). "Configure the benchmarked app as close to the release version or production as possible. Set it up as non-debuggable and preferably with minification on", "We discourage running the benchmarks on an emulator", 기본 컴파일 모드가 Baseline Profile을 설치하는 것, `StartupMode.COLD`는 setup과 measure 사이에 프로세스를 죽이므로 프로세스를 살려 두려면 "set `startupMode` to `null` and call `killProcess` within the `setupBlock`" [S313](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview). "Run A/B comparisons across the same device and same OS version"이고 "Compile your app on-device to a known state---generally `speed` for simplicity, or `speed-profile` to more closely match production performance"이며 jank 테스트에 `lockClocks`는 권하지 않는다 [S312](https://developer.android.com/topic/performance/measuring-performance). 측정 중 "lock screen brightness to a fixed value, maintain a stable device temperature, and close competing background processes"라는 권고는 PowerMetric 절에 있다 [S314](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics). 동작 목록·반복 10회·첫 진입과 재진입의 분리는 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 진단 탭 첫 진입만 잰다: setup 에서 프로세스를 새로 띄워 시작 화면까지 열고 measure 는 이동뿐이다
  benchmarkRule.measureRepeated(
      packageName = TARGET_PACKAGE, metrics = listOf(FrameTimingMetric()), iterations = 10,
      compilationMode = CompilationMode.DEFAULT, startupMode = null,
      setupBlock = { killProcess(); startActivityAndWait() },
  ) { /* 진단 탭으로 이동 */ }
  // Bad — 디버그 빌드·에뮬레이터에서 여러 동작을 한 번에 재서 평균 한 줄을 낸다
  ```
- 체크: 측정 변이가 R8을 켠 non-debuggable인가. 실기기인가. 동작별로 따로 쟀는가. 컴파일 상태(`CompilationMode`)가 보고에 있는가. 첫 진입 값에 런치 프레임이 섞이지 않았는가, 재진입과 분리했는가.

### R-41-03 프레임 측정은 게이트에 넣지 않고 릴리스 준비 때 수동 절차로 돌려 보고한다
- 규칙: 실기기가 필요하므로(R-41-02) `scripts/check.sh`의 게이트(R-31-01)와 pre-push 훅에 넣지 않는다. 릴리스 준비 때(R-31-09)와 화면 진입·전환·고빈도 갱신·레이어 효과·이미지를 건드린 변경을 낼 때 직접 돌린다. 절차 — ① R8 릴리스 상당 변이를 빌드해 기기에 설치 ② Macrobenchmark(`FrameTimingMetric`·`StartupTimingMetric`)를 벤치마크 모듈에서 돌리거나(일반 Macrobenchmark 모듈은 `connectedCheck`), 동작을 반복한 뒤 `adb shell dumpsys gfxinfo <패키지>`의 percentile·janky 값을 읽는다 ③ 통과선(R-41-01)을 넘으면 개별 트레이스를 열어 R-41-04로 간다 — 집계값만으로 원인을 찾지 않는다 ④ 기기 모델·OS·변이·컴파일 상태·동작별 지표를 표준 준수 보고(R-32-01)의 "테스트" 칸에 적고, 못 돌렸으면 통과가 아니라 "미실행"이라고 쓴다(`checklists/review.md`의 게이트 미실행 표기와 같다).
- 근거: 공식 — Macrobenchmark는 `com.android.test` 모듈에서 돌고 "We discourage running the benchmarks on an emulator"이며 `./gradlew :macrobenchmark:connectedCheck`로 실행한다 [S313](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview). "it's critical to manually debug individual test runs. You can't replace the preceding steps by analyzing aggregated data" [S312](https://developer.android.com/topic/performance/measuring-performance). `gfxinfo` 서비스는 "performance information relating to frames of animation"을 내고 `framestats`로 프레임별 시간을 더 본다 [S316](https://developer.android.com/tools/dumpsys). 게이트에서 빼는 것과 수동 절차의 순서는 제안(사용자 위임, 2026-10-15)이다. `gfxinfo` 출력 형식과 통계를 비우는 인자(`reset`), 그리고 Baseline Profile 플러그인이 만드는 `benchmarkRelease` 변이를 어느 태스크 이름으로 돌리는지는 공식 문서에서 확인하지 못했다.
- 예시:
  ```bash
  # Good — 릴리스 준비 때 기기를 연결하고 직접 돌린다
  ./gradlew :app:assembleRelease && ./gradlew :<벤치마크 모듈>:connectedCheck   # 태스크 이름은 모듈 구성에 따른다
  adb shell dumpsys gfxinfo com.example.app      # 동작을 반복한 뒤 percentile 읽기
  # Bad — 게이트(scripts/check.sh)나 pre-push 훅에 connectedCheck 를 넣는다
  ```
- 체크: 게이트 스크립트에 기기가 필요한 태스크가 없는가. 보고에 기기·OS·변이·컴파일 상태·지표가 있는가. 미실행을 통과로 쓰지 않았는가.

### R-41-04 느린 프레임은 단계(컴포지션·레이아웃·그리기)를 트레이스로 가른 뒤 고친다
- 규칙: 통과선(R-41-01)을 넘으면 먼저 그 프레임이 어느 단계에서 느린지 본다. 시스템 트레이스에서 `Choreographer#doFrame` 안의 Layout 구간과 `Record View#draw` 구간을 보고, 컴포저블 이름이 필요하면 composition tracing(`androidx.compose.runtime:runtime-tracing`)을 켠다. 재구성 횟수는 Layout Inspector로 본다. 트레이스도 R8 릴리스 상당·profileable·non-debuggable 변이에서 뜬다. 릴리스에서 컴포저블 이름까지 보려면 `runtime-tracing`·`tracing-perfetto`·`tracing-perfetto-binary`, profileable 매니페스트, `ENABLE_TRACING` 브로드캐스트, perfetto 설정의 `track_event`가 필요하고 이 의존은 임시라 끝나면 되돌린다. R8 릴리스의 이름이 난독화돼 읽히지 않으면 소비자 규칙 `-keepnames class ** { *; }`을 임시로 넣고(`-dontobfuscate`는 거부됐다) simpleperf는 매핑 파일로 역난독화한다 — 이 둘은 프로젝트 보고이고 공식 문서로 확인하지 못했다(미확인). 단계를 모른 채 `remember`·`derivedStateOf`·`key`를 화면 전체에 덧붙이지 않는다. 고칠 대상은 느린 첫 프레임 하나가 아니라 통과선을 넘는 분포다.
- 근거: 공식 — "To fix jank, inspect which frames aren't completing in 16ms and look for what's wrong. Check whether `Record View#draw` or `Layout` is taking abnormally long in some frames" [S311](https://developer.android.com/topic/performance/issues/render). "By default, system traces **do not** include individual composable functions", 정확한 측정에는 `profileable`·`non-debuggable`이 필요하다 [S322](https://developer.android.com/develop/ui/compose/tooling/tracing). "standard system tracing may not show which composables are the root cause. In these instances, use Jetpack Compose composition tracing" [S312](https://developer.android.com/topic/performance/measuring-performance). Layout Inspector는 재구성 횟수를 보여 주고 "Recomposition in itself is not bad; however, unexpected recomposition can be an issue" [S321](https://developer.android.com/develop/ui/compose/performance/tooling). 컴포지션·레이아웃·그리기 세 단계 [S323](https://developer.android.com/develop/ui/compose/phases). 공식은 진단 순서만 적고 단계를 가른 뒤에 고친다는 것과 근거 없이 덧붙이지 않는다는 것은 제안(사용자 위임, 2026-10-15)이다. 터미널 절차는 공식 문서에 있다 — `androidx.tracing:tracing-perfetto`·`tracing-perfetto-binary` 의존, `androidx.tracing.perfetto.action.ENABLE_TRACING` 브로드캐스트(`TracingReceiver`), perfetto 설정의 `track_event` 데이터 소스이고 "Don't ship `androidx.tracing:tracing-perfetto-binary` with your production app as it has a large effect on your app size" [S322](https://developer.android.com/develop/ui/compose/tooling/tracing).
- 예시:
  ```text
  # Good — 트레이스에서 단계를 확인한 뒤(레이아웃 22ms) composition tracing 으로 컴포저블을 찾는다
  # Bad — 원인을 모른 채 모든 화면에 remember·derivedStateOf 를 덧붙인다
  ```
- 체크: 고친 변경이 어느 단계의 어느 구간을 줄였는지 트레이스 전후로 말할 수 있는가. 디버그 빌드 트레이스로 판단했는가.

## 화면 진입(첫 프레임)

### R-41-05 화면의 첫 프레임에는 보이는 내용만 컴포지션한다
- 규칙: 항목이 많거나 길이를 모르는 내용은 `Column`이 아니라 `LazyColumn`·`LazyVerticalGrid`로 만들고(헤더·푸터는 `item {}`), 첫 프레임에 보이지 않는 무거운 블록(차트·`Canvas`·긴 텍스트)도 lazy 항목으로 둬 보일 때 컴포지션되게 한다. 항목 수가 정해진 소수의 내용은 `Column`이어도 된다. lazy 항목은 비동기로 채워지는 내용(이미지 등)에도 고정 크기나 placeholder를 줘 0px 항목이 되지 않게 하고, 종류가 둘 이상이면 `contentType`을 준다(`key`는 R-17-11). 한 `item {}`에 따로 보일 수 있는 요소를 여럿 몰지 않는다(구분선처럼 작은 것은 예외). 같은 방향 스크롤 컨테이너 안에 크기 없는 lazy 목록을 중첩하지 않는다. 중첩 깊이를 줄이려고 구조를 납작하게 만들지 않는다 — 비용은 R-41-04로 확인한다.
- 근거: 공식 — 스크롤이 없는 내용에는 "a simple `Column`"을 쓰라고 하지만 "using a layout such as `Column` can cause performance issues, since all the items will be composed and laid out whether or not they are visible"이고 lazy 컴포넌트는 "only compose and lay out items which are visible in the component's viewport". 0px 항목은 "would cause the Lazy layout to compose all of its items in the first measurement", `contentType`은 "Compose is able to reuse compositions only between the items of the same type", 한 item에 여럿을 두면 "If one element becomes visible on the screen, then all elements corresponding to the item have to be composed and measured" [S306](https://developer.android.com/develop/ui/compose/lists). 깊이에 대해서는 "Since Compose avoids multiple measurements, you can nest as deeply as you want without affecting performance" [S328](https://developer.android.com/develop/ui/compose/layouts/basics)이고 오버드로 문서도 평탄화는 "no longer a performance requirement"라고 적는다 [S327](https://developer.android.com/topic/performance/rendering/overdraw). 무거운 블록을 lazy 항목으로 두는 것과 소수 항목은 `Column` 허용의 경계는 제안(사용자 위임, 2026-10-15)이다. 텍스트·`Canvas`의 첫 그리기 비용 수치는 공식 문서에서 확인하지 못했다. 사례(트레이스 측정, 프로젝트 보고, S22+, 릴리스, 2026-10-15): 느린 프레임은 화면 진입 직후의 무거운 프레임 2~3개(푸시 20~25ms, 홈 팝 46~65ms)와 전환 끝의 14~19ms 프레임이고, 무거운 프레임의 55~65%가 `measureAndLayout`(그 안에서 지연 항목 컴포지션 약 35%, 측정·배치 약 45%, 텍스트 약 15%)이었다. 자기 시간은 한 곳에 몰리지 않고 Compose 기계 전체에 퍼졌고(레이아웃 노드 10.8%, 컴포지션 8%, 스냅샷 5.9%, 코루틴 4.9%, 애니메이션 3.5%, 텍스트 약 5%) 느린 프레임의 74%는 중간 코어의 평균 약 1.16GHz에서 돌았다. 노드 수에 비례하는 비용이라 코드로 쉽게 줄지 않고, 가장 큰 지렛대는 Baseline Profile(R-41-17)이다. 노드 수 자체를 줄이는 구조 변경이 다음 후보지만 그 효과는 측정하지 못했고, 화면을 여러 프레임에 나눠 구성하면 총 정지 시간은 같고 버벅임 프레임 수만 는다는 것은 트레이스에서 읽은 결론이며 나눠 구성한 변형을 재지는 않았다(미확인) — 둘 다 이 규칙의 근거가 아니다.
- 예시:
  ```kotlin
  // Good — 화면 아래의 차트 카드는 보일 때 컴포지션된다
  LazyColumn(contentPadding = contentPadding) {
      item(key = "summary", contentType = "summary") { SummaryCard(state.summary) }
      items(state.charts, key = { it.id }, contentType = { "chart" }) { ChartCard(it) }
  }
  // Bad — 화면 밖 차트까지 첫 프레임에 전부 컴포지션한다
  Column(Modifier.verticalScroll(rememberScrollState())) { state.charts.forEach { ChartCard(it) } }
  ```
- 체크: 화면 밖에 있는 무거운 블록이 `Column` 안에서 첫 프레임에 컴포지션되는가. lazy 항목에 `key`·`contentType`이 있고 비동기 내용에 고정 크기가 있는가. 한 `item {}`에 요소가 여럿인가.

### R-41-06 `BoxWithConstraints`·`SubcomposeLayout`은 컴포지션 안에서 부모가 준 제약이 필요할 때만 쓴다
- 규칙: 부모가 준 제약 값으로 서로 다른 컴포저블을 방출하거나 자식 컴포지션의 입력을 정해야 할 때만 쓴다. 창 크기에 따른 분기는 창 크기 클래스(R-18-13)로, 값이 필요 없는 배치는 `Modifier.layout`·비율 modifier·`weight`로 한다. 이 둘을 화면 루트나 목록 항목마다 습관적으로 감싸지 않는다. 쓴 화면에서 느린 프레임이 나오면 R-41-04로 레이아웃 구간을 확인한다.
- 근거: 공식 — "`BoxWithConstraints`, `LazyColumn`, and `LazyRow` are notable exceptions, where the composition of its children depends on the parent's layout phase" [S323](https://developer.android.com/develop/ui/compose/phases). `BoxWithConstraints`는 "In order to know the constraints coming from the parent and design the layout accordingly"에 쓰고 [S328](https://developer.android.com/develop/ui/compose/layouts/basics), `SubcomposeLayout`은 "Analogue of Layout which allows to subcompose the actual content during the measuring stage for example to use the values calculated during the measurement as params for the composition of the children" [S343](https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/package-summary). 이 둘이 얼마나 비싼지는 공식 문서에서 수치를 확인하지 못했다. 자식 컴포지션이 레이아웃 단계로 넘어가 일이 한 단계 늦게 몰린다는 점이 이유이고, 규칙은 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 부모가 준 폭이 입력이다(창 크기가 아니라 컨테이너 제약이 필요하다)
  BoxWithConstraints { Chart(labelCount = (maxWidth / 48.dp).toInt()) }
  // Bad — 창 크기 분기를 BoxWithConstraints 로 한다(R-18-13)
  BoxWithConstraints { if (maxWidth < 600.dp) Compact() else Wide() }
  ```
- 체크: `BoxWithConstraints`·`SubcomposeLayout` 안에서 제약 값이 컴포지션 입력으로 쓰이는가. 창 크기 분기를 R-18-13이나 비율 modifier로 낼 수 있는가.

## Compose 안정성

### R-41-07 같은 내용이면 같은 컬렉션 인스턴스를 방출하고, 구조 비교(`equals()`)는 비용이 싼 곳에만 쓴다
- 규칙: Kotlin 2.0.20 이상은 강한 건너뛰기가 기본이라 불안정 파라미터(`List`·`Set`·`Map` 포함)도 같은 인스턴스가 다시 오면 O(1) `===` 비교로 건너뛴다. 그래서 첫 대응은 타입을 바꾸는 것이 아니라 같은 내용이면 같은 인스턴스를 방출하는 것이다 — 틱·타이머 같은 빠른 스트림과 `combine`하는 안쪽에서 `map {}`·`toList()`·`filter {}`로 매번 새 목록을 만들면 내용이 같아도 그 파라미터를 받는 컴포저블이 매번 다시 실행된다. 목록 매핑은 원천 목록이 바뀔 때만 하고 결과 인스턴스를 재사용한다. 큰 컬렉션을 `ImmutableList`로 바꾸거나 컬렉션을 담은 클래스에 `@Immutable`·`@Stable`을 붙이는 것은 구조 비교가 정말 필요할 때만 한다 — 재구성마다 원소 전부를 비교하는 O(N) `equals()`가 재구성보다 비쌀 수 있다. 값이 같은 작고 납작한 모델이 자주 다시 만들어지는 경우는 R-41-08이다. R-17-15의 `@Immutable` UiState도 큰 컬렉션을 담으면 이 주의를 적용한다.
- 근거: 공식 — "With Strong skipping mode enabled (the default in Kotlin 2.0.20 and higher), Compose compares unstable parameters using instance equality (===) and skips the composable if the same object instances are passed", "If your app frequently allocates new instances of unstable parameters—or forces expensive .equals() checks on large collections by over-annotating models—you might observe unnecessary recompositions or comparison overhead", "unstable collections still skip recomposition using fast O(1) instance equality (===) when the collection reference does not change", "Avoid converting large collections to Kotlinx immutable collections or annotating collection-holding classes with @Immutable or @Stable unless structural equality (O(N) .equals()) is specifically required, as comparing every element on each recomposition can be more expensive than recomposition itself" [S317](https://developer.android.com/develop/ui/compose/performance/stability). "the Compose compiler always marks a parameter of type `List` as unstable, even when it is a collection of stable types"이고 불변 컬렉션 라이브러리는 문서가 "still in alpha"라고 적는다(현재 상태는 확인하지 못했다) [S319](https://developer.android.com/develop/ui/compose/performance/stability/fix). 불안정 파라미터는 `===`, 안정 파라미터는 `equals()`로 비교한다 [S320](https://developer.android.com/develop/ui/compose/performance/stability/strongskipping). `combine` 안쪽 매핑을 피하는 구성은 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 매핑은 목록이 바뀔 때만, 빠른 틱은 따로 흘려 같은 목록 인스턴스를 재사용한다
  val trips: StateFlow<List<TripUi>> = repo.trips.map { it.map(Trip::toUi) }.stateIn(/* … */)
  val tick: StateFlow<Int> = clock.ticks.stateIn(/* … */)
  // Bad — 틱마다 combine 안쪽에서 새 List 를 만들어 List 파라미터를 받는 컴포저블을 전부 다시 실행한다
  combine(repo.trips, clock.ticks) { l, t -> UiState(items = l.map(Trip::toUi), tick = t) }
  ```
- 체크: 빠른 스트림과 `combine`하는 안쪽에서 목록을 매번 새로 만드는가. 큰 컬렉션을 `ImmutableList`로 바꾸거나 `@Immutable`을 붙인 근거(구조 비교 필요·`equals()` 비용)가 있는가.

### R-41-08 Compose 컴파일러가 안 도는 모듈의 모델은 `===`로 비교되고, 값이 같은 작은 모델이 자주 새로 만들어질 때만 안정성 설정 파일에 올린다
- 규칙: `:core:model`처럼 Compose 컴파일러를 적용하지 않는 모듈의 클래스는 화면 모듈에서 불안정으로 취급되어 강한 건너뛰기 아래에서 `===`로 비교된다. 같은 인스턴스를 재사용할 수 있으면(R-41-07) 그대로 둔다. 데이터 소스가 값이 같은 작고 납작한 모델을 자주 다시 만들어 불필요한 재구성이 보이고(R-41-04·R-41-09) `equals()`가 싸면, 그 타입이 불변(`val`·읽기 전용 컬렉션, R-12-01·R-17-10)일 때에 한해 루트 안정성 설정 파일(`compose_stability.conf`)에 적고 `application.compose`·`library.compose` 컨벤션 플러그인(R-10-10)이 `composeCompiler { stabilityConfigurationFiles.add(...) }`로 같은 파일을 꽂는다. 다른 길은 그 모듈에 Compose 런타임만 의존해 `@Immutable`·`@Stable`을 붙이는 것(R-17-15)과 화면 쪽 UI 모델로 감싸는 것이다. 고르는 기준은 클래스 수와 사용처다(R-10-04). `kotlin.collections.*`를 적으면 모든 컬렉션을 안정으로 보는 계약이고 큰 컬렉션의 `equals()` 비용도 따라오므로(R-41-07) 쓰지 않는 것이 기본이다.
- 근거: 공식 — "Compose considers classes from modules where the Compose compiler does not run to be unstable (compared using === under Strong skipping mode). If a data source frequently re-instantiates small, flat models from non-Compose modules with identical values, you can configure a stability configuration file or use @Stable or @Immutable where .equals() is cheap" [S317](https://developer.android.com/develop/ui/compose/performance/stability). "The Compose compiler can only infer whether a class is stable if all of the non-primitive types that it references are either explicitly marked as stable or in a module that was also built with the Compose compiler", 해법은 설정 파일·`@Stable`/`@Immutable` 태그·UI 래퍼 셋이다. 설정 파일은 "Beginning with Compose Compiler 1.5.5"부터이고 "These configurations don't make a class stable on its own. Instead, by using these configurations, you opt in to a contract with the compiler" [S319](https://developer.android.com/develop/ui/compose/performance/stability/fix). Gradle 플러그인 API에 `stabilityConfigurationFile`과 `stabilityConfigurationFiles`(목록)가 있다 [S339](https://kotlinlang.org/api/kotlin-gradle-plugin/compose-compiler-gradle-plugin/org.jetbrains.kotlin.compose.compiler.gradle/-compose-compiler-gradle-plugin-extension/). 작고 납작한 모델에 한정하는 기준과 설정 파일의 위치는 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```text
  // compose_stability.conf — 값이 같은데 자주 다시 만들어지는 작고 납작한 불변 모델만 적는다
  com.example.app.core.model.Trip
  ```
  ```kotlin
  // Good — application.compose·library.compose 컨벤션 플러그인이 같은 파일을 꽂는다
  composeCompiler { stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("compose_stability.conf")) }
  // Bad — 가변 필드를 가진 타입까지 적어 값이 바뀌어도 화면이 갱신되지 않는 버그를 만든다
  ```
- 체크: 설정 파일에 적힌 타입이 작고 납작하고 불변이며 `equals()`가 싼가(`var`·가변·큰 컬렉션 없음). 적은 이유가 리포트·재구성 횟수로 확인된 재구성인가. 설정을 모듈마다 복사해 갈라졌는가.

### R-41-09 안정성은 릴리스 컴파일러 리포트로 확인하고 모든 컴포저블을 skippable로 만들려 하지 않는다
- 규칙: 느린 화면(R-41-04)에서 불필요한 재구성이 보일 때만 안정성을 판다. ① 강한 건너뛰기가 꺼져 있지 않은지(`ComposeFeatureFlag.StrongSkipping.disabled()`나 `enableStrongSkippingMode = false`가 없어야) 본다 ② `composeCompiler { reportsDestination; metricsDestination }`로 리포트를 만들되 릴리스 변이로 만든다 ③ `*-composables.txt`의 `unstable` 파라미터와 `*-classes.txt`의 `unstable class`를 찾되 먼저 같은 값의 새 인스턴스가 매번 할당되는지 본다 ④ R-41-07·R-41-08·R-17-15로 고친다. 호출이 드문 컴포저블, 건너뛸 수 있는 컴포저블만 부르는 컴포저블, 비싼 `equals`를 가진 파라미터가 많은 컴포저블은 skippable로 만들려 하지 않는다.
- 근거: 공식 — 리포트는 `reportsDestination`·`metricsDestination`으로 켜고 "Make sure to always run this on a release build to ensure accurate results", "Trying to make your entire UI skippable is a premature optimization that could lead to maintenance difficulties in the future" [S318](https://developer.android.com/develop/ui/compose/performance/stability/diagnose). "you shouldn't attempt to make every composable skippable" 이고 skippable은 "a small overhead"를 더한다 [S319](https://developer.android.com/develop/ui/compose/performance/stability/fix). "If your app is recomposing a composable whose parameters have not changed, first check whether new instances of unstable types are being allocated on every recomposition" [S317](https://developer.android.com/develop/ui/compose/performance/stability). 끄는 방법은 `featureFlags = setOf(ComposeFeatureFlag.StrongSkipping.disabled())` [S338](https://kotlinlang.org/docs/compose-compiler-options.html)이고 Gradle 플러그인 API에는 `enableStrongSkippingMode` 속성도 있다 [S339](https://kotlinlang.org/api/kotlin-gradle-plugin/compose-compiler-gradle-plugin/org.jetbrains.kotlin.compose.compiler.gradle/-compose-compiler-gradle-plugin-extension/).
- 예시:
  ```text
  # Good — 리포트(릴리스)에서 원인 파라미터를 짚는다
  restartable scheme("[androidx.compose.ui.UiComposable]") fun TripList(unstable items: List<TripUi>)
  # Bad — unstable 이 보인다고 모든 UiState·컴포저블에 @Immutable·@Stable 을 붙인다
  ```
- 체크: 안정성을 고치기 전에 릴리스 리포트로 원인을 짚었는가. `@Immutable`·`@Stable`을 붙인 타입이 계약을 지키는가(R-17-15).

### R-41-10 자주 바뀌는 값은 UiState에서 떼어 내고 읽는 범위를 좁힌다
- 규칙: 틱·수신 데이터처럼 초당 여러 번 바뀌는 값을 화면 UiState의 필드로 두면 값이 바뀔 때마다 UiState를 받는 컴포저블이 다시 실행된다. ① 그 값은 그것을 그리는 작은 컴포저블 하나만 읽게 한다 — 별도 `StateFlow`를 그 컴포저블 가까이에서 수집하거나(R-22-08), 값을 `() -> T`로 내려 레이아웃·그리기 단계에서 읽는다(`Modifier.offset { }`·`drawBehind { }`, R-17-11). 분리는 R-12-01이 요구하는 이유(통과선을 넘긴 측정, R-41-01)를 KDoc에 남긴다. ② 화면이 구분하지 못하는 변화는 원천에서 줄인다 — `StateFlow`는 이미 같은 값을 건너뛰므로 `distinctUntilChanged`를 또 붙이지 않고, `StateFlow`가 아닌 `Flow`에는 `distinctUntilChanged()`를, 사람이 못 읽는 빈도에는 `sample`(`@FlowPreview`, 옵트인 필요)을 쓴다. 표시 갱신 주기 값은 정하지 않는다 — R-41-01 측정으로 정한다. ③ 수 초마다 바뀌는 수치 텍스트는 R-28-16대로 애니메이션 없이 즉시 교체한다.
- 근거: 공식 — "When you are passing frequently changing State variables into modifiers, you should use the lambda versions of the modifiers whenever possible", "You should be suspicious if you are causing recomposition just to re-layout or redraw a Composable" [S17](https://developer.android.com/develop/ui/compose/performance/bestpractices). StateFlow는 "Values in state flow are conflated using Any.equals comparison in a similar way to distinctUntilChanged operator" [S340](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/), `distinctUntilChanged`는 "any instance of StateFlow already behaves as if distinctUntilChanged operator is applied to it" [S342](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/distinct-until-changed.html), `sample`은 "emits only the latest value emitted by the original flow during the given sampling period"이고 `@FlowPreview`다 [S341](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/sample.html). 틱을 UiState에서 떼어 내는 구성 자체는 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 틱은 그것을 그리는 컴포저블만 읽는다
  @Composable fun SpeedText(speed: () -> Int) { Text("${speed()}") }
  SpeedText(speed = { speedState.value })           // 부모는 틱 때문에 다시 실행되지 않는다
  // Bad — 틱 필드가 화면 UiState 에 있어 값이 바뀔 때마다 화면 전체가 다시 실행된다
  data class DashboardUiState(val cards: List<Card>, val speed: Int)
  ```
- 체크: 초당 여러 번 바뀌는 값이 화면 UiState 필드인가. 그 값을 읽는 컴포저블이 화면 전체인가. `StateFlow`에 `distinctUntilChanged`를 또 붙였는가.

## 렌더링 비용

### R-41-11 레이어를 만드는 효과는 필요한 요소에만 걸고, 오래 유지되는 상태 표현을 큰 컨테이너의 alpha로 하지 않는다
- 규칙: `alpha`가 1 미만인 `Modifier.alpha`·`graphicsLayer`는 그 요소를 오프스크린 버퍼로 그리고 `Modifier.shadow`는 별도 그래픽 레이어로 그린다. ① 색·글자의 투명도는 색에 알파를 넣는다(`Color.copy(alpha)`) — 같은 효과를 더 낮은 비용으로 낸다. ② 값이 애니메이션되는 투명도는 `Modifier.alpha(값)`가 아니라 `graphicsLayer { alpha = … }` 람다로 하고(R-28-16), 겹치는 내용이 없으면 `compositingStrategy = CompositingStrategy.ModulateAlpha`로 오프스크린 버퍼를 피한다. ③ 눌림·비활성 같은 오래 유지되는 상태를 화면 전체·배경을 포함한 큰 컨테이너의 alpha<1로 표현하지 않는다(색·내용 쪽으로 옮긴다). R-28이 정한 화면 전환·`Crossfade`·오버레이 페이드는 전환 시간 동안만 쓰이므로 예외이고 그 비용은 R-41-02로 잰다. ④ alpha<1 레이어는 기본으로 그 컴포저블의 레이아웃 경계까지만 담으므로 경계 밖을 그리는 내용(그림자·글로우·`offset`된 그리기)은 잘린다 — 그런 요소에는 alpha를 걸지 않거나 레이어 경계를 넓히고(`LayerOutsets`는 Compose 1.12.0-alpha01+라 이 팩의 BOM에 있는지는 미확인), `clip`·`drawBehind`를 섞은 조합은 기기에서 눈으로 확인한다(R-28-01 체크처럼). 사례: `InfocarEV_Android`에서 clip + drawBehind + alpha 조합이 기기에서 배경이 사라지는 결함이 됐고 원인은 확인하지 못했다 — ④의 공식 문장이 가능한 설명이다(가설).
- 근거: 공식 — `Modifier.alpha`: "Usage of this API renders this composable into a separate graphics layer. Note when an alpha less than 1.0f is provided, contents are implicitly clipped to their bounds ... This layer is sized to the bounds of the composable this modifier is configured on", 애니메이션에는 "it is highly recommended to use Modifier.graphicsLayer instead" [S325](https://developer.android.com/reference/kotlin/androidx/compose/ui/draw/alpha.modifier). "Whenever the alpha is less than 1f, a compositing layer is created automatically", `ModulateAlpha`는 "It won't create an offscreen buffer for alpha less than 1.0f ... However, it can provide different results for overlapping content", 그리고 "the offscreen texture defaults to the composable's measured layout bounds. Any drawing or shadow inside the layer that extends outside the composable's layout bounds" 는 "implicitly clipped at the boundary of the offscreen buffer"이며 "LayerOutsets is available from Compose 1.12.0-alpha01+" [S324](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers). `Modifier.shadow`는 "Usage of this API renders this composable into a separate graphics layer" [S326](https://developer.android.com/reference/kotlin/androidx/compose/ui/draw/shadow.modifier). 투명 픽셀은 "a key contributor to overdraw"이고 반투명 회색 글자는 `Modifier.alpha` 대신 "directly setting the composable's text color"가 "better performance"다 [S327](https://developer.android.com/topic/performance/rendering/overdraw). ③의 큰 컨테이너 상태 표현 금지와 alpha가 1과 1 미만을 오갈 때 레이어가 생기고 사라지는 비용은 공식 문서에서 수치를 확인하지 못했고 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 색에 알파를 넣는다 / 겹침 없는 애니메이션 알파는 람다 + ModulateAlpha
  Text(label, color = Color.Black.copy(alpha = 0.5f))
  Box(Modifier.graphicsLayer { alpha = fade.value; compositingStrategy = CompositingStrategy.ModulateAlpha })
  // Bad — 눌림 상태를 전체 화면 컨테이너의 alpha 로 표현한다
  Box(Modifier.fillMaxSize().alpha(if (pressed) 0.6f else 1f)) { Content() }
  ```
- 체크: `Modifier.alpha`에 애니메이션·상태 값을 넘기는가. 화면 전체나 큰 영역의 상태 표현에 alpha<1이 쓰이는가(R-28의 전환·`Crossfade` 제외). alpha가 걸린 요소 안에 경계 밖을 그리는 내용이 있는가. 기기에서 눈으로 확인했는가.

### R-41-12 그리기 객체는 한 번만 만들고 불투명 배경·그림자·클립은 필요한 곳에만 둔다
- 규칙: `Canvas`·`drawBehind`·`drawWithContent` 안에서 `Brush`·`Path`·`Shader`·포맷터를 그릴 때마다 만들지 않는다. 크기에 따라 정해지는 객체는 `drawWithCache`로, 크기와 무관하면 `remember`나 상수로 만든다. 캐시할 객체가 없는데 `drawWithCache`를 쓰지 않는다(람다만 할당된다). 가려지는 배경(부모와 자식이 같은 색을 겹쳐 칠함)은 지우고 앱 바탕색은 한 군데에 둔다. 그림자·반투명은 보이는 것에만 둔다. `clip`·`clipToBounds`의 비용 수치는 공식 문서에서 확인하지 못했으므로 모든 요소에 습관으로 걸지 않고 필요한 곳(둥근 이미지 등)에만 건다. 오버드로는 "GPU 오버드로 디버그"와 Layout Inspector로 보되, 공식 권고대로 UI 스레드 작업(R-41-04)이 먼저다.
- 근거: 공식 — `drawWithCache`는 "it avoids the need to reallocate objects (such as: `Brush, Shader, Path` etc.) that are created on draw"이고 "Using this modifier without needing to cache objects, can result in unnecessary lambda allocations" [S324](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers). "you might instantiate `Brush`, `Path`, or data formatting objects during a composable's recomposition or inside a `DrawScope` call (such as `Canvas`). In both cases, the app creates a lot of objects quickly" [S329](https://developer.android.com/topic/performance/memory/manage-app-memory). 오버드로는 "Remove unnecessary backgrounds in layouts"와 "Reduce transparency"로 줄이고 "we recommend instead focusing on optimizing UI thread work" [S327](https://developer.android.com/topic/performance/rendering/overdraw). 배경 정리와 클립 사용 범위의 경계는 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 크기가 같은 동안 Brush 를 재사용한다
  Modifier.drawWithCache { val brush = Brush.verticalGradient(colors); onDrawBehind { drawRect(brush) } }
  // Bad — 그릴 때마다 만든다(프레임마다 할당)
  Modifier.drawBehind { drawRect(Brush.verticalGradient(colors)) }
  ```
- 체크: `Canvas`·`drawBehind` 안에서 `Brush`·`Path`를 만드는가. 같은 영역을 칠하는 배경이 겹쳐 있는가. 바뀌지 않는 값에 `drawWithCache`를 쓰는가.

## 메모리

### R-41-13 이미지는 표시 크기로 디코딩하고 아이콘은 벡터를 쓴다
- 규칙: 이미지를 그리는 컴포저블에는 크기(`Modifier.size`·비율)를 준다 — `wrapContentSize`나 무제약이면 로더가 대상 크기를 몰라 원본을 디코딩한다. 컴포저블에는 `Painter` 대신 URL이나 `@DrawableRes Int`를 넘기고 이미지 로더(Coil 등)가 요청 크기에 맞춰 디코딩하게 한다. 아이콘·단순 도형은 벡터를 쓰고 큰 래스터를 앱에 넣지 않는다. 같은 이미지의 중복 디코딩은 heap dump의 duplicate bitmaps로 확인한다. 불투명 이미지의 `RGB_565`(메모리 절반)는 화질이 달라지므로 선택 항목이다. 앱에 이미지 로딩이 없으면 이 규칙의 대상이 아니다 — 없는 라이브러리를 미리 넣지 않는다.
- 근거: 공식 — "Graphic bitmaps are usually the largest common objects residing in your app's memory" [S329](https://developer.android.com/topic/performance/memory/manage-app-memory). "Avoid using `wrapContentSize` or leaving dimensions unconstrained on composables that load remote images. If these libraries cannot infer the target bounds, they fall back to loading the original full-size image", "the `Painter` API is explicitly not marked as stable", "Use `RGB_565` when you don't need transparency; this format uses half the memory of the default `ARGB_8888` format", 벡터는 "much smaller than a bitmap" [S330](https://developer.android.com/develop/ui/compose/graphics/images/optimization). Android vitals 비트맵 점검표의 Reduce·Reuse·Recycle [S334](https://developer.android.com/topic/performance/issues/bitmap-memory-usage). `Painter` 대신 URL·`@DrawableRes`를 넘기는 것은 공식 권고이고, 이미지 로딩이 없는 앱은 대상이 아니라는 것과 `RGB_565`를 선택 항목으로 두는 것은 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 크기가 정해져 로더가 그 크기로 디코딩한다
  AsyncImage(model = url, contentDescription = label, modifier = Modifier.size(96.dp))
  // Bad — 크기가 없어 원본 해상도를 디코딩한다
  AsyncImage(model = url, contentDescription = label, modifier = Modifier.wrapContentSize())
  ```
- 체크: 이미지 컴포저블에 크기가 있는가. `Painter`를 파라미터로 받는가. 큰 래스터가 아이콘 자리에 있는가.

### R-41-14 ViewModel과 컬렉션의 상주 범위를 정해 두고 측정한다
- 규칙: ① 엔트리 스코프 ViewModel은 그 엔트리가 백스택에 있는 동안 산다(R-13-05). 하단 탭처럼 백스택이 여럿이면 비활성 탭의 엔트리·ViewModel이 남는지 사라지는지를 `NavigationState` 설계가 정하고 한 줄로 문서에 남긴다 — 남으면 재진입이 빠르고 메모리와 구독이 남고, 사라지면 탭을 바꿀 때마다 화면을 처음부터 컴포지션한다(첫 진입 비용이 반복된다). 어느 쪽이든 R-41-02의 "탭 첫 진입·재진입"으로 잰다. ② 화면이 보이지 않는 동안 구독을 멈춘다(R-12-06·R-22-08). ③ 큰 컬렉션을 UiState·싱글턴에 통째로 쌓지 않는다 — 보이는 만큼만 넘기고(`Paging`, R-12-09), 상한 없이 쌓이는 목록(로그·측정 이력)에는 상한을 둔다.
- 근거: 공식 — "This ensures that the `ViewModel`'s state is retained only while that particular `NavEntry` is part of the back stack, and is cleared when the `NavEntry` is popped" [S22](https://developer.android.com/guide/navigation/navigation-3/save-state). 멀티 백스택 레시피는 "Navigating to a top level route that is not the starting route *replaces* the other entries. For example, navigating A->B->C would result in entries for A+C, B's entries are removed" [S281](https://developer.android.com/guide/navigation/navigation-3/recipes/multiple-backstacks). 데코레이터는 "clears its associated `ViewModelStore`" [S23](https://developer.android.com/guide/navigation/navigation-3/naventrydecorators). 큰 데이터는 Paging으로 "loading and displaying small chunks of the list as necessary" [S306](https://developer.android.com/develop/ui/compose/lists). 탭 상주 여부에 정답이 있다고 공식이 적지 않았고 상한 값도 정하지 않았다 — 측정으로 정하는 것은 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 누적 이력은 상한을 둔다
  _history.update { (it + sample).takeLast(MAX_HISTORY) }
  // Bad — 앱이 켜져 있는 동안 계속 쌓는다
  _history.update { it + sample }
  ```
- 체크: 비활성 탭의 ViewModel 상주 여부가 문서에 적혀 있는가. 상한 없이 쌓이는 컬렉션이 UiState·싱글턴에 있는가. 보이지 않는 화면이 계속 구독하는가.

### R-41-15 디버그 빌드에서만 StrictMode와 LeakCanary를 켠다
- 규칙: 디버그 소스셋의 App Startup `Initializer`(R-18-02)에서 `StrictMode` ThreadPolicy·VmPolicy를 `penaltyLog`로 켜 메인 스레드의 디스크·네트워크 접근과 누수 클래스를 잡고(`Application.onCreate`는 열지 않는다, R-18-01), `leakcanary-android`는 `debugImplementation`으로만 넣는다(버전은 카탈로그, R-10-12). 릴리스 소스셋·릴리스 의존에는 두지 않는다. 위반을 전부 고칠 필요는 없다 — 우연히 한 것을 찾는 도구이고 R-41-02의 대상 동작에서 나온 위반을 먼저 본다. LeakCanary는 파괴된 Activity·Fragment·ViewModel·Service가 메모리에 남는 경우를 잡는다. 디버그 빌드 수치로 프레임·메모리를 판정하지 않는다(R-40-01).
- 근거: 공식 — StrictMode는 "most commonly used to catch accidental disk or network access on the application's main thread", "don't feel compelled to fix everything that StrictMode finds", "StrictMode is not a security mechanism and is not guaranteed to find all disk or network accesses" [S335](https://developer.android.com/reference/android/os/StrictMode). LeakCanary 문서는 "debugImplementation because LeakCanary should only run in debug builds"이고 파괴된 Activity·Fragment·fragment View, cleared ViewModel, 파괴된 Service를 자동 감지한다 [S336](https://square.github.io/leakcanary/getting_started/). Android 문서도 비트맵 누수 점검에 LeakCanary와 Memory Profiler를 쓰라고 한다 [S334](https://developer.android.com/topic/performance/issues/bitmap-memory-usage). 위반 우선순위는 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 디버그 소스셋 Initializer 와 debugImplementation
  StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
  // build.gradle.kts: debugImplementation(libs.leakcanary.android)
  // Bad — main 소스셋·릴리스에 StrictMode 와 LeakCanary 를 둔다
  ```
- 체크: LeakCanary가 `debugImplementation`뿐인가. StrictMode 코드가 디버그 소스셋에만 있는가. `Application.onCreate`에 정책 설정을 넣었는가.

### R-41-16 메모리는 공식 임계 안에서 측정하고 앱 예산은 제안값으로 둔다
- 규칙: R-41-02와 같은 변이·기기에서 핵심 동작(탭 순회·화면 푸시·팝)을 마친 뒤 `adb shell dumpsys meminfo <패키지>`의 TOTAL PSS와 `/proc/<pid>/status`의 `RssAnon`+`VmSwap`을 함께 적는다. 공식 상한은 Android vitals의 bad behavior 임계다 — 메모리는 Anonymous RSS+Swap이고 RAM 구간·프로세스 상태별(예: 4GB 기기 포그라운드 2.00GB), 비트맵 메모리는 사용자 인지 서비스·백그라운드 200MB, 캐시 400MB다. 이것은 Play 노출 한계이지 앱 예산이 아니다. 앱 예산(제안): 핵심 동작 순회 뒤 TOTAL PSS 200MB 이하. 같은 순회를 반복해도 PSS가 줄지 않고 계속 늘면 누수로 보고 R-41-15로 확인한다. 현장에서는 vitals의 P90÷P50이 3.5배를 넘으면 누수를 의심한다. 캐시를 가진 앱은 `onTrimMemory`에서 `TRIM_MEMORY_UI_HIDDEN`·`TRIM_MEMORY_BACKGROUND`만 다룬다.
- 근거: 공식 — Anonymous RSS+Swap과 비트맵 메모리 임계 표와 "Apps exceeding the thresholds for memory usage, bitmap memory usage, or code optimization may see store visibility impact starting from **February 2027**" [S331](https://developer.android.com/google/play/vitals). "Tracking anonymous RSS + swap ensures you see your app's true, unevictable memory footprint"이고 "If your P90 to P50 ratio exceeds 3.5x, it indicates a likely memory leak during extended sessions" [S332](https://developer.android.com/google/play/vitals/memory-usage). 비트맵 메모리는 "doesn't always correlate with anonymous RSS + swap memory usage" [S333](https://developer.android.com/google/play/vitals/bitmap-memory-usage). `RssAnon`·`VmSwap`·`dumpsys meminfo` 측정과 "focus exclusively on the `TRIM_MEMORY_UI_HIDDEN` and `TRIM_MEMORY_BACKGROUND` events" [S329](https://developer.android.com/topic/performance/memory/manage-app-memory). 제안(사용자 위임, 2026-10-15) — PSS 200MB는 첫 실측(PSS 126MB, 그래픽 60MB, S22+, 2026-10-15)에 여유를 둔 값이고 기능이 늘면 다시 정한다. PSS와 vitals 지표(Anonymous RSS+Swap)의 환산은 공식 문서에서 확인하지 못했다.
- 예시:
  ```bash
  # Good — 순회 뒤 두 지표를 함께 적는다 (릴리스 상당 변이, 실기기)
  adb shell dumpsys meminfo com.example.app
  adb shell "cat /proc/\$(pidof com.example.app)/status | grep -E 'VmRSS|RssAnon|VmSwap'"
  # Bad — 디버그 빌드(LeakCanary 포함)의 메모리를 예산과 비교한다
  ```
- 체크: 측정이 릴리스 상당 변이인가. PSS와 `RssAnon`+`VmSwap`을 함께 적었는가. 같은 순회를 반복했을 때 증가 추세를 확인했는가.

## Baseline Profile 이행

### R-41-17 Baseline Profile은 `:baselineprofile` 테스트 모듈 하나로 만들고 R-10-01의 예외로 둔다
- 규칙: R-40-03이 정한 것(릴리스에 프로파일과 `profileinstaller`)을 이행하려면 모듈 유형 셋(R-10-01) 밖에 `com.android.test` 모듈 `:baselineprofile`을 최상위에 하나 둔다 — 프로파일 생성과 Macrobenchmark(R-41-02)가 같은 모듈이다. ① 빌드 파일은 `com.android.test`와 `androidx.baselineprofile`을 직접 적용하고(컨벤션 6종에 이 모듈용이 없고 사용처가 하나라 R-10-14) `targetProjectPath = ":app"`이며 `:app` 말고는 이 모듈에 의존하지 않는다. ② `:app`은 `androidx.baselineprofile` 플러그인, `baselineProfile(project(":baselineprofile"))`, `androidx.profileinstaller` 의존을 둔다. ③ 생성기(`BaselineProfileRule`)는 시작만이 아니라 R-41-02의 동작(화면 이동·탭 전환)을 돌고, 시작 경로만 `includeInStartupProfile = true`로 둔다. ④ 프로파일 저장(`saveInSrc`)과 생성 시점(`automaticGenerationDuringBuild`)은 결정 항목이다(new-app 결정 14). ⑤ 효과를 가정하지 않는다 — 프로파일은 릴리스에서만 설치되고 규칙이 넓으면 시작이 오히려 느려질 수 있으므로 R-41-18의 방법으로 전후를 잰다(측정 사례는 근거 끝). ⑥ 코드가 바뀌면 프로파일을 다시 생성한다 — 프로파일은 클래스·메서드 이름 기준이라 이름이 바뀐 코드는 담기지 않는다.
- 근거: 공식 — "Macrobenchmarks require a `com.android.test` module---separate from your app code" [S313](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview), "Create a new `com.android.test` module---for example, `:baseline-profile`", `androidx.baselineprofile` 플러그인·`targetProjectPath`·`baselineProfile(project(...))` 구성, "Baseline Profiles are only installed for release builds", 시작 관련 테스트는 "a `rule` block with `includeInStartupProfile` set to `true`"에 두라고 한다 [S337](https://developer.android.com/topic/performance/baselineprofiles/create-baselineprofile). 프로파일 생성 모듈을 "the profile producer module---likely the `:baselineprofile` test module"이라 부르고, "*Compiled* Baseline Profiles must be smaller than 1.5 MB", "Broad rules that compile too much of the application can slow down startup" [S269](https://developer.android.com/topic/performance/baselineprofiles/configure-baselineprofiles). 대상은 "app startup, navigating between screens, or scrolling through content" [S37](https://developer.android.com/topic/performance/baselineprofiles/overview). Compose는 "includes a default profile, but ideally, you should create an app-specific one as well" [S16](https://developer.android.com/develop/ui/compose/performance). `androidx.profileinstaller` 의존은 R-40-03의 근거(S37)를 따른다. 모듈 이름 `:baselineprofile`과 R-10-01 예외 한 건은 제안(사용자 위임, 2026-10-15)이다. 사례(AGP 9.4.0 + 플러그인 1.5.0, 이 저장소에서 확인): 플러그인이 `nonMinifiedRelease`를 구형 `isMinifyEnabled=false`로만 끄는데 AGP 9의 R8은 새 DSL `optimization.enable`을 따라, `release`가 그것을 켜 두면 이 변이도 R8으로 줄이고 난독화해 `mapping.txt`가 `benchmarkRelease`와 같아졌다(그 변이로 만든 프로파일은 이름이 난독화돼 릴리스에 맞지 않는다). `:app`에서 `androidComponents.finalizeDsl`로 이 변이만 `optimization.enable=false`로 껐고 그 뒤 `minify…WithR8` 대신 `dexBuilder…`가 돈다. 공식 문서에서는 확인하지 못한 동작이고 플러그인이 새 DSL을 따르면 필요 없다. 모듈은 `templates/baselineprofile/`이고 보정 블록은 `templates/app/app/build.gradle.kts`의 주석 블록이다. ⑥은 R8이 난독화된 릴리스에 맞게 "rewrites the rules from your unobfuscated profile files"한다는 공식 문장 [S37](https://developer.android.com/topic/performance/baselineprofiles/overview)에서 규칙이 코드 이름에 묶여 있다고 읽은 것이고 "이름 기준"이라는 문장 자체는 확인하지 못했다(미확인). 사례(측정, S22+ 한 대, 2026-10-15, 프로젝트 보고): 같은 `benchmarkRelease` 설치에서 `cmd package compile` 모드만 바꿔 `dumpsys gfxinfo`로 6회×2라운드를 쟀다. verify(프로파일 없음) → speed-profile(생성한 프로파일) → speed(전체 AOT) 순으로 설정 푸시·팝 버벅임 9~10%·p99 53ms → 7~7.5%·38~40ms → 6.7~8.4%·36~38ms, 대시보드 탭 왕복 4.5~5.2%·36~38ms → 4.4%·32ms → 4.4~5.7%·25~32ms, 진단 2.9%·36~38ms → 2.6~2.8%·28~29ms → 2.9~3.2%·22~24ms, 기록 2.5~3.1%·36~38ms → 2.7~3.3%·28~30ms → 3.0%·28ms였다. 읽으면 프로파일이 p99를 약 11~28% 줄였고(설정 푸시·팝이 가장 컸다 — 보고된 수치에서 계산) 버벅임 비율은 설정에서만 뚜렷했으며 speed-profile과 speed는 비슷했다. 한계: 한 기기·2라운드·오차 ±1.5%p·적응형 주사율(10~120Hz)이라 다른 기기·앱 상태로 일반화하지 않는다. 프로파일 설치는 `am broadcast -a androidx.profileinstaller.action.INSTALL_PROFILE <패키지>/androidx.profileinstaller.ProfileInstallReceiver`(result=1)로 했다. 컴파일된 `baseline.prof`는 텍스트(2.8MB·24,895줄)가 R8 매핑을 거쳐 385KB 텍스트 → 10,323바이트 바이너리(생성 전 8,484)가 되므로 크기가 줄어드는 것이 정상이다.
- 예시:
  ```kotlin
  // :baselineprofile/build.gradle.kts — 컨벤션 플러그인 없이 직접 (R-10-14)
  plugins { alias(libs.plugins.android.test); alias(libs.plugins.androidx.baselineprofile) }
  android { targetProjectPath = ":app" }
  // :app/build.gradle.kts
  plugins { alias(libs.plugins.androidx.baselineprofile) }
  dependencies { baselineProfile(project(":baselineprofile")) }
  // Bad — 프로파일 생성용 변이의 R8 설정을 buildTypes 에 손으로 적는다 (R-40-03)
  ```
- 체크: 최상위 모듈이 `:app`·`:core:*`·`:feature:*`와 `:baselineprofile` 하나뿐인가. 카탈로그에 플러그인·라이브러리 별칭이 있는가(R-10-12, 팩의 카탈로그 스니펫에 Baseline Profile 그룹이 있다). 앱 프로파일 파일이 저장소에 있고 릴리스 APK의 `assets/dexopt/baseline.prof`가 생성 전보다 커졌는가(파일은 생성 전에도 라이브러리 프로파일로 있다, R-40-03). `nonMinifiedRelease` 빌드가 R8(`minify…WithR8`)을 돌지 않는가(AGP 9 + 플러그인 1.5.0 사례는 근거 끝). 생성기가 시작 말고 화면 이동·탭 전환을 도는가. 전후 프레임 측정을 했는가. 프로파일을 만든 뒤 코드를 바꿨다면 다시 생성했는가. 텍스트 프로파일이 크고 바이너리 `baseline.prof`가 작다는 이유로 실패로 판단하지 않았는가(생성 전보다 커졌는지만 본다).


## 비교 방법과 컴포지션 낭비 (2026-10-15 실측 이후)

### R-41-18 프레임 비교는 변형을 번갈아 설치해 2라운드 이상 재고, 렌더한 프레임 수를 같이 적는다
- 규칙: 버벅임률 하나는 같은 변형을 반복해도 ±1.5%p 흔들린다. 둘 이상의 변형(빌드·컴파일 모드·구조 변경)을 비교할 때는 ① 변형을 번갈아 설치하며 2라운드 이상 잰다(한 변형을 몰아서 재거나 다른 시각에 잰 값과 비교하지 않는다) ② 변형마다 렌더한 프레임 수를 같이 적는다 — 적응형 주사율이면 변형마다 프레임 수가 달라져(사례: 17%) 비율 비교가 깨진다 ③ 버벅임률과 함께 P90·P99, 예산 초과 시간의 합, 예산(16.7ms)과 그 2배(33ms)를 넘은 프레임 수를 적는다. 프레임 수가 변형 사이에 크게 다르면 그 비교로 결론을 내지 않고 주사율을 고정해(방법은 확인하지 못했다, 미확인) 다시 잰다. 오차(±1.5%p) 안의 차이는 개선으로 쓰지 않는다 — 효과를 못 쟀으면 그렇게 쓴다(R-41-19). 가벼워 보이는 직접 구현이 항상 빠른 것은 아니므로 바꾸기 전후를 같은 방법으로 잰다.
- 근거: 공식 — "Run A/B comparisons across the same device and same OS version"이고 "The `RenderTime` metrics ... are more important than the count of janky frames for identifying regressions or improvements" [S312](https://developer.android.com/topic/performance/measuring-performance). 프레임 시간은 P50·P90·P95·P99 분포로 나온다 [S314](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics). 사례(측정, S22+ 한 대, 2026-10-15, 프로젝트 보고): 같은 변형을 반복한 버벅임률이 ±1.5%p 흔들렸다. 어떤 변형은 버벅임이 6~7%로 보였지만 렌더 프레임 수가 17% 적었고 원인은 확인하지 못했다(적응형 주사율 10~120Hz 의심, 미확인). 하단 바를 Material `NavigationBar` 대신 직접 그린 Row+selectable로 바꿔 항목당 애니메이션을 없앴더니 탭 왕복 버벅임이 약 3%에서 6~7%로 보이고 렌더 프레임 수가 17% 줄어 되돌렸다(원인 모름, 주사율 변화 의심, 미확인). 2라운드 이상과 기록할 지표 목록은 제안(사용자 위임, 2026-10-15)이다. 주사율을 고정하는 방법은 공식 문서에서 확인하지 못했다.
- 예시:
  ```text
  # Good — 변형 A·B 를 번갈아 설치해 2라운드, 프레임 수와 초과 지표를 같이 적는다
  라운드1 A: frames=N janky=x% p90=a p99=b  >16.7ms=n1  >33ms=n2  초과합=t
  라운드1 B: frames=N janky=x% p90=a p99=b  >16.7ms=n1  >33ms=n2  초과합=t
  라운드2 A, 라운드2 B …                      # frames 가 크게 다르면 결론을 미룬다
  # Bad — 한 변형을 몰아서 재거나 프레임 수 없이 "6%라 빨라졌다"고 쓴다
  ```
- 체크: 변형을 번갈아 설치했고 2라운드 이상인가. 렌더 프레임 수가 보고에 있고 변형 사이에 크게 다르지 않은가. 차이가 ±1.5%p 안인데 개선이라고 썼는가.

### R-41-19 컴포지션 본문의 반복 낭비(요소별 상태 벌·인셋 읽기·포맷터 생성)를 줄이되 효과를 주장하지 않는다
- 규칙: ① 눌림 같은 요소별 피드백은 `collectIsPressedAsState` 2개와 `animate*AsState` 2개처럼 컴포지션 상태를 여러 벌 만들지 않고, 진행도 하나가 색과 축소를 함께 구동하게 해 `graphicsLayer {}`·`drawBehind {}` 람다에서만 읽는다(R-28-16·R-28-18). ② `Scaffold`의 `innerPadding` 같은 `PaddingValues`는 상태가 든 객체다 — `calculateBottomPadding()` 같은 값 꺼내기를 컴포지션 본문에서 하지 않고 레이아웃·그리기 단계에서 읽는다(R-18-08). 객체째 `Modifier.padding(innerPadding)`·`contentPadding`으로 넘길 때 읽기가 어느 단계인지는 공식 문서에서 확인하지 못했다(미확인). ③ `NumberFormat`·`DateTimeFormatter`·`String.format`을 컴포지션 본문에서 호출마다 만들지 않고 로캘·설정이 같은 동안 `remember`로 캐시한다. ④ 항상 있지만 대부분 비어 있는 요소(스낵바 호스트의 `AnimatedContent` 등)는 첫 사용 전까지 구성하지 않는다. 이 항목들의 성능 효과는 측정하지 못했다 — 전후 차이가 측정 오차(±1.5%p) 안이었으므로 "개선했다"고 쓰지 않고 "낭비를 줄였다, 효과는 측정 못 했다"로 쓴다.
- 근거: 공식 — "When you are passing frequently changing State variables into modifiers, you should use the lambda versions of the modifiers whenever possible" [S17](https://developer.android.com/develop/ui/compose/performance/bestpractices)이고 "If your animation occurs in the draw phase, it is by default more performant" [S293](https://developer.android.com/develop/ui/compose/animation/quick-guide)이며 "you might instantiate `Brush`, `Path`, or data formatting objects during a composable's recomposition or inside a `DrawScope` call (such as `Canvas`). In both cases, the app creates a lot of objects quickly" [S329](https://developer.android.com/topic/performance/memory/manage-app-memory). 사례(트레이스 횟수, 프로젝트 보고, S22+, 2026-10-15 — 횟수와 시간은 측정했고 jank 개선은 측정하지 못했다): ① 눌림 상태 4벌이던 요소에서 느린 프레임 48개 동안 `animateValueAsState` 751회·`LaunchedEffect` 1,532회가 나왔고 진행도 하나를 그리기·레이어 람다에서 읽는 노드 1개로 바꿨다. ② 홈이 전환 25프레임 내내 재구성됐다 — 하단 바를 엔트리 안에 그리는 새 셸로 바뀐 뒤 사라졌지만 둘을 분리해 측정하지 않았다(미확인). ③ 포맷터 호출당 약 0.6ms(트레이스 값). ④와 캐시 기준은 제안(사용자 위임, 2026-10-15)이다.
- 예시:
  ```kotlin
  // Good — 진행도 하나가 색과 축소를 구동하고 그리기 람다에서만 읽힌다. 포맷터는 로캘이 같은 동안 재사용한다
  val pressed by interactionSource.collectIsPressedAsState()
  val progress = animateFloatAsState(if (pressed) 1f else 0f, Motion.fast(), label = "press")
  Box(Modifier.graphicsLayer { scaleX = 1f - 0.02f * progress.value; scaleY = scaleX }
      .drawBehind { drawRect(lerp(rest, pressedColor, progress.value)) })
  val format = remember(locale) { NumberFormat.getInstance(locale) }
  // Bad — 색용·축소용 상태를 따로 만들고 포맷터를 컴포지션마다 만든다
  val color by animateColorAsState(if (pressed) pressedColor else rest)
  Text(NumberFormat.getInstance().format(value))
  ```
- 체크: 한 요소에 `collectIsPressedAsState`·`animate*AsState`가 둘 이상인가. 컴포지션 본문에서 `PaddingValues` 값을 꺼내는가. 포맷터가 호출마다 만들어지는가. 효과를 재지 않았는데 개선이라고 썼는가.
