# Android 표준 팩 설계 (android/ 도메인 팩)

> 작성: 2026-09-09. 상태: 사용자 승인(채팅) 완료. 이 문서는 구현 계획의 입력이다.

## 1. 목적

Android 그린필드 프로젝트에서 AI 에이전트가 받는 작업 요청("페이지 만들어줘", "이 함수 짜줘", "API 붙여줘")이
**항상 같은 표준을 거쳐** 나오게 한다. 표준은 국내외 대기업·공식 가이드의 공개 사례를 조사해 근거와 함께 정립하고,
문서(규칙) + 템플릿(골격) + 강제 장치(lint·아키텍처 테스트·CI)로 구성한다.

### 성공 기준

1. 스크래치 그린필드 프로젝트에서 "로그인 페이지 만들어줘"를 요청하면 `android-standards` 스킬이 자동 트리거되고,
   산출물이 체크리스트 6항목(모듈 위치·Nav·상태 아키텍처 선택 근거·DI·테스트·CI)을 전부 채운다.
2. `templates/` 코드가 컴파일되고, `enforcement/`의 Konsist 아키텍처 테스트·detekt·ktlint가 0 이슈로 통과한다.
3. 모든 규칙 항목에 출처 링크가 있다. `SKILL.md` ≤ 150줄, `references/*.md` 각 ≤ 300줄.
4. `eval/scenarios.md`의 시나리오 3개가 루브릭을 통과한다.

### 범위 밖 (하지 않음)

- 레거시(Java/XML/MVC) 어댑터 규칙. 그린필드 전용이다. infoCar 같은 기존 프로젝트에서는 신규 Kotlin·Compose 코드에만
  적용하고, 충돌 시 프로젝트 지침(CLAUDE.md/AGENTS.md)이 우선한다는 한 줄만 SKILL.md에 둔다.
- 빌드 가능한 레퍼런스 샘플 앱 동봉. 검증용 스크래치 프로젝트는 작업 중에만 쓰고 레포에 넣지 않는다.
- Claude Code 플러그인 패키징. 심볼릭 링크 설치로 충분하다.
- iOS/백엔드 팩. 단, 팩 구조는 `<domain>/` 하위 트리로 추가 가능하게 둔다.

## 2. 확정 결정 (2026-09-09 사용자 확정)

| 항목 | 결정 |
|------|------|
| 레포 구성 | AI-Agent 레포에 `android/` 도메인 팩으로 추가. 기존 범용 하네스(AGENTS.md·WORKLOG·bootstrap·docs·templates)는 수정 최소화 |
| 소비 방식 | Claude Code 스킬(`~/.claude/skills/android-standards` → `android/` 심볼릭 링크) + `bootstrap.sh --pack android`로 AGENTS.md에 참조 섹션 주입(Codex/Cursor 호환) |
| 적용 대상 | 신규 그린필드만 (Kotlin·Compose·Navigation 3·Hilt·멀티모듈) |
| 산출물 깊이 | 문서 + 코드 템플릿 + 강제 장치. 레퍼런스 앱 없음 |
| 언어 | 문서 한국어, 코드·식별자 영어 |

### Phase 1에서 사용자에게 물을 미확정 항목

리서치 결과와 추천안을 붙여 한 번에 묻는다. 임의 결정 금지.

- MVI 구현체: Orbit / 순수 구현(sealed Intent + reduce) / Circuit
- 스크린샷 테스트: Roborazzi / Paparazzi
- 에러 타입: `kotlin.Result` / 커스텀 sealed `Result<T, E>` / Arrow `Either`
- UseCase: 항상 생성 / 로직이 있을 때만 생성(ViewModel→Repository 직접 호출 허용)
- 포맷터: ktlint 단독 / detekt-formatting 통합
- 그 외 리서치 중 출처 간 충돌이 드러난 항목

> **정정(2026-09-09 16:40, Ruling 12)**: 리서치 결과 Google 아키텍처 가이드·Now in Android는 의존 방향 **UI → Domain → Data**(데이터 계층은 다른 계층에 의존하지 않음), Repository 인터페이스·구현 모두 data 계층, 모델은 `model`(`:core:model`)에 둔다. 아래 §3·§7의 "인터페이스는 domain, 구현은 data"·"data가 ui 참조 금지" 표현은 이 정정으로 대체한다. 근거: `android/references/90-sources.md` 결정 표(LAYER_DIRECTION·REPO_INTERFACE_LOCATION·MODEL_LOCATION)와 `android/README.md` 확정 결정 요약.

