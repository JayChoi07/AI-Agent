# 국내 대기업 Android 공개 사례 조사 노트

조사일: 2026-09-09
담당 references 파일 번호: 없음(사례 보강 전용). 규칙 초안 대신 **사례 표**와 **보강 제안 표**를 쓴다.

**요약**: 국내 기업은 형식을 갖춘 "스타일 가이드 문서"를 거의 공개하지 않는다. 이번 조사에서 확인한 12건 중 **문서화된 스타일 가이드는 0건**, 전부 경험 공유 글이거나 컨퍼런스 발표다. 다만 LY(LINE)와 토스 일부 글은 팀 내부 규칙을 조문 형태로 적어두어 규칙 승격이 가능하다.
**커버리지 한계**: 당근(medium.com/daangn)과 네이버(d2.naver.com)는 **한 건도 읽지 못했다**(차단). 카카오 본체(tech.kakao.com) Android 태그는 비어 있어 계열사(카카오페이·카카오뱅크)로 대체했다. 상세는 맨 아래 "미확인·접근 불가" 참조.

---

## 출처

| # | 조직 | 문서명 | URL | 문서 종류 | 최종 갱신 |
|---|------|--------|-----|-----------|-----------|
| S1 | LY(LINE) | Jetpack Compose로 LINE 앱 Yahoo!검색 모듈 개발하기 | https://techblog.lycorp.co.jp/ko/developing-android-ui-with-jetpack-compose | 기술 블로그(팀 규칙 조문 포함) | 2023-10-11 (조세영) |
| S2 | LY(LINE) | 린트 적용으로 코드 대량 변경 시 AST를 이용해 검증하기 | https://techblog.lycorp.co.jp/ko/using-ast-to-verify-the-code-after-code-linting | 기술 블로그 | 2024-02-23 (윤종민) |
| S3 | LY(LINE) | AI 에이전트를 위한 Android CLI: 대규모 모바일 개발 환경에 적용하기 | https://techblog.lycorp.co.jp/ko/android-cli-for-ai-agents-at-scale | 기술 블로그(운영 규약 포함) | 2026 추정 (차영호) — 본문에 발행일 명시 없음 |
| S4 | 토스 | AI가 팀 규칙을 지키도록 하는 방법 (Coding Agent를 위한 Stylepack) | https://toss.tech/article/52631 | 기술 블로그 | 2026-09-08 (김경윤) |
| S5 | 토스 | 레고처럼 조립하는 토스 앱 (SLASH23) | https://toss.tech/article/slash23-iOS | 발표 정리 글 — **iOS 사례** | 2023-08-22 (이준석·송범근) |
| S6 | 토스 | 토스의 디바이스 팜 만들기 (Nebula) | https://toss.tech/article/51605 | 기술 블로그 | 2026-07-30 (차영훈) |
| S7 | 우아한형제들 | 클린 아키텍처와 함께하는 배민앱 (Android) | https://techblog.woowahan.com/2602/ | 기술 블로그 | 2019-01-17(페이지 표기) — **2022 이전** |
| S8 | 우아한형제들 | 구름톡 업데이트 48시간 전, 그 치열함에 대하여 | https://techblog.woowahan.com/7949/ | 기술 블로그(회고) | 2022-04-28 (채상아) |
| S9 | 우아한형제들 | 기술블로그 Android 태그 목록 | https://techblog.woowahan.com/tag/android/ | 색인 페이지 | 확인일 2026-09-09 (총 5건, 2022년 이후 1건뿐) |
| S10 | 카카오페이 | 내 주변 송금이 블루투스로 만들어졌다고? | https://tech.kakaopay.com/post/bluetooth-remittance/ | 기술 블로그 | 2023-10-13 (해로·냅스터) |
| S11 | 카카오뱅크 | REST API 데이터 모킹 도구 개발 이야기 (제우스) | https://tech.kakaobank.com/posts/2411-android-mocking-story/ | 기술 블로그 | 2024-11-07 (문종락/Groot) |
| S12 | LY(LINE) | 기술블로그 Android 태그 목록 | https://techblog.lycorp.co.jp/ko/tag/android | 색인 페이지 | 확인일 2026-09-09 (총 4건) |

> 조직별 3건 상한을 지켰다. LY는 태그 목록(S12)에 있는 「Recode 프로젝트」(2024-10)·「폰트 커스터마이징」(2024-03-12)도 읽었으나 상한 때문에 표에서 제외했다.

