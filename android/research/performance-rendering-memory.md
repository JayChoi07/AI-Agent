# 렌더링·안정성·메모리·성능 측정 조사 노트

조사일: 2026-10-15
담당 references 파일 번호: 41(렌더링·안정성·메모리와 성능 측정), 10(R-10-01 예외)·40(상호 참조)

목적: 화면 이동·홈 진입에서 버벅임이 있는데 팩에 렌더링·메모리·성능 지침이 부족하다는 지적(2026-10-15, 사용자가 팩에 규칙 추가를 승인하고 공식 문서가 값을 정하지 않은 것은 "제안(사용자 위임)"으로 두라고 위임)에 따라 공식 문서를 원문으로 확인했다. 원문은 `android docs fetch kb://android/<경로>`(= developer.android.com/<경로>)와 `curl`로 받은 API 레퍼런스·Kotlin·LeakCanary 문서다. 아래 지역 번호(S1…)는 이 노트에서만 쓰고, 규칙은 `92-sources-2.md`의 전역 번호를 인용한다.

## 계기가 된 실측 (InfocarEV_Android, S22+, 2026-10-15, 프로젝트 쪽 측정)

- 디버그: 느린 프레임 22%, p99 129ms. 릴리스(R8): 5.5%, p99 40ms. 대부분 화면이 처음 들어올 때의 레이아웃·그리기 기록 단계가 병목(느린 프레임에서 22~23ms)이고 GPU는 최대 7ms.
- 완전 AOT(`speed`)로 컴파일해도 느린 프레임 비율은 그대로이고 p99만 44→31ms였다. 프로젝트에 Baseline Profile이 없었다.
- 앱 PSS 126MB(그래픽 60MB), 콜드 스타트 196ms.
- clip + drawBehind + alpha 조합이 기기에서 배경이 사라지는 결함으로 이어졌다(원인 미확인).

이 수치는 프로젝트 쪽이 낸 것이고 이 노트에서 다시 재지 않았다. 팩 규칙은 이 수치에 맞춰 값을 고른 것이 아니라 이 문제를 가려내기 위한 출발값이다.

## 출처

| # | 전역 | 조직 | 문서명 | URL | 문서 종류 |
|---|---|---|---|---|---|
| S1 | S310 | Google | Slow rendering (Android vitals) | https://developer.android.com/google/play/vitals/render | 공식 가이드 |
| S2 | S311 | Google | Slow rendering (진단·수정) | https://developer.android.com/topic/performance/issues/render | 공식 가이드 |
| S3 | S312 | Google | Overview of measuring app performance | https://developer.android.com/topic/performance/measuring-performance | 공식 가이드 |
| S4 | S313 | Google | Write a Macrobenchmark | https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview | 공식 가이드 |
| S5 | S314 | Google | Capture Macrobenchmark metrics | https://developer.android.com/topic/performance/benchmarking/macrobenchmark-metrics | 공식 가이드 |
| S6 | S315 | Google | JankStats library | https://developer.android.com/topic/performance/jankstats | 공식 가이드 |
| S7 | S316 | Google | dumpsys | https://developer.android.com/tools/dumpsys | 공식 가이드 |
| S8~S12 | S317~S321 | Google | Compose 안정성 5건(개요·진단·수정·강한 건너뛰기·도구) | developer.android.com/develop/ui/compose/performance/stability… · …/tooling | 공식 가이드 |
| S13 | S322 | Google | Composition tracing | https://developer.android.com/develop/ui/compose/tooling/tracing | 공식 가이드 |
| S14 | S323 | Google | Jetpack Compose phases | https://developer.android.com/develop/ui/compose/phases | 공식 가이드 |
| S15 | S324 | Google | Drawing and graphics modifiers in Compose | https://developer.android.com/develop/ui/compose/graphics/draw/modifiers | 공식 가이드 |
| S16 | S325 | Google | Modifier.alpha | https://developer.android.com/reference/kotlin/androidx/compose/ui/draw/alpha.modifier | 공식 API 레퍼런스 |
| S17 | S326 | Google | Modifier.shadow | https://developer.android.com/reference/kotlin/androidx/compose/ui/draw/shadow.modifier | 공식 API 레퍼런스 |
| S18 | S327 | Google | Reduce overdraw | https://developer.android.com/topic/performance/rendering/overdraw | 공식 가이드 |
| S19 | S328 | Google | Compose layout basics | https://developer.android.com/develop/ui/compose/layouts/basics | 공식 가이드 |
| S20 | S329 | Google | Manage your app's memory | https://developer.android.com/topic/performance/memory/manage-app-memory | 공식 가이드 |
| S21 | S330 | Google | Optimizing bitmap images (Compose) | https://developer.android.com/develop/ui/compose/graphics/images/optimization | 공식 가이드 |
| S22~S25 | S331~S334 | Google | Android vitals · 메모리 사용량 · 비트맵 메모리(vitals) · 비트맵 메모리(진단) | developer.android.com/google/play/vitals… · …/topic/performance/issues/bitmap-memory-usage | 공식 가이드 |
| S26 | S335 | Google | StrictMode | https://developer.android.com/reference/android/os/StrictMode | 공식 API 레퍼런스 |
| S27 | S336 | Square | LeakCanary — Getting started | https://square.github.io/leakcanary/getting_started/ | 오픈소스 문서 |
| S28 | S337 | Google | Create Baseline Profiles | https://developer.android.com/topic/performance/baselineprofiles/create-baselineprofile | 공식 가이드 |
| S29~S30 | S338~S339 | JetBrains | Compose compiler options · Compose compiler Gradle plugin API | kotlinlang.org/docs/compose-compiler-options.html · kotlinlang.org/api/kotlin-gradle-plugin/… | 공식 문서 |
| S31~S33 | S340~S342 | JetBrains | StateFlow · Flow.sample · Flow.distinctUntilChanged | kotlinlang.org/api/kotlinx.coroutines/… | 공식 API 문서 |
| S34 | S343 | Google | androidx.compose.ui.layout (SubcomposeLayout) | https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/package-summary | 공식 API 레퍼런스 |