## 3. 구조

```
android/
├── SKILL.md                 라우터. 요청 분류 → checklists/ 선택 → 필요한 references/만 로드. ≤150줄
├── README.md                팩 개요·설치(심볼릭 링크·bootstrap)·사용법·구조
├── references/              규칙 정본. 주제별 1파일, 각 ≤300줄
│   ├── 00-principles.md             설계 철학·규칙 우선순위·결정 원칙(UDF·불변성·명시적 의존)
│   ├── 10-project-structure.md      멀티모듈(:app, :core:*, :feature:*), build-logic convention plugin, version catalog
│   ├── 11-architecture-layers.md    UI/Domain/Data 계층 책임·의존 방향·계층별 금지 사항
│   ├── 12-state-management.md       UiState·UDF, MVVM-UDF vs MVI 결정 매트릭스, one-shot 이벤트 처리, 상태 홀더
│   ├── 13-navigation.md             Navigation 3: 타입세이프 키, 백스택, 결과 반환, 딥링크, 모듈 간 네비 경계
│   ├── 14-di.md                     Hilt: 생성자 주입만, 모듈 위치, 스코프, 인터페이스 바인딩, 테스트 교체
│   ├── 15-data-layer.md             Repository/DataSource, Room·DataStore·Retrofit, 오프라인 우선, 매핑(DTO↔Entity↔Model)
│   ├── 16-domain-layer.md           UseCase 규칙(operator invoke, 단일 책임), 만들 때·안 만들 때
│   ├── 17-compose-ui.md             상태 호이스팅, 안정성·재구성, Preview, 슬롯 API, 디자인 시스템 모듈, 접근성 최소 규칙
│   ├── 20-kotlin-style.md           네이밍·파일 구성·가시성·불변성·null 처리(!! 금지 등)·표현식 본문
│   ├── 21-functions.md              함수 설계: 크기·파라미터 수·순수함수·고차함수·컬렉션 체이닝 한계·Sequence 기준·
│   │                                스코프 함수 결정표·가드절·when 완전성·확장함수 배치·inline/reified
│   ├── 22-coroutines-flow.md        디스패처 주입, 구조적 동시성, stateIn/shareIn, 취소·예외, 금지 패턴(GlobalScope 등)
│   ├── 23-error-handling.md         에러 모델(Phase 1 결정 반영), 예외 경계, 사용자 메시지 매핑, 로깅
│   ├── 30-testing.md                테스트 피라미드, fake>mock, Turbine, 스크린샷 테스트, ViewModel/Repository 테스트 골격, 커버리지 기준
│   ├── 31-ci-cd.md                  GitHub Actions 파이프라인(lint→detekt→konsist→unit→screenshot→build), 캐시, 브랜치 보호, 릴리즈
│   ├── 32-code-review.md            리뷰 체크리스트, PR 템플릿, "표준 준수 보고" 형식
│   ├── 40-performance-security.md   시작 시간·재구성·메모리·Baseline Profile·R8, 시크릿·네트워크·저장·권한 최소 규칙
│   └── 90-sources.md                출처 목록(URL·조사일)과 채택/기각/보류 결정 표
├── checklists/              요청 유형별 파이프라인. SKILL.md가 라우팅
│   ├── new-screen.md                화면 추가
│   ├── new-feature-module.md        feature 모듈 추가
│   ├── new-data-source.md           API/DB/DataStore 추가
│   ├── function.md                  함수·유틸 작성
│   ├── bugfix.md                    재현 테스트 → 수정
│   ├── refactor.md                  동작 보존 검증 → 변경
│   └── review.md                    산출물 검증 게이트(표준 준수 보고 생성)
├── templates/               복사해서 쓰는 코드 골격. 플레이스홀더 {{Feature}} {{feature}} {{package}}
│   ├── module/                      feature 모듈: build.gradle.kts, 패키지 트리, di/
│   ├── ui/                          {{Feature}}Route.kt, {{Feature}}UiState.kt, {{Feature}}ViewModel.kt, {{Feature}}Screen.kt
│   ├── domain/                      {{Verb}}{{Noun}}UseCase.kt
│   ├── data/                        {{Feature}}Repository.kt, Default{{Feature}}Repository.kt, Remote/LocalDataSource
│   ├── di/                          {{Feature}}Module.kt
│   └── test/                        {{Feature}}ViewModelTest.kt, Fake{{Feature}}Repository.kt, {{Feature}}ScreenshotTest.kt
├── enforcement/             강제 장치. 프로젝트에 복사해 그대로 동작
│   ├── build-logic/                 convention plugin 스켈레톤(android-application, android-library, android-feature, compose, hilt, test)
│   ├── config/detekt/detekt.yml
│   ├── .editorconfig                ktlint 규칙
│   ├── konsist/ArchitectureTest.kt  계층 의존 방향, 네이밍(UseCase/Repository/ViewModel 접미사), 애노테이션, 패키지 위치
│   └── .github/workflows/android-ci.yml
├── eval/                    하네스 자체 검증
│   ├── scenarios.md                 시나리오 3개(로그인 화면 / 즐겨찾기 API 추가 / 리스트 필터 함수) + 기대 체크포인트
│   └── rubric.md                    통과 기준
└── research/                Phase 0 산출물. 주제별 조사 노트(URL·조사일·핵심 규칙·출처 간 충돌). 90-sources.md의 원재료
```

