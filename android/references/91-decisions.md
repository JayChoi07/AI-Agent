# 채택 결정과 보류

> 출처 번호(S..)는 [`90-sources.md`](90-sources.md). 이 파일은 규칙 문서가 아니라 결정 기록이라 `check-pack.sh`의 규칙 5요소 검사에서 제외한다.

## 결정 표

`키`는 규칙 문서들이 참조하는 확정값 이름이다. 75건에 빈 칸은 없다. 다만 REDUCE_MOTION 한 행은 사용자가 고른 원안과 달라 사용자 재확인이 필요하고, PERF_* 여섯 행은 공식 문서가 값이나 방법을 정하지 않아 둔 **제안**(사용자 위임, 2026-10-15)이라 사용자 확인이 필요하다(나머지 68건은 확정). 2026-10-15 웨이브 5 리뷰에서 더한 7행(SINGLE_TASK~EAGERLY_REASON)은 InfocarEV 프로젝트 사례에서 나온 결정이다. 아래 표의 `근거 출처`·`결정자` 칸이 선택지별 판단 근거를 그대로 담고 있으며, 짧은 요약은 `android/README.md`의 확정 결정 요약에 있다.

| 키 | 주제 | 선택지 | 결정 | 근거 출처 | 결정자 |
|---|---|---|---|---|---|
| MVI_IMPL | MVI 구현체 | 순수(ViewModel+StateFlow) / Orbit / Circuit | **순수 구현** — ViewModel + StateFlow + 단일 UiState, 라이브러리 의존 0. MVI가 필요한 화면만 같은 골격 안에 sealed Intent + `reduce` 함수 + Channel SideEffect를 더한다 | S02, S09, S91, S93, S98 | 사용자 |
| SCREENSHOT_LIB | 스크린샷 테스트 도구 | Roborazzi / Paparazzi / com.android.compose.screenshot | **Roborazzi 1.74.0**. 골든 경로는 `<모듈>/src/test/screenshots/`, 렌더링은 `@Config(sdk = [35])`(실빌드 실증 2026-09-09) | S87, S88, S34, S56, S46 | 사용자 |
| ERROR_TYPE | 에러 타입 | kotlin.Result / 커스텀 sealed / Arrow Either | **도메인별 커스텀 `sealed` 계층 + 단순 부재는 nullable**. `kotlin.Result`·Arrow 미채택 | S65, S63, S105 | 사용자 |
| USECASE_POLICY | UseCase 정책 | 항상 생성 / 로직 있을 때만 | **로직이 있을 때만 생성**. 승격 조건은 R-16-07(ViewModel 2개 이상 공유 또는 repository 2개 이상 조합) | S07, S02, S51 | 사용자 |
| FORMATTER | 포맷터 | ktlint-gradle 단독 / spotless+ktlint / detekt-formatting | **ktlint-gradle 14.2.0 단독**. ktlint 엔진 버전은 `ktlint { version.set("1.8.0") }`으로 고정. 최종 조문은 20-kotlin-style.md의 R-20-05가 소유하며 도구 중립이 아니라 ktlint 단독을 명시한다(초안의 R-20-22·ktfmt 언급은 폐기). ktlint-gradle는 Maven Central에 없어 저장소에 `gradlePluginPortal()`이 필요하다(실빌드 실증 2026-09-09) | S85, S56, S78, S83 | 사용자 |
| NAV3_VERSION | Navigation 3 버전 기준선 | 1.1.7 stable / 1.2.0-beta01 | **stable 1.1.7**. 결과 반환은 공유 상태·상위 ViewModel, 딥링크는 `DeepLinkPattern`+`KeyDecoder` 직접 구현 | S43, S26, S27, S28, S57 | 사용자 |
| DETEKT_LINE | detekt 계열 | 1.23.8 / 2.0.0-alpha.6 | **2.0.0-alpha.6, 플러그인 id `dev.detekt`**. AGP 9.4.0 조합은 스크래치 빌드로 실증됐다(실빌드 실증 2026-09-09). CI가 쓰는 태스크는 타입 해석이 있는 **`detektDebug`** — plain `detekt`는 타입 해석이 없어 `UnsafeCallOnNullableType`·`InjectDispatcher`·`GlobalCoroutineUsage`를 조용히 통과시킨다 | S78, S79, S80, S82, S66 | 사용자 |
| FEATURE_MODULE_SPLIT | feature 모듈 분할 기본값 | 단일 모듈 / api+impl 분할 | **단일 `:feature:*` 모듈로 시작**. 다른 feature가 키를 직접 참조해야 할 때만 그 feature를 api/impl로 분할하고, feature 간 이동은 콜백 + `:app` 조합층으로 한다 | S04, S24, S52, S03 | 사용자 |
| DI_FRAMEWORK | DI 프레임워크 | Hilt / Anvil / Metro | Hilt | S02, S05 vs S109, S103, S104 | 규칙 |
| BUILD_LOGIC_LOC | convention plugin 위치 | build-logic included build / buildSrc | build-logic included build | S53, S109 vs S69, S110 | 규칙 |
| FEATURE_BOUNDARY | feature 경계 결정 주체 | 화면 단위 / 비즈니스 로직 트리(RIBs) | 화면(또는 밀접한 화면군) 단위 | S04 vs S100 | 규칙 |
| DATA_IMPL | 데이터 계층 구현 | 직접 작성 repository / Store5 | 직접 작성 repository, Store5는 오프라인 동기화 요구 시 옵션 | S06 vs S101, S102 | 규칙 |
| STATE_HOLDER | 화면 상태 홀더 실행 주체 | AAC ViewModel / Compose 런타임 presenter | AAC ViewModel | S02, S08, S22 vs S93, S98, S91 | 규칙 |
| ONE_SHOT_EVENT | ViewModel→UI 일회성 이벤트 | UiState 환원 / Channel·SideEffect | UiState 환원. 유실돼도 무해한 Toast·햅틱만 예외. **MVI_IMPL이 허용한 Channel SideEffect는 MVI를 적용한 화면에 한정된 예외**이며, 12가 그 경계를 문장으로 못 박는다 | S02, S09, S41 vs S91 | 규칙 |
| NAV_LIBRARY | 내비게이션 라이브러리 | Navigation 3 / Navigation 2 | Navigation 3 (S29는 2025-05 시점 글이라 낡음) | S02, S43 vs S29 | 규칙 |
| BACKSTACK_API | 백스택 조작 추상화 | 리스트 직접 조작 / Navigator 래퍼 | `NavigationState`+`Navigator` 래퍼 | S25 vs S21 | 규칙 |
| UISTATE_GRANULARITY | 화면 상태 단위 | 단일 uiState / 스트림 분리 | 단일 기본, 분리는 근거 기록. `PagingData`는 항상 분리 | S02, S08 | 규칙 |
| NAV_RESULT | 화면 간 결과 전달 | 공유 상태 / ResultEventBus | NAV3_VERSION=1.1.7이므로 **ResultEventBus를 쓸 수 없다**(1.2.0-alpha02 이상 필요). 공유 상태·상위 ViewModel만 사용 | S27, S28, S43 | 규칙(NAV3_VERSION 확정 반영) |
| INDENT | 블록 들여쓰기 | 4 스페이스 / 2 스페이스 | 4 스페이스 | S35, S58 vs S106, S86 | 규칙 |
| LINE_LENGTH | 최대 줄 길이 | 100자 / 120자 | 100자. detekt `MaxLineLength`를 100으로 재설정 | S35, S106 vs S81 | 규칙 |
| KDOC_TAGS | KDoc `@param`/`@return` | 항상 태그 / 본문 통합 | 본문 통합 우선, 설명이 길 때만 태그 | S58 vs S35 | 규칙 |
| COLLECTION_ITER | 컬렉션 순회 | 고차 함수 / `for` | 변환은 `filter`·`map`, 단독 순회는 `for`(`forEach` 아님) | S58 | 규칙 |
| COMPLEXITY_NUMBERS | 복잡도 수치 임계값 | detekt 1.23.8 값 / 2.0.0-alpha.6 값 | DETEKT_LINE=2.0.0-alpha.6의 문서 확인 기본값을 그대로 쓴다. **함수 길이 60(`LongMethod`, 키 `allowedLines`) / 중첩 블록 깊이 4(`NestedBlockDepth`) / 파라미터 함수 5·생성자 6 / 복합조건 3 / 순환복잡도 14**. `MaxLineLength`만 LINE_LENGTH에 맞춰 100으로 재설정 | S80(2.0.0-alpha.6), S82(1.23.8 대조) | 규칙(도구 기본값, DETEKT_LINE 확정 반영) |
| TEST_DOUBLE | 테스트 더블 | fake / mock | fake 기본, mock은 인터랙션 검증이 목적일 때만 | S31 vs S32 | 규칙 |
| PYRAMID_RATIO | 테스트 피라미드 비율 | 70/20/10 수치화 / 수치 없음 | 수치를 규칙화하지 않는다. "small 중심, medium·big은 대표 플로우" | S30 | 규칙 |
| CI_JDK | CI JDK | 17 / 21 | 17 고정. Robolectric 4.16.1은 SDK 34·35가 Java 17, SDK 36이 Java 21을 요구하고 37은 미지원이므로, JDK 17을 지키는 스크린샷 렌더링 SDK는 35다(실빌드 실증 2026-09-09). SDK 36이 필요해지면 그 잡만 21로 올린다 | S46 vs S56 | 규칙 |
| OFFICIAL_SCREENSHOT | 공식 스크린샷 플러그인 취급 | 기본 채택 / 관찰 | 관찰 상태로만 문서화(0.0.1-alpha15) | S34 | 규칙(SCREENSHOT_LIB 종속) |
| UI_PATTERN | UI 패턴 | MVP / MVVM+UDF | MVVM+UDF. S117은 2019년 글이라 계층 원칙만 취한다 | S02, S08 vs S117 | 규칙 |
| DATASOURCE_SHAPE | DataSource 구성 | 단일 DataSource가 Remote/Cache 선택 / Remote·Local 분리 | Remote·Local 분리 | S06 vs S117 | 규칙 |
| ASYNC_TYPE | 비동기 노출 타입 | LiveData / StateFlow | StateFlow 단일화 | S41, S02 vs S122 | 규칙 |
| COMPOSE_SCOPE | Compose 적용 범위 | 신규 모듈 한정 / 전면 | 그린필드는 전면 Compose. 우회(AndroidView 래핑)는 근거와 재검토 시점 기록 시에만 | S111 | 규칙 |
| MODULE_5WAY | 기능당 5모듈 분할 | 채택 / 미채택 | 미채택. 700모듈 규모 전제이고 iOS 사례다 | S115 vs S04, S03 | 규칙 |
| ENFORCEMENT_ORDER | 규칙 집행 수단 순서 | 린터만 / 린터+CI+에이전트 | 린터(기계 판정) → CI 게이트 → 에이전트 주입(판단 필요 규칙) | S112, S114, S39 | 규칙 |
| RUN_LOCATION | 검사·테스트·빌드의 실행 위치 | 로컬 스크립트 / 자체 호스팅 러너 / PR 검사만 GitHub / GitHub 워크플로 전부 | **로컬 스크립트가 기본, GitHub 워크플로는 선택**. 푸시 전 pre-push 훅이 게이트를 돌린다. 이유는 비공개 저장소의 분량 차감과 초과 청구 | S177, S162 | 사용자(2026-09-30) |
| RELEASE | 배포 | 업로드: Play Console 직접 / r0adkll 액션 / fastlane supply / Gradle Play Publisher / API 직접 · 테스터 채널: Play 내부 테스트 / Firebase App Distribution · versionCode: 카운터 파일 / CI 실행 번호 / 커밋 수 / 직접 입력 | **사람이 Play Console에 직접 올리는 것이 기본**, 워크플로를 쓰면 `r0adkll/upload-google-play`를 수동 실행으로만. versionCode는 **저장소의 카운터 파일**(처음 고른 CI 실행 번호는 로컬 실행으로 바꾸면서 대체), 트랙은 내부 테스트까지 | S166, S163, S164, S161, S185 | 사용자(2026-09-30) |
| E2E | 전체 플로우 테스트 도구·실행 환경 | 도구: Compose UI Test / +UI Automator / Maestro · 환경: 로컬 / GitHub 러너 에뮬레이터 / Gradle Managed Devices / Firebase Test Lab | **Compose UI Test 단독, 실행은 로컬 기기·에뮬레이터가 기본**. GitHub에서 돌리면 `android-emulator-runner`, 주 2회, 사용량 95%에서 건너뜀. R8 켠 빌드의 UI Automator 검증은 미채택 | S171, S172, S175, S176 | 사용자(2026-09-30) |
| DEP_UPDATE | 의존성 자동 업데이트 | 도구: Dependabot / Renovate / Gradle 플러그인 · 자동 병합: 안 함 / 패치만 / 마이너까지 | **Dependabot, 자동 병합 안 함**. 게이트(로컬 스크립트 또는 PR 워크플로) 통과 뒤 사람이 병합 | S178, S179, S181 | 사용자(2026-09-30) |
| SERVICE_STATE | 포그라운드 서비스와 화면의 상태 공유 | Binder 바인딩 / 싱글턴 Repository의 StateFlow | **싱글턴 Repository의 StateFlow**. 공식 문서는 로컬 서비스에 Binder를 "preferred"라고 적지만 UDF·데이터 계층 규칙(R-11, R-15)과 맞추려고 고르지 않았다 | 24-background-work.md 근거 참조 | 사용자(2026-09-30) |
| PERMISSION_LIB | 권한 요청 라이브러리 | Accompanist Permissions / 공식 launcher 직접 | **launcher 직접 사용**, Accompanist 미사용(experimental, 마지막 배포 2025-04-28) | 25-runtime-permissions.md 근거 참조 | 사용자 승인 기본값(2026-09-30) |
| PERMISSION_STATE | 권한 상태를 ViewModel에 넘기는 형태 | 프레임워크 타입 그대로 / 일반 sealed·enum 값 | 런처는 Route 컴포저블이 갖고 ViewModel은 프레임워크 타입 없는 권한 상태 값만 다룬다. 요청은 사용자 동작에서 UI가 `launch()` — ViewModel이 요청 이벤트를 push하지 않는다(R-12-03·R-12-10) | 25-runtime-permissions.md 근거 참조 | 사용자 승인 기본값(2026-09-30) |
| TIME_API | 시각 API | java.time + java.time.Clock / kotlin.time.Clock + kotlinx-datetime | **java.time + java.time.Clock**. minSdk 26이라 desugaring 불필요, kotlinx-datetime 0.8.0은 experimental, `InstantSource`는 API 34 | 26-time-handling.md 근거 참조 | 사용자(2026-09-30) |
| TIME_STORAGE | Room의 시각 저장 형식 | epoch 밀리초 Long / ISO-8601 문자열 | **epoch 밀리초 Long**(`Instant.toEpochMilli`) | 26-time-handling.md 근거 참조 | 사용자 승인 기본값(2026-09-30) |
| TIME_LINT | 시각 API 직접 사용 검사 | detekt로 막기 / 문서로만 | **detekt로 막는다**(`ForbiddenMethodCall`·`ForbiddenImport`). detekt-cli 2.0.0-alpha.6 `--analysis-mode full`로 동작 확인 | 26-time-handling.md 근거 참조 | 사용자 승인 기본값(2026-09-30) |
| ROOM_VERSION | Room 버전 | 2.8.5 / 3.0.3 | **Room 3.0.3(`androidx.room3`)**. 2026-07-01 정식 출시, 공식 가이드가 2.x를 deprecated로 표기. AGP 9.4·KSP 2.3·Kotlin 2.4 조합은 실행으로 확인하지 않음 | 27-room-migrations.md 근거 참조 | 사용자(2026-09-30) |
| DESTRUCTIVE_MIGRATION | 마이그레이션이 없을 때 | 릴리스 금지 / 항상 허용 / 사용자 확인 시 허용 | **릴리스 빌드에서 금지**, debug만 허용. 공식 문서에 "프로덕션 금지" 문장은 없어 강도는 팩 결정 | 27-room-migrations.md 근거 참조 | 사용자(2026-09-30) |
| MIGRATION_TEST | 마이그레이션 테스트 | 계측 테스트 필수 / 권장만 | **스키마를 바꾼 변경은 계측 마이그레이션 테스트를 통과**해야 한다(`scripts/instrumented.sh`). 공식은 Android 모듈의 로컬 JVM 마이그레이션 테스트를 안내하지 않음 | 27-room-migrations.md 근거 참조 | 사용자 승인 기본값(2026-09-30) |
| MOTION_SCOPE | 인터랙션·모션 값의 위치와 비공식 값 표기 | 값의 위치: 팩에 값까지 / 팩은 틀·값은 프로젝트 / 프로젝트에만 · 비공식 값: 사용자 결정으로 표시 / 공식 값만 규칙화 / 참고 노트로만 | **값까지 팩에 넣고**, 공식 문서가 정하지 않은 값은 근거 줄에 "사용자 결정"으로 표시한다. SEED·토스는 공식 기준이 아니라 참고 사례다. README F.1(출처가 침묵하는 관행은 규칙으로 만들지 않는다)과는 "공식이 정하지 않음을 명시한 사용자 결정"으로 맞춘다 | S287~S305 | 사용자(2026-10-14) |
| NAV_TRANSITION | 화면 이동 전환 | 푸시·팝: shared axis X / fade through만 / Nav3 기본값 / 전환 없음 · 모달 화면: 아래→위 / 기본과 동일 / 페이드 · 스택 교체: 페이드 / 기본과 동일 / 없음 | **푸시·팝 Activity 전환(새 화면이 오른쪽에서 폭 전체로 불투명하게 덮고 이전 화면은 왼쪽 1/4 밀림, 뒤로는 반대), 하단 바는 탭 루트 엔트리 안에 그려 화면과 함께 움직임(Scaffold bottomBar 아님), 최상위 목적지(탭)는 탭마다 NavDisplay 를 두고 살려 둔다(TabHost: 숨은 탭은 배치·의미·수명주기 정지, 측정 유지, 즉시 교체), 모달 전체 화면 아래→위, 스택 교체 fade through**. 2026-10-14에는 shared axis X(폭 1/10 + 페이드)·탭 fade through·셸 하단 바로 정했으나 실기기에서 크로스페이드로 보이고 바가 따로 접혀 2026-10-15에 바꿨다. 1/4와 300ms는 시스템 설정 앱 전환 녹화에 맞춘 값이다 | S287, S288, S289 | 사용자(2026-10-14, 푸시·팝은 2026-10-15 변경) |
| PREDICTIVE_BACK | 예측형 뒤로가기 | 공식 수치 / 푸시와 같은 슬라이드 / 하지 않음 | **pop과 같은 슬라이드**(2026-10-15 변경). 2026-10-14에는 공식 수치(90%·110%·35%)로 정했으나 예측형 뒤로가기가 켜지면 뒤로 키도 이 전환을 타서 위 화면이 오른쪽으로 빠지지 않고 확대돼 R-28-01과 어긋났다 | S290, S291 | 사용자(2026-10-14, 2026-10-15 변경) |
| BACK_POLICY | 뒤로가기 | 진행 중·되돌릴 수 없는 작업 중: 중단 확인 다이얼로그 / 뒤로 막고 취소 버튼 / 즉시 이탈 | 표준 뒤로 버튼·제스처 지원과 상태 보존을 규칙으로 두고, **진행 중·되돌릴 수 없는 작업 중 뒤로가기는 중단 확인 다이얼로그**를 띄운다. 공식 품질 기준의 "custom onscreen back button prompts" 금지가 이 다이얼로그에 해당하는지는 문구만으로 단정하지 못했다 | S292 | 사용자(2026-10-14) |
| NAV_GUARD | 이동 부가 규칙 | 중복 이동 방지(`dropUnlessResumed`) / 공유 요소 전환 / 뒤로·상태 보존 / 제스처 영역 아래 배치 금지 | **`dropUnlessResumed` 채택(공식 레시피가 쓰나 이유는 문서가 침묵)**, **화면 사이 공유 요소 전환은 쓰지 않는다**(Scene 사이 끊김을 막는 `sharedTransitionScope` 전달은 예외 — 공식 문서가 엇갈려 막은 것이 아니라 첫 단계에서 쓰지 않기로 한 결정), 뒤로·상태 보존 보장 채택. 제스처 영역 아래 배치 금지는 선택하지 않았다(보류 표) | S289, S290, S292 | 사용자(2026-10-14) |
| OVERLAY_KIND | 오버레이 종류와 표현 방식 | 종류: 다이얼로그 중심 / 용도별 매핑 / 수정 · 표현: 뒤로가기·복원 기준 / 모두 Nav 엔트리 / 모두 UiState 필드 | **파괴적 확인=다이얼로그, 선택·입력=바텀시트, 가벼운 결과·되돌리기=스낵바, 지속 상태=화면 안 배너**. 뒤로가기로 닫히고 복원돼야 하면 Nav 엔트리(R-13-04), 아니면 `UiState` 필드(R-12-03). 공식에서 벗어나는 곳이 둘이다 — 공식 다이얼로그 문서는 선택지 목록도 다이얼로그 용도로 적지만 시트로 배정했고, 공식 스낵바 문서가 "Network Status"를 스낵바 용도로 적지만 지속되는 상태는 배너로 둔다 | S275, S276, S295~S297, S301, S304 | 사용자(2026-10-14) |
| OVERLAY_DISMISS | 오버레이 닫힘 | 항상 허용 / 기본 허용·파괴적·진행 중 금지 / 닫기 버튼만 | **바깥 탭·뒤로가기·시트 드래그로 기본 허용, 파괴적·진행 중은 닫힘을 끄고 닫기 버튼**, 오버레이가 열려 있으면 뒤로가기는 오버레이를 먼저 닫고 닫힘이 꺼진 오버레이에서는 아무것도 하지 않는다. 공식 앱 품질 테스트(T-Back_Nav)·토스 TDS의 경고와 부딪히는지는 문구만으로 단정할 수 없다 | S295, S302, S305 | 사용자(2026-10-14) |
| SNACKBAR_SPEC | 스낵바 규격 | SEED 값 / 공식 기본만 / 시간·위치만 다르게 | **SEED 값 채택** — 4초, 하단 중앙, 한 번에 1개와 큐, 텍스트 액션 최대 1개, 누르고 있으면 타이머 정지. Material3 1.4.0 기본 `Snackbar`에는 타이머 정지가 없어(소스 확인) 직접 구현해야 한다 | S296, S301 | 사용자(2026-10-14) |
| OVERLAY_MOTION | 오버레이 등장·퇴장 모션 | 퇴장이 등장보다 짧게 / 라이브러리 기본값 / 시트만 spring | **등장 300ms 감속, 퇴장 200ms 가속**(다이얼로그 페이드, 시트 아래 슬라이드). Material fade 기본(150ms·75ms 선형)과 다르다. 라이브러리 컴포넌트의 기본 모션은 바꾸지 않는다 | S287, S302 | 사용자(2026-10-14) |
| MOTION_TOKENS | 모션 토큰 | 이징: bezier+spring / bezier만 / spring만 · 구성: 3+3 / 3+1 · 쓰임: 입력에 이어지면 spring / 전환은 모두 bezier | **bezier 3종 + spring 3종, 시간 150·200·300ms**. 곡선과 시간은 Material 값(Short3·Short4·Medium2), spring은 Material spatial 3단(damping 0.9, stiffness 1400·700·300)을 쓴다 — 토스는 수치를 공개하지 않았다. 손에 이어지면 spring, 시간이 정해지면 bezier. `:core:designsystem` 밖의 숫자 리터럴은 Konsist로 막는다 | S287, S294, S303 | 사용자(2026-10-14) |
| MOTION_GENERAL | 모션 일반 규칙 | 한 요소에 하나의 모션 / 그리기 단계 애니메이션 / 콘텐츠 상태 전환 Crossfade / 목록 항목 변화 애니메이션 | **네 가지 모두 채택**. 실시간 수치는 숫자 즉시 교체·게이지만 그리기 단계 보간 | S293, S303, S306 | 사용자(2026-10-14) |
| PRESS_FEEDBACK | 누름 피드백 | 색+축소 / 색만(리플) / 축소만 | **색 변화 + 거리 기준 축소 150ms** — 거리 2dp, 기준 길이 `max(높이, 폭÷4, 24dp)`(SEED 방식). 터치 영역 48dp | S292, S300 | 사용자(2026-10-14) |
| LOADING_POLICY | 로딩 표현과 표시 지연 | 목적별 / 스피너 단일 / 스켈레톤 단일 · 지연: 200 / 300 / 500ms | **목적별**(콘텐츠 스켈레톤, 짧은 동작 스피너, 진행률을 아는 작업 진행 막대), **300ms 안에 끝나는 로딩은 표시하지 않는다**. 지연값은 공식·SEED·토스 모두 공개하지 않은 사용자 결정 | S307 | 사용자(2026-10-14) |
| HAPTIC_SWITCH | 햅틱 | 확정 순간에만 / 쓰지 않음 / 화면별 · 켜는 방식: 전역 스위치 / 컴포넌트 매개변수 / 자리 없음 | **지금은 쓰지 않고**, `:core:designsystem`에 전역 스위치(`LocalHapticsEnabled`, 기본 꺼짐)만 둔다. 컴포넌트마다 매개변수를 두지 않는다 | S300, S308 | 사용자(2026-10-14) |
| REDUCE_MOTION | 시스템 "애니메이션 줄이기" 대응 | 이동·축소 끄고 페이드·색 유지 / 시스템에 맡김 / 앱 내 토글 | **시스템 애니메이션 배율을 덮어쓰지 않고 상태를 모션 없이도 알아볼 수 있게 설계한다.** 사용자가 처음 고른 "이동·축소 끄고 페이드·색 유지"는 기본 Compose 경로에서 `MotionDurationScale.scaleFactor`가 0이면 모션이 다음 프레임에 끝나(S298) 페이드도 즉시 끝나고, 앱이 배율을 덮어쓰는 방법은 확인하지 못해 보류했다. `pressScale`의 축소도 꺼지지 않고 최종값이 즉시 적용된다 | S298, S300 | **사용자 원안과 다름 — 재확인 필요**(2026-10-14) |
| GESTURE_POLICY | 제스처(스와이프·롱프레스·당겨서 새로고침) | 버튼 대안 필수 / 표준 제스처만 / 자유 | **자유 — 정하지 않는다.** 이 영역은 규칙이 없다 | — | 사용자(2026-10-14) |
| PERF_FRAME | "버벅임 없음"의 통과선 | 판정: 공식 slow(16ms)·frozen(700ms)만 / JankStats식 2배 / `FrameTimingMetric` P95·P99 분포 · 시작: Vitals 회귀 한계만 / 공식 목표 500·200·150ms | **프로즌 0건(공식) + `frameOverrunMs` P95 ≤ 0 + P99 ≤ 프레임 간격 1개(제안)**. 비교는 janky 개수가 아니라 프레임 시간 분포로, 같은 기기·OS의 A/B로 한다(공식). 시작은 공식 목표 500·200·150ms를 목표선으로, R-40-02(5초·2초·1.5초)는 회귀 한계로 둔다. Android vitals 임계 표에 슬로 렌더링은 없다(확인). 5%와 2배는 공식이 정한 값이 아니다 — 2배는 JankStats 기본 jank 정의에서 가져왔다 | S310, S311, S312, S314, S315, S331 | 제안(사용자 위임, 2026-10-15) |
| PERF_MEASURE | 프레임 측정의 대상·조건·실행 위치 | 대상: 앱 전체 한 수치 / 동작별 · 조건: 디버그 / 릴리스 상당 · 반복: 5회 / 10회 이상 · 실행: 게이트 / 릴리스 준비 때 수동 | **동작별(푸시·팝·탭 첫 진입과 재진입·콜드 스타트 첫 화면·고빈도 화면 5초), R8을 켠 non-debuggable·profileable 변이를 실기기에서, 10회 이상, 화면·탭 첫 진입은 `startupMode = null`과 setup 의 `killProcess()`로·콜드 스타트 첫 화면은 `StartupMode.COLD`·재진입은 워밍업 뒤, 게이트에 넣지 않고 릴리스 준비 때 수동, 변형 비교는 번갈아 설치해 2라운드 이상 재고 렌더 프레임 수를 같이 적는다(R-41-18)**. 동작 목록·반복 횟수는 공식 문서에 없다(공식 예는 5회) | S312, S313, S314, S316, S322 | 제안(사용자 위임, 2026-10-15) |
| PERF_COMPOSE | 첫 프레임·안정성·고빈도 갱신 규칙 | 화면 밖 내용: `Column` / lazy · 구조: 깊이 줄이기 / 두기 · 컬렉션: 안정 타입으로 바꾸기 / 같은 인스턴스 재사용 · Compose 없는 모듈의 모델: 래퍼 / 설정 파일 / `@Immutable` · 고빈도 값: UiState 필드 / 분리 | **보이는 내용만 컴포지션(무거운 블록은 lazy 항목), 구조를 납작하게 만들지 않는다(공식: 깊이는 성능에 영향이 없다), `BoxWithConstraints`는 부모 제약이 필요할 때만, 같은 내용이면 같은 컬렉션 인스턴스를 방출하고 큰 컬렉션의 구조 비교(`ImmutableList`·`@Immutable`)는 정말 필요할 때만(공식: O(N) `equals()`가 재구성보다 비쌀 수 있다), Compose 없는 모듈의 모델은 값이 같은 작은 모델이 자주 새로 만들어질 때만 안정성 설정 파일로 선언, 고빈도 값은 UiState에서 떼어 읽는 범위를 좁힌다**. 안정성은 릴리스 컴파일러 리포트로만 판다 | S306, S317~S323, S328, S338~S343 | 제안(사용자 위임, 2026-10-15) |
| PERF_RENDER | 레이어·그리기 비용 | 투명도: `Modifier.alpha` / `graphicsLayer` / 색 알파 · 큰 컨테이너 alpha: 허용 / 상태 표현에는 금지 · 그리기 객체: 매번 생성 / 캐시 | **색 알파 우선, 애니메이션 알파는 `graphicsLayer` 람다(겹침이 없으면 `ModulateAlpha`), 눌림·비활성 같은 오래 유지되는 상태를 큰 컨테이너의 alpha<1로 표현하지 않는다(R-28의 전환·`Crossfade`는 예외), alpha<1 레이어는 레이아웃 경계 밖 내용을 자른다(공식), 그리기 객체는 `drawWithCache`·`remember`, 중복 불투명 배경 제거**. `clip`·`BoxWithConstraints`의 비용 수치는 공식에 없다 | S324~S327, S329 | 제안(사용자 위임, 2026-10-15) |
| PERF_MEMORY | 메모리 예산과 상주 범위 | 예산: 없음 / 공식 vitals 임계만 / 앱 PSS 상한 · 비활성 탭 ViewModel: 남김 / 제거 / 설계가 정함 | **공식 상한은 Android vitals 임계(Anonymous RSS+Swap은 RAM 구간·상태별, 비트맵 메모리 200·200·400MB)이고 앱 예산은 핵심 동작 순회 뒤 TOTAL PSS 200MB 이하(제안)**, 증가 추세로 누수를 본다. 탭 상주 여부는 설계가 정해 문서에 남기고 측정한다(공식 레시피는 시작 탭과 현재 탭의 엔트리만 남기고 그 밖의 탭 엔트리를 제거한다). StrictMode·LeakCanary는 디버그에만 | S22, S23, S281, S329~S336 | 제안(사용자 위임, 2026-10-15) |
| PERF_BASELINE | Baseline Profile 모듈 | 모듈: `:baselineprofile` 하나 / `:baseline-profile`(공식 예 이름) / 앱 모듈 안 · R-10-01: 예외로 허용 / 유형 추가 | **`:baselineprofile`(`com.android.test`) 하나를 R-10-01의 예외로 허용**하고 R-40-03을 이행한다. 프로파일 생성과 Macrobenchmark가 같은 모듈이다. 이름은 공식 구성 문서의 "likely the `:baselineprofile` test module"에 맞춘 팩 선택이다 | S16, S37, S269, S313, S337 | 제안(사용자 위임, 2026-10-15) |
| SINGLE_TASK | 딥링크를 받는 단일 Activity 앱의 `launchMode` | 기본값 유지 / `singleTop` / `singleTask` | **`MainActivity`만 `singleTask`**(R-18-05 예외) — 외부·알림 링크가 두 번째 인스턴스를 만들지 않고 `onNewIntent`로 온다. Activity가 하나라 "위 Activity를 지우는" 부작용이 없다. 공식 문서는 단일 Activity를 예외로 적지 않는다. 플래그 없는 외부 링크의 Back은 미확인(보류 표) | S156, S344 | 사용자(2026-10-15, InfocarEV) |
| ENTRY_GATE | 앱 전체 선행 조건(서버 점검·온보딩·로그인·약관) | 목적지 마커 + `Navigator` 리다이렉트(R-13-10 원안) / 앱 루트 시작 게이트 | **시작 게이트** `StartupState`(Loading / Entry(step) / Ready). 진입 흐름은 저장소 상태에서 파생한 한 장짜리 백스택이고 게이트 전 링크는 `SavedStateHandle`에 문자열로 보류해 `Ready` 뒤 한 번 연다. 링크는 입구에서 스킴·길이(256자)를 먼저 검사한다(R-13-08). 목적지 단위 조건은 R-13-10 원안 그대로 | S280, S345, S346 | 사용자(2026-10-15) |
| TAB_RETENTION | 하단 탭 화면의 유지 | `NavDisplay` 전환으로 매번 재구성 / 탭마다 `NavDisplay`를 살려 두기 | **탭마다 `NavDisplay`·백스택을 두고 숨은 탭은 배치·의미·수명주기를 멈춘다(측정은 유지), 전환은 즉시 교체**(NAV_TRANSITION 행의 세부, R-28-01). 숨은 탭의 의미는 `clearAndSetSemantics {}`로 비운다 — 비우지 않으면 계측 테스트가 숨은 탭의 글자를 잡는다(R-30-11) | S281, S349 | 사용자(2026-10-15) |
| NAV_QUEUE | 전환 중 들어온 이동 처리 | 버림 / 덮어씀 / 줄 세움 | **`Navigator` 큐로 줄 세움**(전환 길이 × 애니메이터 배율, 상한 4). 뒤로가기도 큐. 탭 전환·링크는 큐를 비움(탭은 즉시, 링크는 적용 뒤 소비). 떠나는 화면 탭은 `dropUnlessResumed`로 버림 | 프로젝트 사례(S22+ 녹화, 2026-10-15) | 사용자 |
| DATASTORE_WRITE_RESULT | 영속 저장소(DataStore) 쓰기·읽기 오류 | 쓰기: 예외 전파 / 결과 타입 · 읽기: `catch`+기본값 / `retryWhen` 재구독 | **쓰기는 `Saved`/`Failed` 결과 타입(R-15-11), 읽기 오류는 `retryWhen`+지연 재구독**(첫 값이 없을 때만 기본값 1회, R-22-12). `catch { emit(기본값) }`은 플로우를 끝내 상태가 기본값에 굳는다(리뷰에서 잡힌 결함). 공식 DataStore 가이드에는 `IOException` 예시가 없다 | S352, S353 | 사용자 승인 기본값(2026-10-15) |
| SERVICE_START_RETRY | 연결 유지 서비스의 백그라운드 시작 거부·배터리 최적화 안내 | 거부: 실패로 끝냄 / 보류·재시도 · 안내: 시스템 목록 화면 / 직접 요청 / 제조사 화면 | **보류 → 루트 Activity `onStart`에서 재시도, 알림 "끊기"는 `getForegroundService`+`FLAG_IMMUTABLE`, 안내는 `ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS`만**(R-24-15·R-24-16). 직접 요청은 Play 정책의 허용 사례에만, 제조사 화면은 만들지 않는다 | S193, S209, S354, S356, S357 | 사용자(2026-10-15) |
| UNUSED_IMPORTS | 미사용 import 검사 | ktlint 기본값(1.7+ 꺼짐) / 켬 / detekt | **ktlint `no-unused-imports` 켬**(`.editorconfig`, R-20-05). 오탐은 그 자리만 suppress | S351 | 사용자 승인 기본값(2026-10-15) |
| EAGERLY_REASON | `SharingStarted.Eagerly` 허용 범위 | 금지 / 구독 없이 `value`를 읽는 상태만 · 사유: 뷰모델 단위 / 상태마다 | **구독 없이 `value`를 읽는 상태만(예: 스플래시 유지 조건이 읽는 시작 게이트), 사유는 상태마다 따로**(R-12-06). 두 상태를 한 사유로 묶은 것이 리뷰에서 걸렸다 | S347, S10 | 사용자 승인 기본값(2026-10-15) |