기존 번호도 인용했다: S16(Compose 성능)·S17(best practices)·S22(Nav3 save-state)·S23(NavEntryDecorators)·S37(Baseline Profile 개요)·S269(Baseline Profile 구성)·S281(Nav3 멀티 백스택)·S306(Lazy lists).

## 확인한 내용 (원문 대조)

- **프레임 정의(S1·S2)**: 슬로 프레임은 16ms 초과, 프로즌은 700ms 초과이고 "No frames in your app should ever take longer than 700ms to render". 시작·화면 전환의 첫 프레임이 16ms를 넘는 것은 "normal"로 적는다. 90fps는 11ms, 120fps는 8ms. Play의 bad behavior 임계 표(S22)에는 충돌·ANR·배터리·메모리·DEX만 있고 슬로 렌더링은 없다.
- **측정(S3~S5)**: 핵심 동작은 시작·스크롤·화면 전환이고 매끄러워야 할 전환으로 "switching between tabs or loading a new activity"를 든다. 첫 진입만 재려면 `startupMode = null`과 setup의 `killProcess()`를 쓰라는 대안을 적는다. 디버그 빌드에서 재지 않는다. `FrameTimingMetric`은 `frameOverrunMs`(Android 12+, 양수는 놓친 프레임)와 `frameDurationCpuMs`를 P50·P90·P95·P99로 낸다. "The `RenderTime` metrics ... are more important than the count of janky frames". Macrobenchmark는 `com.android.test` 모듈이 필요하고 앱은 profileable·non-debuggable·가능하면 minify, 에뮬레이터는 비권장이며 오류를 낸다. 기본 `CompilationMode.DEFAULT`는 Baseline Profile을 설치한다. `StartupMode.COLD`는 setup과 measure 사이에 프로세스를 죽인다. 시작 목표는 콜드 500ms·웜 200ms·핫 150ms이고 P95·P99가 중앙값에 가까워야 한다.
- **jank 휴리스틱(S6)**: JankStats는 기본으로 "a frame taking twice as long to render as the current refresh rate"를 jank로 본다. 팩 R-41-01의 "예산의 2배"는 여기서 가져왔다.
- **안정성(S8~S12, S29~S30, 라이브 S317 기준)**: 강한 건너뛰기는 Kotlin 2.0.20부터 기본이고 불안정 파라미터는 `===`, 안정 파라미터는 `equals()`로 비교한다. `List`는 항상 불안정이지만 "unstable collections still skip recomposition using fast O(1) instance equality (===) when the collection reference does not change"이고 "Avoid converting large collections to Kotlinx immutable collections or annotating collection-holding classes with @Immutable or @Stable unless structural equality (O(N) .equals()) is specifically required"다(라이브 S317, 2026-10-08 갱신). Compose 컴파일러가 안 도는 모듈의 모델도 불안정이고 `===`로 비교되며 "small, flat models ... with identical values"를 자주 다시 만들 때만 설정 파일·`@Stable`을 권한다. 디버그 첫 확인은 "whether new instances of unstable types are being allocated on every recomposition"이다. 수정 가이드(S319)는 여전히 설정 파일(1.5.5부터)·불변 컬렉션(문서는 alpha라 적음)·래퍼·`@Stable`/`@Immutable`을 모두 안내한다. 리포트는 릴리스에서 만들고 모든 컴포저블을 skippable로 만들려는 것은 premature optimization이다. Kotlin 문서는 강한 건너뛰기를 기능 플래그(`ComposeFeatureFlag.StrongSkipping.disabled()`)로 끄고, Gradle 플러그인 API에 `enableStrongSkippingMode`·`stabilityConfigurationFile`·`stabilityConfigurationFiles`가 모두 있다.
- **그리기·레이어(S15~S18, 라이브 S324 기준)**: `Modifier.alpha`는 별도 그래픽 레이어를 만들고 alpha<1이면 내용이 경계로 잘리며 애니메이션에는 `graphicsLayer`를 권한다. `graphicsLayer`는 alpha<1에서 오프스크린 버퍼를 만들고 `ModulateAlpha`는 피한다(겹침이 있으면 결과가 다르다). 라이브 S324(2026-10-12 갱신)에는 "Layer outsets" 절이 있다 — 오프스크린 버퍼는 기본으로 레이아웃 경계까지만이고 경계 밖의 그리기·그림자는 잘리며 `LayerOutsets`(Compose 1.12.0-alpha01+)로 넓힌다. `Modifier.shadow`는 별도 레이어다(오프스크린 버퍼라는 말은 없다). `drawWithCache`는 `Brush`·`Shader`·`Path` 재할당을 피한다. 오버드로는 불필요한 배경 제거와 투명 줄이기로 줄이고 저사양 GPU가 아니면 UI 스레드 작업이 먼저다.
- **구조 비용(S14·S19·S18·S34)**: Compose는 자식을 한 번만 측정하므로 깊이는 성능에 영향이 없다고 두 문서가 적는다. 자식 컴포지션이 부모 레이아웃 단계에 의존하는 예외는 `BoxWithConstraints`·`LazyColumn`·`LazyRow`다(phases). `SubcomposeLayout`은 "subcompose the actual content during the measuring stage"하는 레이아웃이다(API 레퍼런스). 그 비용 수치는 없다.
- **Lazy(S306)**: `Column`은 보이지 않아도 전부 컴포지션하고 lazy는 보이는 것만 한다. 0px 항목은 첫 측정에서 전부 컴포지션하게 만든다. `contentType`은 같은 종류끼리만 컴포지션을 재사용하게 한다. 한 item에 여러 요소를 두면 하나만 보여도 전부 컴포지션한다.
- **메모리(S20~S25)**: 비트맵이 가장 큰 객체이고 `wrapContentSize`면 로더가 원본을 디코딩한다. `Painter`는 안정 표시가 없다. vitals 임계는 Anonymous RSS+Swap(RAM 구간·상태별)과 비트맵 메모리(200·200·400MB)이고 2027년 2월부터 노출에 영향을 줄 수 있다고 적는다. P90÷P50 3.5배는 누수 신호다. `RssAnon`·`VmSwap`은 `/proc/<pid>/status`에서 읽는다. `onTrimMemory`는 `TRIM_MEMORY_UI_HIDDEN`·`TRIM_MEMORY_BACKGROUND`만 다룬다. StrictMode는 최선 노력 도구이고 LeakCanary는 `debugImplementation`이다.
- **Nav3 상주(S22·S23·S281)**: 엔트리 스코프 ViewModel은 엔트리가 백스택에서 빠질 때 지워진다. 멀티 백스택 레시피는 시작 탭의 엔트리를 항상 남기고, 시작 탭이 아닌 탭으로 가면 다른 탭의 엔트리를 제거한다(A→B→C는 A+C).
- **Baseline Profile(S28, S37, S269)**: 프로파일 대상은 "app startup, navigating between screens, or scrolling". 생성은 `com.android.test` 모듈(예: `:baseline-profile`)이고 구성 문서는 "likely the `:baselineprofile` test module"이라 적는다. 릴리스에서만 설치되고 컴파일된 프로파일은 1.5MB 미만, 규칙이 넓으면 시작이 느려질 수 있다. Compose는 기본 프로파일을 갖지만 앱 전용도 만들라고 한다.
- **Flow(S31~S33)**: `StateFlow`는 `equals` 기반으로 같은 값을 건너뛰고 `distinctUntilChanged`를 또 붙여도 효과가 없다. `sample`은 `@FlowPreview`다.