## 4. SKILL.md 계약

frontmatter `name: android-standards`, `description`은 트리거 조건을 담는다: Android 앱 개발에서 화면·기능 모듈·데이터 소스·
함수 작성, 아키텍처·상태 관리·네비게이션·DI·테스트·CI 결정이 필요할 때. 본문은 다음 순서를 강제한다.

1. **분류**: 요청을 checklists/ 7유형 중 하나로 분류하고 분류 결과를 사용자에게 한 줄로 알린다. 애매하면 질문.
2. **컨텍스트 수집**: 프로젝트의 모듈 구조, Nav 그래프, 디자인 시스템 유무, 유사 화면·패턴, 프로젝트 지침 파일을 먼저 읽는다.
3. **결정 명시**: 체크리스트가 요구하는 결정 항목을 references/의 규칙으로 정하고 근거를 적는다. 사용자만 정할 수 있는 항목은 질문.
4. **구현**: 체크리스트 순서대로. 템플릿을 복사해 시작한다.
5. **표준 준수 보고**: 산출물 끝에 표를 붙인다. 결정·근거·통과한 테스트·CI 영향·의도적으로 어긴 규칙과 사유.

**점진 로드 규칙**: SKILL.md는 references/를 인라인하지 않는다. 체크리스트마다 "읽을 references" 목록을 두고 그것만 읽는다.
**공식 스킬 연계**: 구현 방법은 로컬 공식 Android 스킬(navigation-3, testing-setup, adaptive, edge-to-edge 등)에 위임하고,
이 팩은 "무엇을 어떻게 결정하는가"만 담는다. 중복 서술 금지.
**우선순위**: 프로젝트 지침 파일 > 이 팩 > 기본 동작.

## 5. 문서 포맷 (references/ 공통)

각 규칙 항목은 다음 5요소를 갖는다. 누락 시 미완성으로 본다.

```
### R-12-03 상태는 단일 UiState data class로 노출한다
- 규칙: ...
- 근거: ... (출처: [Google 아키텍처 가이드](URL), [NiA](URL))
- 예시: Good / Bad 코드 (Kotlin, 10줄 내외)
- 체크: 리뷰 시 확인 문장 1~2개
```

ID 규칙 `R-<파일번호>-<순번>`. checklists/와 Konsist 테스트가 이 ID를 참조한다.
결정이 필요한 주제(12, 16, 23 등)는 **결정 매트릭스** 표를 둔다. 열: 판단 기준 / 선택지 A / 선택지 B / 기본값.

## 6. 체크리스트 계약 (checklists/ 공통)

```
# <유형>
읽을 references: [...]
함께 로드할 공식 스킬: [...]
## 결정 항목 (구현 전 전부 명시)
| # | 결정 | 규칙 | 애매하면 질문할 것 |
## 구현 순서
1. ... → 검증: ...
## 산출물 검증 (review.md 호출)
```

new-screen의 결정 항목 6개: 모듈 위치, 진입·이탈·결과 반환 경로, 상태 아키텍처(결정 매트릭스), UseCase 필요 여부,
테스트 범위, CI 영향. 구현 순서: Route → UiState/Event → ViewModel → UseCase/Repository → Screen+Preview → DI → 테스트.

## 7. 강제 장치

- **Konsist**: 계층 의존 방향(ui→domain→data 단방향, data가 ui 참조 금지), 접미사 규칙, `@HiltViewModel`+`@Inject constructor`,
  UseCase는 public 함수 1개(`operator fun invoke`), Repository 인터페이스는 domain, 구현은 data. 테스트 이름에 R-ID를 넣는다.
