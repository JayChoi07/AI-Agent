# android — Android 그린필드 표준 팩

Kotlin·Compose·Navigation 3·Hilt·멀티모듈 기준의 Android 개발 표준을 **규칙 196개**(출처 125건) · **유형별 체크리스트 7개** · **코드 템플릿 22개**(치환 안내 포함 23파일) · **빌드 강제 장치** · **라우터 스킬**로 묶은 팩이다.
스킬 이름은 `android-standards`이고, 진입점은 [`SKILL.md`](SKILL.md)다. 요청 유형을 먼저 분류한 뒤 그 유형의 체크리스트만 따라가는 라우터 구조라, [`references/`](references) 18개 파일을 전부 읽지 않는다.
대상은 **그린필드**(신규 Kotlin·Compose 프로젝트)이며, 레거시 프로젝트에서는 신규 코드에만 적용한다.
모든 규칙에는 `R-NN-MM` ID와 [`references/90-sources.md`](references/90-sources.md)의 출처 번호(S)가 붙는다. 출처 없는 규칙은 쓰지 않는다.

## 케이스별 사용법

### A. Claude Code에서 그냥 요청하기

**언제** — 팩을 전역 스킬로 걸어 뒀거나 프로젝트에 주입해 둔 상태에서 Android 작업을 시킬 때.
**하는 일** — 평소처럼 요청한다. 예: "로그인 페이지 만들어줘. 이메일·비밀번호 입력, 성공하면 홈으로."
**무엇이 나오는지** — 에이전트가 유형을 분류해 `유형: new-screen` 한 줄을 알리고, 체크리스트의 **결정 항목** 표(모듈 위치 · 진입/이탈·결과 반환 · 상태 아키텍처 매트릭스 · UseCase 필요 여부 · 테스트 범위 · CI 영향)를 R-ID 근거와 함께 전부 채운다. 이어 `templates/`를 치환해 구현하고 `ktlintCheck` → `detektDebug` → `testDebugUnitTest verifyRoborazziDebug` → `assembleDebug` 순으로 검증한 뒤, 응답 끝에 **표준 준수 보고** 표를 붙인다. 최종 응답 순서는 유형 → 결정 표 → 구현 → 검증 → 보고로 고정이라, 결정 표가 구현 뒤로 밀리면 미준수다.
규칙으로 정해지지 않고 사용자만 정할 수 있는 항목(기존 feature에 넣을지, 어디서 진입하는지 등)은 임의로 결정하지 않고 묻는다.

| 유형 | 트리거 예 | 체크리스트 |
|---|---|---|
| new-screen | 페이지/화면 만들어줘, 상세 화면 추가 | [checklists/new-screen.md](checklists/new-screen.md) |
| new-feature-module | 새 기능 모듈, `:feature:*` 추가 | [checklists/new-feature-module.md](checklists/new-feature-module.md) |
| new-data-source | API 붙여줘, DB 테이블 추가, DataStore | [checklists/new-data-source.md](checklists/new-data-source.md) |
| function | 이 함수 짜줘, 유틸·확장함수, 파싱 로직 | [checklists/function.md](checklists/function.md) |
| bugfix | 버그 고쳐, 크래시, 이상 동작 | [checklists/bugfix.md](checklists/bugfix.md) |
| refactor | 리팩터링, 정리, 구조 변경(동작 동일) | [checklists/refactor.md](checklists/refactor.md) |
| review | 리뷰해줘, 표준 준수 확인 | [checklists/review.md](checklists/review.md) |

**참고** — [`SKILL.md`](SKILL.md)(절차·최종 응답 구조), [`checklists/new-screen.md`](checklists/new-screen.md)(요청 하나가 무엇을 만드는지 가장 자세한 예).

### B. 새 Android 프로젝트 세팅

**언제** — 빈 프로젝트에서 시작해 규칙을 빌드로 강제하고 싶을 때.
**하는 일** — `android create --name <이름> -o <경로> empty-activity`(또는 Android Studio)로 골격을 만든 뒤 [`enforcement/README.md`](enforcement/README.md) "설치 순서"를 따른다. 8단계 요약:

1. `build-logic/`을 프로젝트 루트에 복사
2. 루트 `settings.gradle.kts`에 `includeBuild("build-logic")` + `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`
3. `build-logic/libs.versions.toml.snippet`을 `gradle/libs.versions.toml`에 병합(별칭은 그대로 둔다)
4. 루트 `build.gradle.kts`에 컨벤션 플러그인이 적용하는 플러그인을 전부 `apply false`로 선언
5. `config/detekt/detekt.yml`·`.editorconfig` 배치
6. `konsist/ArchitectureTest.kt`를 `:app`의 테스트 소스로 복사하고 패키지 선언 교체
7. `.github/workflows/android-ci.yml` 복사
8. `./gradlew recordRoborazziDebug`로 만든 골든 png를 `<모듈>/src/test/screenshots/`에 커밋

**무엇이 나오는지** — 계층 의존·필드 주입·`!!`·줄 길이 같은 규칙이 Konsist·detekt·ktlint로 검사되고, CI가
ktlintCheck → detektDebug → 테스트 → assembleDebug 순서로 게이트를 돈다.
**필요 환경** — JDK 17 · AGP 9.4.0 · Gradle 9.7.1 · compileSdk 37.
이 단계도 손으로 할 필요는 없다. 에이전트에게 "이 프로젝트에 android-standards 강제장치 세팅해줘"라고 시키면 된다.

### C. 팀·다른 도구(Codex, Cursor)와 공유

**언제** — 팀 저장소에 규칙 사본을 넣어 도구와 무관하게 같은 표준을 쓰게 할 때.
**하는 일** — `./bootstrap.sh /path/to/project --pack android`
**무엇이 나오는지** — 팩이 `<프로젝트>/harness/android/`로 복사되고(조사 노트 `research/`는 제외), `AGENTS.md`에
"## Android 표준" 섹션이 추가돼 Android 작업 전에 `harness/android/SKILL.md`를 읽도록 지시한다. `.gitignore`에는
`.gradle/`·`.kotlin/`·`.idea/`·`local.properties` 같은 Android 항목이 없을 때만 덧붙는다.
멱등이라 다시 실행해도 기존 파일을 덮어쓰지 않고, 이전 실행이 끊겨 반쪽만 남은 `harness/android`는 에러로 잡아 다시 설치하게 한다.

### D. 기존 앱에 적용

**언제** — 레거시 코드가 있는 앱에서 신규 코드만 표준에 맞추고 싶을 때.
**하는 일** — 팩을 그대로 쓰되 **신규 Kotlin·Compose 코드에만** 적용한다.
**무엇이 나오는지** — 그린필드 규칙이 프로젝트 사정과 충돌하면 프로젝트 지침 파일(`CLAUDE.md`/`AGENTS.md`)이
우선이고, 에이전트는 그 사실을 표준 준수 보고에 적는다. 레거시용 어댑터를 즉흥으로 만들지 않는다.

### E. 템플릿만 직접 쓰기

**언제** — 에이전트 없이 손으로 feature 골격을 만들 때.
**하는 일** — [`templates/README.md`](templates/README.md)의 치환 스크립트를 레포 루트에서 돌린다. `PACK_ROOT`(읽은 `SKILL.md`가 있는 디렉터리)를 정하고 `NAME`·`LOWER`·`PKG` 세 값만 바꾸면 되며, 공용 파일 2개(`Dispatchers.kt`·`MainDispatcherRule.kt`)는 대상이 이미 있으면 건너뛴다. 치환 후 `./gradlew ktlintFormat`을 한 번 돌린다.

### F. 규칙 고치기·추가하기

1. **출처 확보** — 공식 문서·릴리즈 노트 등 인용 가능한 URL을 찾는다. 출처가 침묵하는 관행은 규칙으로 만들지 않는다. 도구 기본값을 쓸 때는 근거 줄에 "출처는 도구 기본값"이라고 명시한다.
2. **`research/`에 기록** — 해당 주제 노트에 문서명·URL·확인 내용을 추가한다. 새 주제면 노트를 추가한다.
3. **`references/90-sources.md`에 등록** — 전역 번호 `S###` 행을 표 맨 아래에 추가한다. 기존 번호는 재배열하지 않는다.
4. **`references/NN-*.md`에 규칙 작성** — `### R-NN-MM` + `- 규칙:` `- 근거:`(S번호 + URL) `- 예시:`(Good/Bad) `- 체크:` 5요소를 채운다. R-ID는 파일 번호와 일치하고 전역 유일해야 한다. 파일당 300줄 이하.
5. **연동 갱신** — 규칙이 결정·검증에 걸리면 해당 `checklists/`, `templates/`, `enforcement/`(detekt.yml·Konsist)도 같이 고친다.
6. **검사** — `bash android/scripts/check-pack.sh`가 통과해야 한다. 줄 수 한도, 5요소, 근거 URL, R-ID 중복, 체크리스트 필수 헤더, 그리고 SKILL·checklists·templates·enforcement·README 가 인용한 R-ID 가 `references/`에 실제로 있는지를 검사한다.