## 프로젝트 측정·관찰 (InfocarEV_Android, S22+, 2026-10-15 — 프로젝트 보고, 팩에서 다시 재지 않았다)

**측정으로 확인한 것**과 **코드 기준(효과 미측정)**을 나눠 적는다. 효과를 측정하지 못한 것은 규칙의 근거로 쓰지 않았다.

### 측정으로 확인한 것

- **Baseline Profile 효과(R-41-17 근거 끝).** 같은 `benchmarkRelease` 설치에서 `cmd package compile` 모드만 바꿔 `dumpsys gfxinfo`로 6회×2라운드 쟀다. verify(프로파일 없음) → speed-profile(생성한 프로파일) → speed(전체 AOT):
  설정 푸시·팝 버벅임 9~10%·p99 53ms → 7~7.5%·38~40ms → 6.7~8.4%·36~38ms / 대시보드 탭 왕복 4.5~5.2%·36~38ms → 4.4%·32ms → 4.4~5.7%·25~32ms /
  진단 2.9%·36~38ms → 2.6~2.8%·28~29ms → 2.9~3.2%·22~24ms / 기록 2.5~3.1%·36~38ms → 2.7~3.3%·28~30ms → 3.0%·28ms.
  읽는 법: 프로파일이 p99를 줄이고(보고된 수치로 계산하면 약 11~28%, 설정 푸시·팝이 가장 크다 — 보고에는 "25~30%"로 적혀 있었으나 대시보드는 11~16%라 범위를 고쳤다) 버벅임 비율은 설정에서만 뚜렷하다. speed-profile과 speed는 비슷하다.
  한계: 한 기기·2라운드·오차 ±1.5%p·적응형 주사율(10~120Hz). 다른 기기·앱 상태로 일반화하지 않는다.
