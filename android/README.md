# android — Android 그린필드 표준 팩

Kotlin·Compose·Navigation 3·Hilt·멀티모듈 기준의 Android 개발 표준을 규칙(references) · 파이프라인(checklists) · 코드 골격(templates) · 강제 장치(enforcement)로 묶은 스킬 팩이다.
스킬 이름은 `android-standards`이고, 진입점은 [`SKILL.md`](SKILL.md)다. 요청 유형을 먼저 분류한 뒤 그 유형의 체크리스트만 따라가는 라우터 구조라, `references/` 18개 파일을 전부 읽지 않는다.
모든 규칙에는 `R-NN-MM` ID와 [`references/90-sources.md`](references/90-sources.md)의 출처 번호(S)가 붙는다. 출처 없는 규칙은 쓰지 않는다.

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

## 사용 흐름

에이전트는 Android 요청을 받으면 `SKILL.md`의 유형 표에서 **분류**(new-screen · new-feature-module · new-data-source · function · bugfix · refactor · review)를 하고 그 한 줄을 사용자에게 알린다. 이어서 해당 체크리스트의 "컨텍스트 수집"으로 프로젝트 지침 파일과 지정된 references만 읽고, "결정 항목" 표를 R-ID 근거와 함께 전부 채운다. 규칙으로 정해지지 않고 사용자만 정할 수 있는 항목은 임의로 결정하지 않고 묻는다. 그다음 "구현 순서"대로 `templates/`를 복사·치환해 작성하고, 마지막에 `checklists/review.md`를 실행해 응답 끝에 **표준 준수 보고** 표(모듈 위치 · 네비게이션 · 상태 아키텍처 · UseCase · 테스트 · CI · 어긴 규칙)를 붙인다.

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

## 규칙 갱신 절차

1. **출처 확보** — 공식 문서·릴리즈 노트 등 인용 가능한 URL을 찾는다. 출처가 침묵하는 관행은 규칙으로 만들지 않는다. 도구 기본값을 쓸 때는 근거 줄에 "출처는 도구 기본값"이라고 명시한다.
2. **`research/`에 기록** — 해당 주제 노트에 문서명·URL·확인 내용을 추가한다. 새 주제면 노트를 추가한다.
3. **`references/90-sources.md`에 등록** — 전역 번호 `S###` 행을 표 맨 아래에 추가한다. 기존 번호는 재배열하지 않는다.
4. **`references/NN-*.md`에 규칙 작성** — `### R-NN-MM` + `- 규칙:` `- 근거:`(S번호 + URL) `- 예시:`(Good/Bad) `- 체크:` 5요소를 채운다. R-ID는 파일 번호와 일치하고 전역 유일해야 한다. 파일당 300줄 이하.
5. **연동 갱신** — 규칙이 결정·검증에 걸리면 해당 `checklists/`, `templates/`, `enforcement/`(detekt.yml·Konsist)도 같이 고친다.
6. **검사** — `bash android/scripts/check-pack.sh`가 통과해야 한다. 줄 수 한도, 5요소, 근거 URL, R-ID 중복, 체크리스트 필수 헤더, 그리고 SKILL·checklists·templates·enforcement·README 가 인용한 R-ID 가 `references/`에 실제로 있는지를 검사한다.