### G. 팩이 잘 동작하는지 확인

**하는 일** — [`eval/scenarios.md`](eval/scenarios.md)의 세 요청(로그인 화면 추가 · 즐겨찾기 API 연동 · 일자별 거리 합계 함수)을 스크래치 프로젝트에서 그대로 던지고 [`eval/rubric.md`](eval/rubric.md)로 채점한다. 시나리오당 10점이고 8점 이상이 통과다.
**최근 실행 기록(2026-09-10)** — S1 8/10 통과, S2 9/10 통과, S3는 6/10 실패였다가 응답 순서 고정·R-ID 제목 병기를 반영한 뒤 재실행에서 10/10 통과했다.

## 설치

```bash
# Claude Code 전역 설치 — macOS·Linux
mkdir -p ~/.claude/skills          # 처음이면 이 디렉터리가 없어 ln -s 가 실패한다
ln -s "$(pwd)/android" ~/.claude/skills/android-standards
# 프로젝트에 주입 (Codex/Cursor 포함) — 모든 OS 공통
./bootstrap.sh /path/to/project --pack android
```

`bootstrap.sh` 는 bash 4 이상이 필요하다(배열 확장 `${arr[@]+…}`). Windows에서는 **Git Bash**에서 실행한다 — PowerShell·cmd 에서는 돌지 않는다. macOS 기본 bash 3.2 대신 `brew install bash` 로 올린 bash 를 쓴다.

Windows에서는 심볼릭 링크 대신 **디렉터리 정션**을 만든다. `<repo>`는 이 저장소의 절대 경로다.

```powershell
# PowerShell (권장) — 관리자 권한 불필요
New-Item -ItemType Directory -Force "$env:USERPROFILE\.claude\skills" | Out-Null   # 없으면 만든다
New-Item -ItemType Junction -Path "$env:USERPROFILE\.claude\skills\android-standards" -Target "<repo>\android"
```

```bat
:: cmd 대안
cmd /c mklink /J "%USERPROFILE%\.claude\skills\android-standards" "<repo>\android"
```

**주의: Windows의 Git Bash에서 `ln -s`를 쓰면 링크가 아니라 복사본이 만들어진다**(MSYS 기본 동작). 그러면 팩을 고쳐도 설치본에 반영되지 않는다. 위 정션 명령을 쓰고, 이미 `ln -s`로 만든 디렉터리가 있으면 지운 뒤 다시 만든다.

전역 설치는 링크·정션이라 팩을 갱신하면 즉시 반영된다. 프로젝트 주입은 팀 저장소에 팩 사본을 넣어 도구와 무관하게 같은 규칙을 쓰게 한다.

## 구조

```
android/
├── SKILL.md          # 라우터 — 유형 분류 → 체크리스트, 절차·보고 형식 (≤150줄)
├── references/       # 규칙 18파일. 00 원칙 / 10~17 구조·아키텍처 / 20~23 Kotlin·비동기·에러
│                     #             / 30~32 테스트·CI·리뷰 / 40 성능·보안 / 90 출처(S01~S125)
├── checklists/       # 유형별 파이프라인 7파일. 컨텍스트 수집 → 결정 항목 → 구현 순서 → 산출물 검증
├── templates/        # 치환형 코드 골격. ui/ · ui/mvi/ · domain/ · data/ · model/ · di/ · test/ · module/
│                     #   치환 규칙은 templates/README.md
├── enforcement/      # 강제 장치. detekt.yml · .editorconfig · build-logic 컨벤션 플러그인
│                     #   · konsist/ArchitectureTest.kt · .github/workflows/android-ci.yml
├── eval/             # 팩 자체 검증 시나리오
├── research/         # 출처 조사 노트 5건 (architecture · state-nav · kotlin-style · testing-ci · korea)
└── scripts/
    └── check-pack.sh # 형식 검사 — 줄 수 한도, 규칙 5요소, 근거 URL, R-ID 유일성, 체크리스트 헤더,
                      #             references 밖에서 인용한 R-ID 의 실존 여부
```

## 확정 결정 요약

규칙 문서가 전제로 삼는 결정이다. 팀이 다르게 정하면 프로젝트 지침 파일이 우선이며, 그 사실을 표준 준수 보고에 적는다.