- **프로파일 설치·크기.** 설치는 `am broadcast -a androidx.profileinstaller.action.INSTALL_PROFILE <pkg>/androidx.profileinstaller.ProfileInstallReceiver`(result=1)였다.
  컴파일된 `baseline.prof`는 텍스트(2.8MB·24,895줄)가 R8 매핑을 거쳐 385KB 텍스트 → 10,323바이트 바이너리(생성 전 8,484)가 된다. 크기가 줄어드는 것이 정상이므로 "텍스트가 크다/바이너리가 작다"로 실패를 판단하지 않는다.
- **측정 방법(R-41-18).** 버벅임률 하나는 같은 변형을 반복해도 ±1.5%p 흔들린다. 변형을 번갈아 설치해 2라운드 이상 재고 렌더한 프레임 수를 같이 적는다 — 적응형 주사율이면 프레임 수가 변형마다 17%까지 달라져 비율 비교가 깨졌다(어떤 변형이 6~7%로 보였지만 프레임 수가 17% 적었다, 원인 미확인). 예산 초과 시간의 합과 >16.7ms·>33ms 개수를 함께 본다. 주사율 고정은 방법을 확인하지 못했다(미확인).
- **트레이스 방법(R-41-04).** `runtime-tracing` + `tracing-perfetto-binary` + profileable 매니페스트 + `ENABLE_TRACING` 브로드캐스트 + perfetto 설정의 `track_event`로 릴리스에서도 컴포저블 이름이 나온다(공식 터미널 절차와 같다). R8 릴리스를 읽으려면 소비자 규칙 `-keepnames class ** { *; }`이 필요했고 `-dontobfuscate`는 거부됐다. simpleperf는 매핑 파일로 역난독화했다. 임시 의존성이므로 끝나면 되돌린다(공식: `tracing-perfetto-binary`는 출시용 앱에 넣지 않는다).
- **메인 스레드 증거(R-41-05 근거).** 느린 프레임은 화면 진입 직후 무거운 프레임 2~3개(푸시 20~25ms, 홈 팝 46~65ms)와 전환 끝의 14~19ms 프레임이다. 무거운 프레임의 55~65%가 `measureAndLayout`(지연 항목 컴포지션 약 35%, 측정·배치 약 45%, 텍스트 약 15%)이다. 자기 시간은 한 곳에 몰리지 않고 Compose 기계 전체에 퍼졌다(레이아웃 노드 10.8%, 컴포지션 8%, 스냅샷 5.9%, 코루틴 4.9%, 애니메이션 3.5%, 텍스트 약 5%). 느린 프레임의 74%는 중간 코어에서 평균 약 1.16GHz로 돌았다.
  프로젝트의 결론은 "코드로 쉽게 줄지 않는 노드 수 비례 비용 — 가장 큰 지렛대는 Baseline Profile, 다음이 노드 수 자체를 줄이는 구조 변경"이고 "화면을 여러 프레임에 나눠 구성하면 총 정지 시간은 같고 버벅임 프레임 수만 는다"이다. 뒤의 두 가지(노드 수를 줄인 변형, 나눠 구성한 변형)는 효과를 재지 않은 읽기이므로 규칙의 근거로 쓰지 않았다(미확인).