---

## 사례 표

| # | 조직 | 주제 | 핵심 주장 | 출처 |
|---|------|------|-----------|------|
| C1 | LY(LINE) | 상태 관리 | 상태 변경 지점을 ViewModel 한 곳으로 모으고 외부에는 `StateFlow`만 노출한다(`MutableStateFlow` 비공개). "상태 관리 지점을 일원화하기". | [S1] |
| C2 | LY(LINE) | Compose 컴포저블 설계 | "상태 호이스팅을 사용해 컴포저블을 스테이트리스하게 만들기" — 리프 컴포저블은 ViewModel을 모르고 `value` + `onValueChange`만 받는다. | [S1] |
| C3 | LY(LINE) | 프리뷰·리뷰 | "컴포저블에서 가능한 각 상태별로 미리 보기 그리기" + 라이트/다크(`uiMode = UI_MODE_NIGHT_YES`) 프리뷰. PR에 프리뷰 스크린샷을 첨부해 리뷰어가 코드를 읽지 않고 UI를 검증한다. | [S1] |
| C4 | LY(LINE) | Compose 도입 범위 | 기존 View를 리팩터링하지 않고 **신규 모듈에만** Compose를 쓴다. 안정성이 미검증인 컴포넌트(`TextField`)는 `AndroidView`로 `EditText`를 감싸 회피했다. | [S1] |
| C5 | LY(LINE) | 린트 대량 적용 | ktlint 0.5.0 → 1.0.1 상향으로 위반 28만 건 이상을 포매터로 일괄 수정(수작업 추정 2년 → 수 분). 최다 위반은 `standard:indent` 115,006건. | [S2] |
| C6 | LY(LINE) | 자동 변경 검증 | 포매팅 전후 AST(Kotlin PSI)를 비교해 "코드의 표현 방식만 변경된 것인지 확인할 수 있습니다". 공백·주석·`@Suppress`는 비교에서 제외. 실제로 ktlint가 기본 파라미터 값을 지우는 버그를 이 검증이 잡아냈다. | [S2] |
| C7 | LY(LINE) | AI 에이전트 도구 규약 | 수백 개 Gradle 모듈 모노레포에서 grep은 결과가 너무 많아 토큰을 낭비한다. Android CLI를 씬 래퍼로 감싸 에러를 1줄 파싱 가능 형식으로 정규화하고, 바이너리를 `.agents/tools/android-cli/`에 git-lfs로 동봉해 "환경 파편화 방지". | [S3] |
| C8 | LY(LINE) | 에이전트 폴백 규약 | `AGENTS.md`/`CLAUDE.md`에 실패 시 행동을 명문화한다. IDE 인덱스 검색이 실패하면 grep/glob으로 폴백하고, IDE 전용 기능은 추측하지 말고 에러를 보고한 뒤 대기한다. | [S3] |
| C9 | 토스 | 팀 규칙 집행 | 규칙을 초기 프롬프트에만 두면 "Lost in the Middle"로 묻힌다. Stylepack은 **파일 작성 직후**(규칙 최대 2개)와 **작업 종료 직전 `git diff` 검토**(최대 4개) 두 시점에 규칙을 주입한다. | [S4] |
| C10 | 토스 | 규칙 저장 위치 | 규칙은 개별 프로젝트 repo가 아니라 **중앙 저장소**에 두고, 트리거 조건(정규식·파일 확장자)과 주입 텍스트를 한 쌍으로 관리한다. 적용 지표를 추적해 과적합 규칙을 걸러낸다. | [S4] |
| C11 | 토스 | 모듈화 (iOS) | Microfeatures로 기능 하나를 **Feature/Interface/Testing/Tests/Example 5개 모듈**로 분할. "기존에 하나였던 Home 모듈을 이렇게 5개의 모듈로 나누게 되는 거예요". 같은 계층끼리 직접 의존하지 않고 Interface 모듈로만 접근해 순환 참조를 없앴다. 약 700 모듈·Swift 100만 줄 규모. | [S5] |
| C12 | 토스 | 빌드 시간 (iOS) | 기능별 Example 앱은 전체 앱 대비 약 **5배 빠르게** 빌드되어 UI 작업 생산성을 올린다. 모듈 생성은 Tuist Scaffold로 단일 명령화. | [S5] |
| C13 | 토스 | 테스트 인프라 | "실기기를 24시간 연결해두고, 전사가 API 호출 한 번으로 함께 쓰는 디바이스 팜". Appium 대신 자체 드라이버로 클릭 13.5배(52ms vs 702ms)·텍스트 입력 10배 개선, Android는 SurfaceControl+MediaCodec H.264 미러링, stateless 아키텍처. | [S6] |
| C14 | 우아한형제들 | 계층 구조 | Presentation / Domain / Data 3계층 수직 분리, 의존 방향은 Presentation → Domain → Data 단방향. 경계마다 Mapper로 Entity↔Model 변환. Presentation은 생성자 주입. | [S7] |
| C15 | 우아한형제들 | DataSource 구성 | Remote/Local을 나누지 않고 **단일 DataSource가 Remote/Cache를 선택**한다. UseCase는 `Flowable`을 반환. | [S7] |
| C16 | 우아한형제들 | 계층 분리의 효용 | Domain/Data를 재사용하고 Presentation만 다시 만들어 배민마켓을 2주 만에 출시했다. | [S7] |
| C17 | 우아한형제들 | 성능·회고 | 댓글을 컷 단위로 묶고 컷당 Observable 하나만 유지, 한 번에 한 개 뷰만 렌더링해 댓글 3,076개에서도 메모리 증가를 약 10%로 억제. 성공 요인으로 "회고"와 "믿을 수 있는 팀"을 꼽는다. | [S8] |
| C18 | 카카오페이 | 기기 편차 흡수 | 하드웨어 `isOffloadedScanBatchingSupported()`에 의존하지 않고 "모든 기기에서 Coroutine으로 Scan Result를 직접 Buffering하여 Batching 처리" — 앱 레이어에서 5초 주기를 균일화. | [S10] |
| C19 | 카카오페이 | 프로토콜 설계 | 16바이트 고정 패킷에 맞추려 UTF-8(한글 3바이트) 대신 EUC-KR(2바이트) 인코딩을 선택. 마이크로 최적화보다 예측 가능성을 택했다. | [S10] |
| C20 | 카카오뱅크 | 응답 모킹 계층 | 모킹을 **OkHttp Interceptor 계층**에 두어 ViewModel·Repository 등 상위 계층은 실제 서버 응답과 구분하지 못한다. 지연·에러·초장문 문자열 등 엣지 케이스를 UI 테스트 코드 없이 재현. | [S11] |
| C21 | 카카오뱅크 | 디버그 코드 격리 | 모킹 코드는 전용 `/debug` 소스셋에만 두어 릴리스 빌드에 절대 포함되지 않게 한다(공격자 악용 차단). | [S11] |