## R-ID 중복·겹침 정리

조사 노트가 각자 붙인 후보 ID 중 같은 규칙을 가리키는 것들이다. 원칙: **UiState 형태·상태 홀더 규칙은 12, 계층 의존 규칙은 11, Compose 컴포저블 규칙은 17이 소유**한다. 소유 파일이 아닌 파일은 그 규칙을 다시 쓰지 않고 참조만 한다.

> **이 표의 번호는 조사 노트의 초안(DRAFT) 후보 ID이고, 최종 `references/` 파일의 R-ID와 일치하지 않는다.** 규칙을 찾을 때는 최종 파일을 보고 이 표는 "초안에서 어느 파일이 그 주제를 가져갔나"의 기록으로만 읽는다. 특히 초안 R-11-04~R-11-08은 최종 11-architecture-layers.md에서 전혀 다른 규칙(Android 타입 의존 금지·data의 상위 참조 금지·계층 간 통신·모델 매핑·`AndroidViewModel` 금지)이 쓰고 있고, 초안 R-10-11·R-13-09가 가리키던 feature 모듈 조건부 분할 규칙은 최종 **R-10-13**이다(최종 R-10-11은 `subprojects {}`·`allprojects {}` 주입 금지).

| 겹치는 후보 ID | 관계 | 소유 파일 | 처리 |
|---|---|---|---|
| R-11-04 ↔ R-12-04 + R-12-05 | 동일(화면=ViewModel, 재사용=plain holder) | 12 | 11에서 삭제 |
| R-11-05 ↔ R-12-03 | 동일(단일 uiState + 관련 상태 묶기) | 12 | 11에서 삭제 |
| R-11-06 ↔ R-12-11 ↔ R-23-12 | 동일(일회성 이벤트 push 금지) | 12 | 11·23에서 삭제 |
| R-11-08 ↔ R-12-06 | 동일(`collectAsStateWithLifecycle` 수집) | 12 | 11에서 삭제 |
| R-11-07 (`AndroidViewModel` 금지) | 대응 없음. 프레임워크 의존 차단이므로 계층 규칙 | 11 | 유지. 12는 재기술 금지 |
| R-12-13 ↔ R-17-19 | 동일(호이스팅 위치). 17이 3원칙으로 더 완전 | 17 | 12에서 삭제 |
| R-12-14 (애니메이션 suspend 스코프) | Compose 전용 규칙 | 17 | 12 → 17 이관 |
| R-12-17 ↔ R-22-02 ↔ R-15-05 | 대상이 다름(상태 홀더 / suspend 함수 / data 계층) | 12·22·15 | 각자 유지, 문장에 대상 명시 |
| R-00-03 ↔ R-11-02 ↔ R-12 전반 | 원칙(UDF) vs 적용(의존 방향·상태 흐름) | 00·11 | 12는 참조만 |
| R-00-04 ↔ R-12-01 | 원칙(불변 노출) vs 적용(UiState 형태) | 00·12 | 둘 다 유지, 12가 UiState 문장 소유 |
| R-00-06 ↔ R-15-05 / R-16-06 / R-22-02 | 원칙(main-safe) vs 계층별 적용 | 00 원칙 1줄, 나머지는 계층 파일 | 유지 |
| R-00-08 ↔ R-30-02 ↔ R-15-12 | 동일(fake 우선) | 30 | 00은 원칙 1줄, 15는 data 테스트 구성만 |
| R-11-03 ↔ R-15-04 ↔ R-22-05 | 다름(계층 간 통신 / data 노출 형태 / 코루틴 사용법) | 11·15·22 | 유지 |
| R-11-10 ↔ R-20-05 · R-20-09 | 다름(아키텍처 네이밍 / 언어 네이밍) | 11·20 | 유지 |
| R-10-11 ↔ R-13-09 | 해소됨. FEATURE_MODULE_SPLIT=단일 시작이 초안 R-10-11의 조건부 분할을 채택 | 10 | **완료. 흡수처의 최종 번호는 R-10-13**("`:feature:*`는 단일 모듈로 시작하고 조건이 맞을 때만 나눈다"). 초안 R-13-09는 독립 규칙으로 쓰지 않았고 최종 13-navigation.md에 없다 |
| R-20-22 ↔ R-32-03 · R-32-06 | 해소됨. FORMATTER=ktlint-gradle 단독 | 20 | **완료.** 포맷터 조문은 최종 파일에서 **R-20-05**("포맷은 손이 아니라 포맷터가 정한다")가 소유한다. 초안의 ktfmt 언급과 2-스페이스 전제는 지웠고 INDENT·LINE_LENGTH를 따른다. 초안 번호 R-20-22는 최종 파일에 없다 |
| R-21-02 · R-21-06 · R-21-08 | 해소됨. DETEKT_LINE=2.0.0-alpha.6 | 21 | 파라미터 5·6 / 복합조건 3 / 순환복잡도 14로 확정 기입 |
| R-30-08 · R-30-09 | 해소됨. SCREENSHOT_LIB=Roborazzi 1.74.0 | 30 | 초안 문장 그대로 확정. `@GraphicsMode(NATIVE)` 포함 |
| R-13-02 · R-13-13 · R-13-15 | 해소됨. NAV3_VERSION=1.1.7 | 13 | 1.1.7 고정, 결과는 공유 상태, 딥링크는 직접 구현으로 확정 |
| R-12-15 · R-12-16 | 해소됨. MVI_IMPL=순수 구현 | 12 | MVI 라이브러리 미도입. R-12-15는 "라이브러리 없이 sealed Intent+`reduce`를 더한다"로 재작성 |
| R-16-01 · R-16-07 | 해소됨. USECASE_POLICY=로직 있을 때만 | 16 | 초안 문장 그대로 확정 |
| R-23-02 · R-23-03 · R-23-04 · R-23-05 | 해소됨. ERROR_TYPE=커스텀 sealed + nullable | 23 | 초안 문장 그대로 확정. `kotlin.Result`·Arrow는 규칙에서 배제 |