### 코드 기준 (성능 효과는 측정하지 못했다)

- **눌림 피드백 상태 정리(R-41-19 ①).** 요소마다 `collectIsPressedAsState` 2개와 `animate*AsState` 2개(컴포지션 상태 4벌)였을 때 느린 프레임 48개에서 `animateValueAsState` 751회·`LaunchedEffect` 1,532회가 나왔다. 진행도 하나를 그리기·레이어 람다에서 읽는 노드 1개(상태 4벌 → 1개)로 바꿨다. 호출 횟수는 측정했으나 전후 성능 차이는 오차(±1.5%p) 안이라 개선을 주장하지 않는다 — "낭비를 줄였다, 효과는 측정 못 했다".
- **`PaddingValues` 읽기(R-41-19 ②).** `Scaffold`의 `PaddingValues`는 상태가 든 단일 객체라 컴포지션에서 읽으면 인셋이 애니메이션할 때 화면이 프레임마다 재구성된다(홈이 전환 25프레임 내내 재구성됐다). 레이아웃·그리기 단계에서만 읽는다. 새 셸 구조(하단 바를 엔트리 안에 그림)로 바뀐 뒤 이 현상은 사라졌지만 둘을 분리해 측정하지는 않았다. 객체째 `Modifier.padding(innerPadding)`로 넘길 때 읽기가 어느 단계인지는 확인하지 못했다.
- **포맷터·빈 호스트(R-41-19 ③④).** `NumberFormat`·`DateTimeFormatter`·`String.format`은 호출당 약 0.6ms(트레이스 값)라 로캘·설정이 같은 동안 캐시한다. 항상 있지만 대부분 비어 있는 요소(스낵바 호스트의 `AnimatedContent`)는 첫 사용 전까지 구성하지 않는다. 효과는 측정하지 못했다.

### 실패한 시도와 비용