| 키 | 확정 |
|---|---|
| 계층 의존 방향 | UI → Domain → Data 단방향. UI → Data 직접 의존 허용(domain은 선택). Repository 인터페이스·구현 모두 data 계층, 모델·에러 타입은 `model` 패키지. 패키지는 `{ui, domain, data, model, di}` 5종 |
| MVI_IMPL | 순수 구현. 기본은 ViewModel + private `MutableStateFlow` / public `StateFlow` + 단일 UiState. 매트릭스가 MVI를 요구하면 `sealed interface Intent` + private `reduce` + `Channel<SideEffect>`. MVI 라이브러리 미채택 |
| SCREENSHOT_LIB | Roborazzi 1.74.0 (Robolectric 4.16.1, `@GraphicsMode(NATIVE)`, CI 태스크 `verifyRoborazziDebug`). 골든은 `<모듈>/src/test/screenshots/`에 커밋하고 렌더링은 `@Config(sdk = [35])`로 고정(실빌드 실증 2026-09-09). Paparazzi·compose-screenshot 미채택 |
| ERROR_TYPE | 도메인별 커스텀 `sealed` 계층 + 구분 무의미한 부재는 nullable. `kotlin.Result`·Arrow Either 미채택. 데이터 계층 경계에서 도메인 타입으로 변환 |
| USECASE_POLICY | 로직이 있을 때만 생성. 단순 위임이면 ViewModel → Repository 직접 호출. 승격 조건은 ViewModel 2개 이상 공유 또는 Repository 2개 이상 조합 |
| FORMATTER | ktlint-gradle 14.2.0 단독, 엔진 `ktlint 1.8.0` 고정, `.editorconfig`에 `ktlint_code_style = android_studio`. 이 플러그인은 Maven Central에 없어 저장소에 `gradlePluginPortal()`이 있어야 한다(실빌드 실증 2026-09-09). spotless·detekt-formatting 미채택 |
| NAV3_VERSION | Navigation 3 stable 1.1.7. ResultEventBus·DeepLinkRequest 없음 → 결과 반환은 공유 상태·상위 ViewModel, 딥링크는 `DeepLinkPattern`+`KeyDecoder` 직접 구현 |
| DETEKT_LINE | detekt 2.0.0-alpha.6, 플러그인 id `dev.detekt`. 임계값은 문서 확인 기본값(파라미터 5·6, 복합조건 3, 순환복잡도 14, 중첩 4, LongMethod 60). CI 태스크는 타입 해석이 있는 `detektDebug` — plain `detekt`는 타입 해석 규칙을 조용히 통과시킨다(실빌드 실증 2026-09-09) |
| FEATURE_MODULE_SPLIT | 단일 `:feature:*` 모듈로 시작. feature 간 직접 이동 없음(콜백 + `:app` 조합층). 다른 feature가 NavKey를 직접 참조해야 할 때만 `:api`/`:impl` 분할 |

확정 버전 라인은 AGP 9.4.0 · Gradle 9.7.1 · JDK 17 · compileSdk·targetSdk 37 · Kotlin 2.4.20 · Compose BOM 2026.08.00 · Hilt 2.60.1 · Navigation 3 1.1.7이다(Gradle 9.7.1 · compileSdk 37은 실빌드 실증 2026-09-09 — 조사 시점 값은 각각 "9.6.x 이상"·36이었다). 전체 목록은 `enforcement/build-logic/libs.versions.toml.snippet`이 정본이고, 조사 근거는 `research/testing-ci.md`의 "확인 버전 표"에 있다.

## 한계·주의

- 검증은 스크래치 프로젝트의 CLI 게이트까지다. **실기기·계측 테스트로는 검증하지 않았다.**
- 스크린샷 테스트 렌더링은 `@Config(sdk = [35])`로 고정한다. Robolectric 4.16.1의 상한은 36이지만 SDK 36 이미지가 Java 21을 요구하고 팩은 JDK 17이기 때문이다.
- CLI로 게이트를 돌릴 때 셸 기본 JDK가 17이 아니면 `JAVA_HOME`을 JDK 17 경로로 인라인 지정해야 한다.
- 20~22번 규칙 문서의 Bad 예시 약 15건은 코드가 아니라 산문 설명이고, Bad 예시가 아예 없는 규칙도 4건 있다. 리뷰에서 차단 사유가 아니라고 보고 남겨 뒀다.