## 보류 (출처 없는 관행)

| 관행 | 어디서 왔나 | 왜 보류 |
|---|---|---|
| 컬렉션 체이닝 단계 상한 | kotlin-style R-21-13 | S58은 포맷만, S60은 "multiple steps"만. detekt에 체인 길이 규칙 없음. 전 출처 침묵 |
| 함수 길이·중첩 깊이·파라미터 수 상한을 **언어/스타일 권위가 정해주지 않는다** | kotlin-style R-21-01·R-21-02·R-21-03 | 수치 자체는 보류가 아니다. detekt 문서 확인 기본값(S80)을 COMPLEXITY_NUMBERS에서 확정했다. 보류로 남는 것은 S35·S58·S106이 어떤 수치도 제시하지 않아 **린터 기본값을 그대로 채택한 것이 팩의 판단**이라는 점뿐이다. 팀이 다른 수치를 원하면 근거 없이 바꿔도 되는 자리 |
| `!!` 전면 금지 | kotlin-style R-23-01 | S62는 금지하지 않고 조건부 허용, S106은 초기화 보장 인스턴스 변수에만. 전면 금지를 명시한 출처 없음 |
| `when`의 `else` 필수 | kotlin-style 질문표 | S106(Kodeco)만 명시. S35·S58 침묵 |
| Sequence 전환 임계(크기·단계 수) | kotlin-style R-21-14 | S60이 수치 제시를 명시적으로 거부("decide which one is better for your case") |
| 테스트 피라미드 비율(70/20/10) | testing-ci 충돌표 | S30에 그림만 있고 수치 없음. 업계 통설 |
| 커버리지 임계값 % | testing-ci R-30-12 | NiA(S56)는 리포트만 생성하고 게이트가 없음. 임계값 근거 출처 없음 |
| "예외 삼키기 금지" 정적 규칙 | kotlin-style 미확인 | detekt exceptions 룰셋 페이지 조회 실패. `SwallowedException` 기본값 미확인 |
| ktlint 스타일별 `max_line_length` 기본값 | kotlin-style 미확인 | pinterest.github.io 문서 404. 스타일별 기본값 확인 실패 |
| MVI 도입 판단선("상태 전이 검증 필요 시") | state-nav 섹션 3 | 노트의 종합 해석. 이 기준선을 문장으로 제시한 출처 없음 |
| Nav3 `entry` 안의 `hiltViewModel()` 사용 | state-nav R-13-07 | **해소됨.** 공식 navigation-3 스킬의 Hilt 연동 레시피가 `entry` 안 `hiltViewModel()` + `rememberViewModelStoreNavEntryDecorator()` 조합을 제시하고, 스크래치 빌드로 동작을 확인했다(2026-09-09) |
| Mavericks "저활동" 판정 | state-nav 비교표 | 릴리스·push 날짜에서 추론. 공식 유지보수 선언 미확인(사이트 본문 비어 반환) |
| Konsist `withAllParentsOf`·`hasDataModifier`·`hasOperatorModifier`·단수형 `primaryConstructor` | testing-ci 미확인 | **부분 해소.** `hasDataModifier`·`hasOperatorModifier`는 Konsist 0.17.3 공개 소스로 존재를 확인했고 `enforcement/konsist/ArchitectureTest.kt`가 실제로 쓴다(스크래치 빌드 통과 — 검증 기록은 `enforcement/README.md`). 보류로 남는 것은 `withAllParentsOf`·단수형 `primaryConstructor` 둘뿐이고, 이 둘은 `withParentClassOf`·`hasPrimaryConstructor`·`primaryConstructors`로 대체한다 |
| androidx.test(core/runner/rules/ext-junit) 안정 버전 | testing-ci 미확정 | **해소됨(2026-09-30).** 릴리스 노트 확인값은 core·runner·rules 1.7.0, ext-junit 1.3.0, espresso 3.7.0이다(S174). NiA 카탈로그는 여전히 `-rc01`이라 복사하지 않는다. 실빌드로는 아직 돌려 보지 않았다 |
| 버전 조합 검증(KSP 2.3.11+Kotlin 2.4.20, detekt 2.0.0-alpha.6+AGP 9.4.0, Robolectric 4.16.1+SDK 36, Hilt 2.60.1+Kotlin 2.4.20) | testing-ci 리스크 목록 | **해소됨(실빌드 실증 2026-09-09).** 공식 문서 명시는 여전히 없지만 스크래치 빌드로 전부 통과 확인. 단 Robolectric×SDK 36은 Java 21을 요구해 SDK 35로 내렸고, compileSdk는 36→**37**, Gradle 래퍼는 **9.7.1**로 올려야 했다 |
| 국내 사례(P1~P13)의 조문 승격 | korea 요약 | 12건 중 문서화된 스타일 가이드 0건. 전부 경험 공유 글이라 단일 조직 1~2건 근거 |
| 에이전트 동시 주입 규칙 수 상한(2개·4개) | korea P7 | 근거가 S114 한 건. 다른 조직 교차 확인 없음 |
| 큰 화면 오버레이 최대 폭 | 모션 정리 질문(SEED 시트 480·스낵바 464) | 사용자가 선택하지 않았다. 폴더블·태블릿 대응이 필요해지면 정한다. 공식 근거는 없다 |
| 시스템 제스처 인셋 아래에 터치·드래그 대상을 두지 않기 | 예측형 뒤로가기 공식 디자인 문서의 권고(S290) | 사용자가 선택하지 않아 규칙으로 올리지 않았다. 공식 권고이므로 첫 feature 화면에서 다시 묻는다 |
| Nav3 기본 전환의 모양, 앱이 직접 정한 예측형 전환에 공식 수치를 따라야 하는지 | 인터랙션·모션 조사(S288, S290) | 문서에 기본 모양이 적혀 있지 않다. 뒤로 키가 `predictivePopTransitionSpec`을 타는 것은 실기기에서 확인했다(2026-10-15) |
| 시스템 "애니메이션 줄이기"가 Compose `MotionDurationScale`에 연결되는 방식, 앱이 줄이기 상태를 읽는 공식 방법 | 인터랙션·모션 조사(S298) | 공식 레퍼런스는 scaleFactor 0의 동작만 적는다. 비공식 논의에서만 연결이 보였다 |
| 앱 수준 "모션 줄이기"(이동·축소는 끄고 페이드·색 유지) | REDUCE_MOTION의 원안 | 기본 Compose 애니메이션 경로에서는 배율 0에서 페이드도 즉시 끝나므로 원안을 하려면 앱이 배율을 덮어써야 한다. 덮어쓰는 방법은 확인하지 못해 채택하지 않았다 — 사용자 재확인 필요 |
| Compose Material 3 `MotionScheme` | 인터랙션·모션 조사 | Material3 1.4.0 소스에서 `internal interface`다. 릴리스 노트상 `MaterialTheme.motionScheme`은 1.5.0 알파 줄에서 보인다. 공개·안정되면 토큰 정의를 갈아탈지 판단한다 |
| 스낵바 "누르고 있으면 타이머 정지"의 Material 3 기본 지원 | R-28-11 | Material3 1.4.0 `SnackbarHost.kt`·`Snackbar.kt`에 해당 코드가 없다(검색 0건). 직접 구현해야 하고 그 구현은 만들지 않았다 |
| 슬라이드 거리(폭의 1/10)·spring 3단·`modalPush`(공식 `KeepUntilTransitionsFinished`)의 하층 화면 유지·`pressScale`의 체감과 `clickable` 순서에 따른 히트 영역·리플 차이의 시각 확인 | 모션 템플릿 | 스크래치 프로젝트에서 컴파일·ktlint·detekt·Konsist까지만 확인했고 화면에서 움직임을 보지 않았다 |
| 첫 진입 프레임에서 텍스트·`Canvas`·벡터의 첫 그리기 비용, 노드 수·깊이가 컴포지션에 주는 비용, `LazyListState` 초기 위치의 비용 | 성능 조사(S306, S327, S328) | 공식은 수치를 적지 않고 깊이는 성능에 영향이 없다고만 적는다. 규칙으로 만들지 않고 R-41-04의 트레이스로 확인한다 |
| `BoxWithConstraints`·`SubcomposeLayout`·`clip`·그림자의 비용 수치 | 성능 조사(S323, S326, S343) | 공식은 구조(자식 컴포지션이 레이아웃 단계에 의존)와 "별도 그래픽 레이어"만 적는다. R-41-06은 그 구조를 이유로 둔 제안이다 |
| 알파가 1과 1 미만을 오갈 때 레이어 생성·소멸 비용, clip + drawBehind + alpha 조합에서 배경이 사라진 원인 | InfocarEV_Android 실기기 사례(2026-10-15) | 원인을 확인하지 못했다. 공식 `Modifier.alpha` 문서의 "alpha<1이면 내용이 경계로 잘린다"가 가능한 설명(가설)이다 |
| `dumpsys gfxinfo`의 출력 형식·통계 초기화 인자(`reset`), gfxinfo percentile과 `frameOverrunMs`의 환산 | 성능 조사(S316) | 공식 문서는 명령과 `framestats`만 적는다. R-41-01의 통과선은 두 도구에 같은 뜻으로 옮긴 것이다 |
| PSS와 Android vitals 지표(Anonymous RSS+Swap)의 환산, 앱 PSS 상한 | 성능 조사(S331, S332) | 공식에 앱 예산이 없다. R-41-16의 200MB는 제안이고 두 지표를 함께 적어 환산 근거를 모은다 |
| `kotlinx.collections.immutable`의 현재 안정성 | 성능 조사(S319) | 공식 문서는 "still in alpha"라고 적는다. 현재 버전 상태는 확인하지 못했다 |
| Baseline Profile이 화면 이동·첫 진입 프레임에 주는 효과의 크기 | InfocarEV_Android 실측(S22+ 한 대, 2026-10-15) | **일부 해소.** 공식은 코드 실행 속도 약 30% 개선만 적고 프레임 수치는 없다. 같은 설치에서 컴파일 모드만 바꿔 잰 결과 프로파일은 p99를 약 11~28% 줄였고(설정 푸시·팝 53ms → 38~40ms가 가장 컸다) 버벅임 비율은 설정에서만 뚜렷했으며 speed-profile과 speed는 비슷했다(R-41-17 근거 끝). 한 기기·2라운드·오차 ±1.5%p·적응형 주사율이라 다른 기기·앱 상태에서의 크기는 미확인이다 |
| 전환 중 이전 화면이 계속 컴포지션되는 범위, 비활성 탭 엔트리의 상주 범위(프로젝트의 `NavigationState`) | 성능 조사(S22, S281) | 공식 레시피는 시작 탭과 현재 탭의 엔트리만 남기고 그 밖의 탭 엔트리를 제거한다고만 적는다. 템플릿은 백스택이 하나라 다중 백스택 동작을 검증하지 않았다 |
| `LayerOutsets`(Compose 1.12.0-alpha01+)가 이 팩의 Compose BOM(2026.08.00)에 들어 있는지 | 성능 조사(S324) | 공식 문서가 알파 버전부터라고만 적는다. R-41-11 ④는 "미확인"으로만 언급한다 |
| Baseline Profile 플러그인이 만드는 `benchmarkRelease` 변이를 돌리는 Gradle 태스크 이름 | 성능 조사(S313, S269) | 공식은 일반 Macrobenchmark 모듈의 `connectedCheck`만 적는다. R-41-03은 모듈 구성에 따른다고만 쓴다 |
| 변형 사이에 렌더한 프레임 수가 17% 달랐던 원인, 주사율을 고정하는 방법 | InfocarEV_Android 실측(S22+, 2026-10-15) | 적응형 주사율(10~120Hz)을 의심하지만 확인하지 못했다. 공식 문서에서 고정 방법을 찾지 못했다. R-41-18은 프레임 수를 같이 적게만 한다 |
| 하단 바를 직접 그린 Row+selectable로 바꿨을 때 탭 왕복 버벅임이 약 3% → 6~7%로 보인 원인 | InfocarEV_Android 실측(S22+, 2026-10-15) | 렌더 프레임 수도 17% 줄어 주사율 변화를 의심하지만 확인하지 못했다. 되돌렸다. R-41-18의 사례다 |
| 하단 바를 탭 루트 엔트리 안에 그리는 새 셸의 성능 비용(설정 푸시·팝 버벅임 약 5.3% → 약 9%, 홈 팝 프레임 41~46ms → 54~65ms) | InfocarEV_Android 실측(S22+, 2026-10-15) | 하단 바 컴포지션 약 2.8ms·엔트리 레이어 추가·`applyChanges` 증가를 의심하지만 셸만 되돌린 변형으로 분리하지 못했고 구 셸은 다른 시각에 쟀다. 모양을 우선한 사용자 결정이라 R-28-01 근거에 비용만 적었다 |
| 플래그 없이 온 외부 앱 링크(브라우저)에서 `singleTask` 루트 Activity의 Back 동작 | InfocarEV 진입 설계(S344) | 실기기로 보지 못했다. `adb shell am start`는 항상 `FLAG_ACTIVITY_NEW_TASK`를 붙여 재현하지 못한다. R-18-05 예외 근거에 미확인으로 적었다 |
| `onNode`가 여럿 잡힐 때 실패한다는 공식 문장, DataStore 읽기 `IOException` 처리의 공식 예시, 서비스 시작 거부 뒤 재시도 시점 | 테스트·DataStore·서비스 조사(S355, S352, S193) | 공식 페이지에서 찾지 못했다(DataStore 가이드는 2026-10-15 현재 `CorruptionException`만 다룬다). 실행으로 확인한 동작만 R-30-11·R-22-12·R-24-15에 적었다 |
| 눌림 상태 정리(4벌 → 1개)·`PaddingValues` 읽기 단계·포맷터 캐시·빈 호스트 지연의 성능 효과 | InfocarEV_Android 트레이스(S22+, 2026-10-15) | 호출 횟수·시간은 측정했으나 전후 jank 차이가 오차(±1.5%p) 안이라 개선은 측정하지 못했다. R-41-19는 "낭비를 줄였다, 효과는 측정 못 했다"로 쓰게 한다. 화면을 여러 프레임에 나눠 구성하는 변형과 노드 수를 줄이는 구조 변경의 효과도 재지 않았다 |