- **하단 바를 직접 그린 Row+selectable로 교체(R-41-18 사례).** Material `NavigationBar` 대신 직접 그려 항목당 애니메이션을 없앴더니 탭 왕복 버벅임이 약 3% → 6~7%로 보이고 렌더 프레임 수가 17% 줄었다. 되돌렸다. 원인은 모른다(주사율 변화 의심, 미확인). 교훈은 "가벼워 보이는 직접 구현이 항상 빠른 것은 아니다, 바꾸기 전후를 교차 설치로 잰다"이다.
- **새 셸(하단 바를 탭 루트 엔트리 안에 그림)의 비용(R-28-01 근거에 한 줄).** 설정 푸시·팝 버벅임이 구 셸 약 5.3% → 약 9%(홈 팝 프레임 41~46ms → 54~65ms). 하단 바 컴포지션 약 2.8ms·엔트리 레이어 추가·`applyChanges` 증가를 의심하나 셸만 되돌린 변형으로 분리하지 못했고 구 셸은 다른 시각에 쟀다(미확인). 모양을 우선한 사용자 결정이다.

## 충돌·주의

- Android 안정성 문서의 `enableStrongSkippingMode = true`(2.0.20 이전)와 Kotlin 문서의 기능 플래그 방식이 다르다. Kotlin 2.4.20 기준으로는 기본이 켜져 있고 끄는 쪽만 기능 플래그다. 팩은 "꺼져 있지 않은지" 확인만 요구한다.
- kb 스냅샷(`android docs fetch kb://…`)이 라이브 페이지보다 오래된 문서가 있었다 — 안정성 개요(S317, 라이브 2026-10-08 갱신)와 그래픽 modifier(S324, 라이브 2026-10-12 갱신). 처음 규칙은 스냅샷의 "불안정 파라미터는 부모가 재구성될 때 항상 재구성한다"·"컬렉션은 항상 불안정"을 따랐다가 독립 리뷰가 지적해 라이브 문구로 고쳤다. 인용은 라이브 페이지로 다시 대조했다. 이후 새 규칙은 kb만으로 근거를 닫지 말고 공개 URL의 현재 판본도 본다.
- 수정 가이드(S319)는 불변 컬렉션·설정 파일을 첫 해법으로 안내하고 라이브 개요(S317)는 큰 컬렉션의 구조 비교를 조심하라고 한다. 팩은 둘을 함께 따라 "같은 인스턴스 재사용이 먼저, 구조 비교는 필요할 때만"으로 정리했다(R-41-07).
- 슬로 프레임 16ms는 60Hz 기준이다. 기기가 90·120Hz로 돌면 예산이 줄어든다(S2). R-41-01은 주사율의 프레임 간격을 예산으로 쓴다.
- Macrobenchmark 샘플의 반복 횟수는 5회지만 권장 횟수는 문서에 없다. R-41-02의 10회는 제안이다.
- "계층·노드 수를 줄이라"는 흔한 지침은 공식 문서와 어긋나서 규칙으로 만들지 않았다(깊이는 성능에 영향이 없다고 두 문서가 적는다).

## 확인하지 못한 것

