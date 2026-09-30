# 채택 결정과 보류

> 출처 번호(S..)는 [`90-sources.md`](90-sources.md). 이 파일은 규칙 문서가 아니라 결정 기록이라 `check-pack.sh`의 규칙 5요소 검사에서 제외한다.

## 결정 표

`키`는 규칙 문서들이 참조하는 확정값 이름이다. 46건 모두 확정됐고 빈 칸은 없다. 아래 표의 `근거 출처`·`결정자` 칸이 선택지별 판단 근거를 그대로 담고 있으며, 짧은 요약은 `android/README.md`의 확정 결정 요약에 있다.

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