---

## 출처 간 충돌

| 주제 | 입장 A (국내 사례) | 입장 B (해외 공식 가이드) | 팩 추천과 근거 |
|------|-------------------|--------------------------|----------------|
| UI 패턴 | MVP(View + Presenter) [S7, C14] | Google 현행 앱 아키텍처 가이드는 MVVM/UDF와 상태 홀더를 권장 | **B**. S7은 2019년 글로 현행 권장과 시점이 다르다. 계층 분리·단방향 의존 원칙만 취하고 패턴은 UDF로 간다. |
| DataSource 구성 | 단일 DataSource가 Remote/Cache를 내부에서 선택 [S7, C15] | Repository가 Remote/Local DataSource를 각각 두고 조합 | **B**. 캐시 정책이 DataSource에 숨으면 테스트 대역 주입과 오프라인 정책 변경이 어렵다. A는 소규모에서만 허용. |
| 모듈 분할 입도 | 기능당 5모듈(Feature/Interface/Testing/Tests/Example) [S5, C11] | now-in-android은 기능당 `:feature:*` 1모듈 + `:core:*` | **B를 기본, A는 옵션**. 5분할은 700모듈·100만 줄 규모에서 나온 답이다. 그린필드는 1모듈로 시작하고, 기능 간 직접 의존이 생기는 시점에만 Interface 모듈을 떼어낸다. |
| Compose 도입 범위 | 신규 모듈에만 적용, 기존 View는 그대로 [S1, C4] | Google도 점진 도입을 권장하나 신규 화면은 Compose 우선 | **충돌 아님(정도 차이)**. 다만 S1이 `TextField`를 `AndroidView`+`EditText`로 대체한 것은 2023년 시점 판단이므로 그대로 규칙화하면 안 된다. 팩은 "우회는 재현 가능한 근거와 재검토 시점을 남길 때만 허용"으로 쓴다. |
| 비동기 타입 | MVVM + LiveData 조합을 2024년에도 채택 (LY Recode 프로젝트, https://techblog.lycorp.co.jp/ko/recode_project) | Kotlin/Compose 스택에서는 `StateFlow` 권장 | **B**. 단 같은 조직의 S1은 이미 `StateFlow`를 쓴다. 조직 내부에서도 갈리므로 팩은 `StateFlow` 단일화를 명시한다. |
| 규칙 집행 수단 | 훅으로 AI 에이전트에 규칙 주입 [S4] / 린터 + AST 동등성 검증 [S2] | 공식 가이드는 대체로 린터·CI 게이트만 언급 | **보완 관계**. 셋 다 채택하되 순서는 린터(기계 판정) → CI 게이트 → 에이전트 주입(판단 필요 규칙)으로 둔다. |

---

## 보강 제안 표

| # | 실천 | 보강 대상 R-파일번호 | 왜 |
|---|------|---------------------|-----|
| P1 | ViewModel만 상태를 바꾸고 외부에는 `StateFlow`만 노출, `MutableStateFlow`는 비공개 [C1] | **12 상태** | "상태 홀더를 쓴다"보다 강한 조문. 노출 타입까지 못박아야 리뷰에서 기계적으로 판정된다. |
| P2 | 리프 컴포저블은 상태 호이스팅으로 stateless, `value` + `onValueChange` 시그니처 고정 [C2] | **17 Compose**, 12 상태 | 컴포저블이 ViewModel을 직접 참조하지 못하게 막아 프리뷰·테스트·재사용이 동시에 열린다. |
| P3 | 컴포저블마다 상태별 `@Preview` + 라이트/다크 프리뷰를 의무화하고, PR에 프리뷰 스크린샷을 첨부 [C3] | **17 Compose**, **31 CI** | 리뷰어가 컴포저블 코드를 읽지 않고도 UI를 검증하는 구체적 리뷰 규약. 스크린샷 테스트 도입 전 단계로 비용이 싸다. |
| P4 | Compose는 신규 모듈/화면부터 적용하고, 우회(AndroidView 래핑)는 근거와 재검토 시점을 함께 기록 [C4] | **17 Compose**, 10 구조 | 국내에서 실제로 통한 도입 경로. 무기한 우회를 막는 조건을 붙여야 부채가 안 쌓인다. |
| P5 | 포매터/린터로 대량 자동 변경을 할 때는 **AST 동등성 검증을 통과 조건으로** 둔다 [C6] | **20 스타일**, **31 CI** | 실제로 포매터가 기본 파라미터를 지운 사고를 이 검증이 잡았다. "린터 켜라"에서 한 단계 나아간, 근거 있는 안전장치. |
| P6 | 린터·CLI 등 개발 도구는 버전을 고정하고 저장소에 동봉(git-lfs)해 로컬과 CI가 같은 바이너리를 쓴다 [C7] | **31 CI**, 20 스타일 | 도구 버전이 갈리면 포맷 diff가 리뷰를 오염시킨다. 환경 파편화를 구조적으로 차단. |
| P7 | 코딩 에이전트용 규칙은 중앙 저장소에 두고, 주입 시점(파일 작성 직후·`git diff` 검토 시)과 **동시 주입 규칙 수 상한**을 정한다 [C9, C10] | **20 스타일**, **31 CI** | 그린필드 팩이 AI 에이전트와 함께 쓰일 것을 전제하면, 규칙 문서를 "언제 어떻게 다시 보여줄지"까지 정의해야 실효가 생긴다. |
| P8 | 기능 모듈은 1모듈로 시작하고, 기능 간 참조가 생기면 그때 Interface 모듈만 분리한다(Testing/Example 분리는 규모 임계 이후) [C11, C12] | **10 구조** | 토스 5분할을 그린필드에 그대로 적용하면 과설계다. 승격 조건을 붙인 형태로 담아야 한다. |
| P9 | 계층은 Presentation→Domain→Data 단방향, 경계마다 Mapper로 모델을 갈아끼운다 [C14] | **11 계층**, **15 데이터**, **16 도메인** | 국내 대규모 앱에서 계층 재사용으로 신규 서비스를 2주에 출시한 실측 근거 [C16]. 단방향 규칙의 효용을 사례로 뒷받침한다. |
| P10 | 모킹·디버그 전용 코드는 `debug` 소스셋에만 두고 릴리스 빌드 포함을 금지 [C21] | **40 성능보안**, 30 테스트 | 편의 기능이 릴리스에 새어 들어가는 사고를 소스셋 분리로 구조적으로 막는다. |
| P11 | 응답 모킹은 네트워크 Interceptor 계층에 두어 상위 계층 코드를 바꾸지 않는다 [C20] | **15 데이터**, **30 테스트** | 엣지 케이스(지연·에러·초장문) 검증을 UI 테스트 없이 수동으로도 돌릴 수 있다. QA·기획도 쓸 수 있는 게 실제 효용. |
| P12 | E2E는 실기기 팜에서 돌리고, 팜은 API로 공용화한다 [C13] | **30 테스트**, **31 CI** | 팀별 기기 관리는 중복이다. 그린필드 팩에서는 "에뮬레이터 한계와 실기기 필요 시점"을 명시하는 근거가 된다. |
| P13 | 기기별 하드웨어 편차는 앱 레이어에서 흡수한다(예: 코루틴 버퍼링으로 배칭 주기 균일화) [C18] | **22 코루틴**, 40 성능보안 | 벤더 플래그 분기 대신 앱에서 일관된 동작을 만드는 편이 예측 가능하고 테스트가 쉽다. |

---

## 미확인·접근 불가

| 대상 | URL | 상태 |
|------|-----|------|
| 당근 테크 블로그 Android 태그 | https://medium.com/daangn/tagged/android | **HTTP 403** — Medium이 자동 조회를 차단. 목록조차 못 읽음 |
| 당근 「App Bundle의 Dynamic Delivery로 국가별 모듈 제공하기」 | https://medium.com/daangn/app-bundle의-dynamic-delivery로-국가별-모듈-제공하기-ee699c561707 | **HTTP 403** — 제목만 검색으로 확인. 내용 미확인 |
| 당근 「아키텍처에 대한 고민은 처음이라」 | https://medium.com/daangn/아키텍처에-대한-고민은-처음이라-b75dffd73eb0 | **HTTP 403** — Android/iOS 여부도 미확인 |
| 당근 채용/기술 블로그 | https://careers.daangn.com/blog/ | 접근됨. **Android·Kotlin·Compose 글 없음** |
| 네이버 D2 | https://d2.naver.com/helloworld | **도구가 이 도메인 조회를 거부**(unable to fetch). 검색으로도 Android 글이 잡히지 않음 |
| 카카오 기술블로그 | https://tech.kakao.com/blog/ , https://tech.kakao.com/tag/android/ | 접근됨. **"등록된 글이 없습니다"** — Android 태그 비어 있음 |
| 카카오 「2023/2022 신입 공채 Android 온보딩 회고」 | https://tech.kakao.com/2023/03/28/2023-new-krew-onboarding-android/ , https://tech.kakao.com/2022/03/15/2022-newkrew-onboarding-android/ | 페이지는 열리나 **본문이 렌더링되지 않음**(헤더·푸터만). 내용 미확인 |
| 토스 SLASH24 「Compose를 토스팀에 안착시키기까지」 (최동근) | https://toss.im/slash-24/sessions/19 | 세션 소개문만 확인("Compose 도입 배경", 토스 디자인시스템 적용). **구체 규칙 없음**, 발표 영상 미확인이라 사례 표에서 제외 |
| WOOWACON 2024 세션 목록 | https://techblog.woowahan.com/20789/ | 접근됨. 트랙 목록에 **Android/모바일 세션 없음** |
| 우아한형제들 Android 태그 | https://techblog.woowahan.com/tag/android/ | 총 5건 중 **2022년 이후는 1건뿐**(2022-04-28). 최신 Android 표준 자료 없음 |
| S7 발행일 | https://techblog.woowahan.com/2602/ | 페이지 표기는 2019-01-17이나 일부 검색 스니펫은 2021-08로 표시. **날짜 확정 못 함** |
| S3 발행일 | https://techblog.lycorp.co.jp/ko/android-cli-for-ai-agents-at-scale | 본문에 발행일 표기 없음. Android Studio 2026.1.1 언급으로 2026년 추정 |