- 첫 진입 프레임에서 텍스트·`Canvas`·벡터의 첫 그리기 비용, 노드 수가 컴포지션에 주는 비용, `LazyListState` 초기 위치의 비용 — 공식 수치 없음.
- `BoxWithConstraints`·`SubcomposeLayout`·`clip`·그림자의 비용 수치 — 공식 문서는 구조와 "별도 레이어"만 적는다(`Modifier.clip` 문서에는 비용 언급이 없다).
- 알파가 1과 1 미만을 오갈 때 레이어 생성·소멸 비용, clip + drawBehind + alpha 조합에서 배경이 사라진 원인(가설: alpha<1이면 내용이 경계로 잘린다는 공식 문장).
- `dumpsys gfxinfo`의 출력 형식·통계 초기화 인자(`reset`), gfxinfo percentile과 `frameOverrunMs`의 환산 — dumpsys 문서는 명령과 `framestats`만 적고, `training/testing/performance` URL은 지금 테스트 개요 페이지로 열려 gfxinfo 설명이 없었다.
- PSS와 vitals 지표(Anonymous RSS+Swap)의 환산, 앱 메모리 예산의 공식 값(200MB는 제안).
- `kotlinx.collections.immutable`의 현재 안정성(문서는 alpha) 와 Baseline Profile이 화면 이동 프레임에 주는 효과의 크기(공식은 코드 실행 속도 약 30%만 적는다).
- 적응형 주사율에서 변형 사이 렌더 프레임 수가 17% 달랐던 원인과 주사율을 고정하는 방법, 직접 그린 하단 바가 6~7%로 보인 원인, 새 셸의 비용을 셸만 되돌려 분리한 값, 눌림 상태 정리·`PaddingValues` 읽기·포맷터 캐시의 성능 효과, 노드 수를 줄이거나 화면을 나눠 구성한 변형의 효과.
- `LayerOutsets`(Compose 1.12.0-alpha01+)가 이 팩의 Compose BOM(2026.08.00)에 들어 있는지, Baseline Profile 플러그인의 `benchmarkRelease` 변이를 돌리는 Gradle 태스크 이름.
- 이 노트와 규칙은 실기기에서 측정해 보지 않았다. `templates/baselineprofile/`(2026-10-15)은 InfocarEV_Android의 `:baselineprofile`을 일반화한 것으로, 그 저장소의 복제본에 치환해 `compileNonMinifiedReleaseKotlin`·`ktlintCheck`·`detektNonMinifiedRelease`까지 통과시켰고 기기 실행(프로파일 생성·벤치마크)은 하지 않았다. `FrameTimingBenchmark`는 프로젝트 원본이 `StartupMode.COLD` + setup 에서 앱 기동인데, 공식 문서는 COLD가 setup과 measure 사이에 프로세스를 죽인다고 적어 측정 블록이 죽은 앱을 조작하게 된다(기기에서 확인하지 못했다, 미확인). 템플릿은 공식 대안(`startupMode = null` + setup 의 `killProcess()`)으로 바꿨다 — R-41-02와 같다.
- Nav3가 전환 중 이전 화면을 계속 컴포지션하는 범위, 팩 템플릿 `Navigator`는 백스택이 하나라 다중 백스택의 탭 상주를 검증하지 못했다.

## 결정 (제안, 사용자 위임 2026-10-15)

91-decisions.md의 PERF_FRAME·PERF_MEASURE·PERF_COMPOSE·PERF_RENDER·PERF_MEMORY·PERF_BASELINE 여섯 행에 있다. 제안 값은 프레임 통과선(프로즌 0·`frameOverrunMs` P95 ≤ 0·P99 ≤ 프레임 간격), 반복 10회, 앱 PSS 200MB이고 전부 첫 실기기 측정 뒤 다시 본다.

## 검증 (2026-10-15)

`bash android/scripts/check-pack.sh` 통과(줄 수·5요소·근거 URL·R-ID 유일·인용 R-ID 실존). 규칙의 근거 줄에 큰따옴표로 옮긴 영어 인용 문장 전부(성능 작업 반영 뒤 109건)를 받은 원문(kb 문서와 공개 URL의 라이브 HTML)에서 공백·마크다운 기호를 정규화해 찾는 스크립트로 대조했고 불일치는 고쳤다. URL은 전부 HTTP 200으로 열린다. 독립 서브에이전트(읽기 전용)가 같은 인용을 라이브 페이지에서 다시 대조해 87건이 일치하고 한 건(S317)이 달랐다고 보고했다.

## 리뷰 반영 (2026-10-15)