- **detekt**: 복잡도(함수 길이·파라미터 수·중첩 깊이), `!!` 금지, GlobalScope 금지, 와일드카드 import 금지 등. Phase 1 결정에 따라 formatting 포함 여부.
- **ktlint**: 공식 Android 스타일 프리셋. `.editorconfig`.
- **CI**: PR마다 lint→detekt→konsist→unit→screenshot→assembleDebug. Gradle 캐시. main 브랜치 보호 규칙은 문서로만.
- **convention plugin**: 모듈 추가 시 설정 중복 제거. NiA build-logic 패턴 기준.

## 8. bootstrap.sh 확장

- `./bootstrap.sh <target> [name] --pack android`: `android/`를 `<target>/harness/android/`에 복사(존재 시 skip),
  AGENTS.md 끝에 "## Android 표준" 섹션 추가(이미 있으면 skip). 섹션 내용: `harness/android/SKILL.md`를 먼저 읽고 따르라는 지시.
- 기존 동작(팩 미지정)은 변경 없음. 기존 Codex 리뷰에서 지적된 원자적 쓰기·특수문자 안전 규칙을 유지한다.
- Claude Code 전역 설치는 README에 명령 한 줄: `ln -s <repo>/android ~/.claude/skills/android-standards`
  (Windows: `mklink /D` 또는 Git Bash `ln -s`).

## 9. 리서치 (Phase 0) 설계

병렬 에이전트 5개. 각자 WebSearch/WebFetch로 **현재 문서**를 읽고 `android/research/<topic>.md`에 기록한다.
기록 항목: 출처 URL·조사일·핵심 규칙(원문 요지)·출처 간 충돌·이 팩에 대한 추천. 학습 데이터 기억으로 쓰지 않는다.

| 에이전트 | 주제 | 주요 출처 |
|---|---|---|
| A 아키텍처·모듈 | 계층, 모듈화, convention plugin, DI | Google 아키텍처 가이드·권장사항, NiA, 모듈화 가이드, Uber RIBs, Dropbox/MNF, Reddit·Netflix 블로그 |
| B 상태·네비 | UDF, MVI 라이브러리 비교, Nav 3 | Google UI 계층·상태 홀더, Orbit, Airbnb Mavericks, Slack Circuit, Spotify Mobius, Bumble MVICore·Appyx, Freeletics FlowRedux, Tinder StateMachine, Nav 3 문서 |
| C Kotlin 스타일·함수·코루틴 | 네이밍, 함수 설계, 체이닝, 스코프 함수, 코루틴·Flow | Google Kotlin 스타일 가이드, JetBrains 코딩 컨벤션, Kodeco 스타일, Google 코루틴 모범 사례, Compose API 가이드라인 |
| D 테스트·CI·강제 | 테스트 전략, 스크린샷, Konsist, detekt, ktlint, GitHub Actions | Google 테스트 가이드, NiA 테스트, Turbine, Paparazzi, Roborazzi, Konsist, detekt, Pinterest ktlint, GitHub Actions Android 예시 |
| E 국내 사례 | 토스·당근·우아한형제들·카카오·네이버·라인 공개 자료 | 각사 기술 블로그·SLASH·DEVIEW·if(kakao) 발표. 문서화 수준을 명시하고 사례로만 반영 |

## 10. 검증 계획

- Phase 2: 각 문서 줄 수·5요소 포맷·링크 유효성 스크립트 점검. 규칙 ID 중복 없음.
- Phase 3: 스크래치 프로젝트(`android` CLI 또는 최소 Gradle 템플릿)를 스크래치패드에 생성해 templates+enforcement를 넣고
  `assembleDebug`, `detekt`, `ktlintCheck`, Konsist 테스트 1회 실행. 레포에는 넣지 않는다.
- Phase 4: 심볼릭 링크 후 스크래치 프로젝트에서 eval 시나리오 3개를 실제로 요청해 루브릭 대조.
- Phase 5: Codex(gpt-6-astra, effort high) 교차 리뷰 → 반영 → 사용자 확인 후 커밋·푸시.

## 11. 제약

- 커밋·푸시는 사용자 명시 지시 후에만. 커밋 제목은 `M/D ` 접두사.
- 기존 킷 파일 수정은 bootstrap.sh와 README.md 구조 표 갱신에 한정한다.
- 규칙은 출처 없이 쓰지 않는다. 출처가 없는 "관행"은 `90-sources.md`에 보류로 표시하고 규칙 본문에 넣지 않는다.