독립 서브에이전트 리뷰(읽기 전용, 라이브 페이지 대조)가 15건을 지적했고 다음과 같이 반영했다.
- 블로킹 1건: R-41-08의 S317 인용이 kb 스냅샷 문구여서 라이브 문구로 교체했다. 같은 문서의 새 주의(큰 컬렉션의 구조 비교 비용, 작고 납작한 모델에만 설정 파일)를 R-41-07·R-41-08·R-41-09와 R-17-15에 반영했고 R-41-07의 첫 대응을 "같은 인스턴스 재사용"으로 바꿨다.
- 규칙 간 모순: R-41-11 ③이 R-28의 전환·`Crossfade`와 충돌해 "오래 유지되는 상태 표현"으로 범위를 좁히고 R-28 전환을 예외로 적었다. R-41-07의 Good 예시가 틱을 UiState에 넣어 R-41-10과 어긋났고, R-41-06의 Good 예시가 창 크기 분기라 R-18-13과 어긋나 둘 다 고쳤다.
- 사실 정정: `Modifier.shadow`는 오프스크린 버퍼가 아니라 별도 레이어(공식 문구 그대로)로 고쳤다. R-41-02의 첫 진입 측정이 런치 프레임을 섞는 문제를 `startupMode = null`과 `killProcess()`로 고쳤다. Nav3 멀티 백스택 레시피는 시작 탭과 현재 탭의 엔트리를 남긴다는 설명으로 고쳤다(91-decisions·이 노트). 리뷰가 "S312에 탭 언급이 없다"고 한 것은 틀렸다 — S312의 "Transitions that aren't smooth"에 "switching between tabs"가 있어 그 문장을 근거에 옮겼다.
- 근거 보강: `SubcomposeLayout` API 문구(S343), `includeInStartupProfile`·`profileinstaller` 근거, `enableStrongSkippingMode` 속성, 라이브 S324의 경계 밖 잘림 문장(R-41-11 ④가 공식 근거를 얻었다), 제안 표시가 빠진 문장(R-41-04·R-41-13)을 채웠다.
- 문구·표 정리: R-41-03의 R-ID 용법과 보고 칸, 예시의 프로젝트 패키지, 컨벤션 플러그인 두 개, `:app` 의존 행, 17번의 소유권 문장.
- 반영하지 않은 것: 카탈로그 스니펫(`enforcement/build-logic/libs.versions.toml.snippet`)에 `android.test`·`androidx.baselineprofile`·`leakcanary` 별칭이 없다는 지적은 템플릿·강제 장치를 이번 범위 밖으로 두었으므로 R-41-17 체크에 "카탈로그에 별칭이 있는가"를 넣는 것으로 대신했다.

## 성능 작업 결과 반영 (2026-10-15)

프로젝트의 성능 작업 결과를 팩에 옮겼다. R-41-17에 Baseline Profile 측정 사례와 "코드가 바뀌면 프로파일을 다시 생성한다"를 더했고, R-41-04에 릴리스 트레이스 방법을, R-41-05 근거에 메인 스레드 증거를 더했다.
새 규칙은 R-41-18(변형을 번갈아 설치해 비교하고 렌더 프레임 수를 같이 적는다)과 R-41-19(컴포지션 본문의 반복 낭비 — 효과를 주장하지 않는다)이다. R-28-01 근거에 새 셸 비용을 한 줄 적었고 91-decisions의 보류 표에 이번에 확인하지 못한 것을 더했다.
**효과를 측정한 것(A·B·C·D)과 코드 기준(E·F·G·H·I)을 나눠 썼고, 측정하지 못한 효과는 규칙의 근거로 쓰지 않았다.** 프로젝트 보고에 있던 "프로파일이 p99를 25~30% 줄인다"는 보고된 표로 다시 계산해 11~28%로 적었다. 새 인용은 `curl`로 받은 공개 페이지에서 109건 모두 일치했다.


## 2026-10-20 프로젝트 관찰 기록 (S22+, 공식 문서에 없음)

- **자동 입력 vs 손가락 입력(R-41-02)**: `adb shell input tap` 첫 프레임은 CPU4 633MHz·CPU7 806MHz(유휴 주파수)에서 돌았고 손가락 탭 트레이스는 CPU7 1.7~2.1GHz 였다. 부스트 메커니즘의 유무는 확정하지 않았다. 다른 기기·OEM 미확인.
- **가설 먼저 고친 변경의 악화(R-41-04)**: 화면 내용을 프레임마다 나눠 드러내는 변경이 설정 푸시에 효과가 없었고(7.9→7.6~8.1%) 기록 세그먼트를 악화시켰다(6→13~16%). 트레이스로 원인(거대 레이어)을 가른 뒤에야 고쳤다.
- **긴 Column + Crossfade 레이어(R-28-17)**: `interaction-motion.md` 의 출처 조사 기록 참고.