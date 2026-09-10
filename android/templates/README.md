# 코드 템플릿

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 새 feature 모듈 한 개를 만드는 최소 골격이다.

## 플레이스홀더 3종

| 토큰 | 뜻 | 예 |
|---|---|---|
| `{{Feature}}` | 기능 이름 PascalCase. 파일 이름에도 들어간다 | `Login` |
| `{{feature}}` | 기능 이름 소문자. 패키지 조각·모듈 이름 | `login` |
| `{{package}}` | 앱 루트 패키지 | `com.example.app` |

이 3개 외의 `{{...}}` 토큰은 쓰지 않는다.

## 계층 방향

`ui → domain → data → model`. 패키지는 `{ui, domain, data, model, di}` 5종이고,
data는 ui·domain을 참조하지 않으며 model은 아무것도 참조하지 않는다 (R-11-01).
Repository는 인터페이스와 구현 모두 data에 있고, 다른 계층은 인터페이스 타입에만 의존한다 (R-11-02).

## 파일이 가는 곳

| 템플릿 | 대상 | 비고 |
|---|---|---|
| `module/build.gradle.kts` | `feature/{{feature}}/build.gradle.kts` | convention 플러그인 사용 |
| `ui/*.kt` | `feature/{{feature}}/src/main/kotlin/…/ui/` | Key·Route·UiState·ViewModel·Screen |
| `ui/mvi/{{Feature}}ViewModel.kt` | 위 ViewModel을 **대체** | MVI를 고른 경우만 |
| `model/{{Feature}}.kt` | `…/model/` | 모델 + 에러/결과 sealed 타입 |
| `domain/Get{{Feature}}UseCase.kt` | `…/domain/` | UseCase 하나뿐 |
| `data/*.kt` | `…/data/` | Repository·Remote·Local 각각 인터페이스 + `Default*` 구현 |
| `di/{{Feature}}Module.kt` | `…/di/` | `@Binds` 3개(Repository·Remote·Local) |
| `di/Dispatchers.kt` | `core/common` | 프로젝트당 한 번만 복사 — **이미 있으면 건너뛴다** |
| `test/MainDispatcherRule.kt` | `core/testing` | 프로젝트당 한 번만 복사 — **이미 있으면 건너뛴다** |
| `test/Fake*.kt`, `test/*Test.kt` | `feature/{{feature}}/src/test/kotlin/` | fake 3종·ViewModel/Repository 단위 테스트·Roborazzi 스크린샷 |

DataSource를 인터페이스 + `Default*` 구현으로 나눈 이유는 하나다. Repository 단위 테스트가
Remote·Local을 fake로 갈아끼울 수 있어야 한다 (R-30-10). 구현이 하나뿐인 인터페이스를 금지하는
R-00-05의 예외가 바로 이 테스트 대역 경계다.

`module/build.gradle.kts` 는 `projects.core.common` 형태의 타입세이프 프로젝트 접근자를 쓴다.
루트 `settings.gradle.kts` 에 `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` 가 없으면
첫 feature 모듈에서 `Unresolved reference: projects` 가 난다(Gradle 9.7.1 에서도 incubating).
설치 절차는 `$PACK_ROOT/enforcement/README.md` "설치 순서" 2단계.

## 팩 루트(`PACK_ROOT`)를 먼저 정한다

팩 루트는 **지금 읽고 있는 `SKILL.md`가 들어 있는 디렉터리**다. 설치 방식에 따라 경로가 다르므로
`android/templates` 처럼 고정해 쓰지 않는다.

| 설치 방식 | 팩 루트 |
|---|---|
| Claude Code 전역 스킬(링크·정션) | `~/.claude/skills/android-standards` |
| `bootstrap.sh --pack android` 주입 | `<프로젝트>/harness/android` |
| 이 저장소에서 직접 | `<repo>/android` |

```bash
SKILL_PATH=~/.claude/skills/android-standards/SKILL.md   # 실제로 읽은 SKILL.md 경로
PACK_ROOT="$(dirname "$SKILL_PATH")"
```

아래 명령과 `references/` 검색은 전부 `$PACK_ROOT/templates`·`$PACK_ROOT/references`로 쓴다.

## 치환 명령

레포 루트에서 실행한다. `NAME`/`LOWER`/`PKG` 세 값만 바꾸면 된다.
공용 파일 2개(`Dispatchers.kt`·`MainDispatcherRule.kt`)는 **대상이 이미 있으면 쓰지 않는다** —
두 번째 feature를 만들 때 먼저 만든 공용 파일의 수정 내용을 덮어쓰지 않기 위해서다.
대상 디렉터리는 복사 전에 전부 `mkdir -p` 로 만든다(없으면 `>` 리다이렉션이 실패한다).

```bash
# 예: Login 화면을 :feature:login 에 생성
SRC="$PACK_ROOT/templates"
DST=feature/login/src
NAME=Login
LOWER=login
PKG=com.example.app
PKG_DIR="${PKG//./\/}"   # com.example.app → com/example/app

MAIN_PKG="$DST/main/kotlin/$PKG_DIR/feature/$LOWER"
TEST_PKG="$DST/test/kotlin/$PKG_DIR/feature/$LOWER"
COMMON_PKG="core/common/src/main/kotlin/$PKG_DIR/core/common"
TESTING_PKG="core/testing/src/main/kotlin/$PKG_DIR/core/testing"

mkdir -p "$MAIN_PKG/ui" "$MAIN_PKG/domain" "$MAIN_PKG/data" "$MAIN_PKG/model" "$MAIN_PKG/di" \
         "$TEST_PKG" "feature/$LOWER" "$COMMON_PKG" "$TESTING_PKG"

subst() { # $1=템플릿 파일, $2=대상 디렉터리
  out="$2/$(basename "$1" | sed "s/{{Feature}}/$NAME/g")"
  sed -e "s/{{Feature}}/$NAME/g" -e "s/{{feature}}/$LOWER/g" -e "s/{{package}}/$PKG/g" "$1" > "$out"
}

subst_keep() { # 공용 파일용. 대상이 이미 있으면 손대지 않는다
  out="$2/$(basename "$1" | sed "s/{{Feature}}/$NAME/g")"
  if [ -e "$out" ]; then echo "skip: $out (이미 존재)"; else subst "$1" "$2"; fi
}

# main 소스 — 계층별 패키지 경로에 바로 놓는다 (ui/mvi 는 글롭에 걸리지 않는다 — 기본은 MVVM-UDF)
for f in "$SRC"/ui/*.kt; do subst "$f" "$MAIN_PKG/ui"; done
for f in "$SRC"/domain/*.kt; do subst "$f" "$MAIN_PKG/domain"; done
for f in "$SRC"/data/*.kt; do subst "$f" "$MAIN_PKG/data"; done
for f in "$SRC"/model/*.kt; do subst "$f" "$MAIN_PKG/model"; done
subst "$SRC/di/{{Feature}}Module.kt" "$MAIN_PKG/di"

# 테스트 소스 — fake 3종(Repository·Remote·Local) + 단위 테스트 3개. 패키지가 feature 루트라 서브디렉터리 없음
for f in "$SRC"/test/Fake*.kt "$SRC"/test/{{Feature}}ViewModelTest.kt \
         "$SRC"/test/Default{{Feature}}RepositoryTest.kt "$SRC"/test/{{Feature}}ScreenshotTest.kt; do
  subst "$f" "$TEST_PKG"
done

# 모듈 빌드 스크립트
subst "$SRC/module/build.gradle.kts" "feature/$LOWER"

# 프로젝트당 한 번만 — 이미 있으면 건너뛴다
subst_keep "$SRC/di/Dispatchers.kt" "$COMMON_PKG"
subst_keep "$SRC/test/MainDispatcherRule.kt" "$TESTING_PKG"
```

복사한 파일은 이미 패키지 경로에 놓인다 — `main`은 `…/ui`, `…/domain`, `…/data`, `…/model`, `…/di`,
`test`는 feature 루트 패키지(`{{package}}.feature.{{feature}}`) 그대로다. IDE의 "Move to package" 단계는 필요 없다.
공용 파일 2개(`Dispatchers.kt`→`{{package}}.core.common`, `MainDispatcherRule.kt`→`{{package}}.core.testing`)만
자신이 속한 모듈(`core/common`·`core/testing`)의 패키지 경로로 따로 들어간다.
`settings.gradle.kts`에 `include(":feature:login")` 추가도 잊지 않는다.

마지막으로 **`./gradlew ktlintFormat` 을 한 번 돌린다.** import 정렬은 사전순인데 `{{package}}` 가
무엇이냐에 따라 `androidx`·`dagger`·`com.github` 사이 어디에 들어갈지가 달라져 템플릿이 미리 맞춰둘 수 없다
(`com.example.app` 이면 `dagger` 앞, `net.…` 이면 뒤). ktlintFormat 이 자동 교정한다.
이미 포맷한 모듈에 템플릿을 다시 치환하면 입력이 직전 실행과 같아져 Gradle 이 format 태스크를
UP-TO-DATE 로 건너뛴다. 그때는 `./gradlew ktlintFormat --rerun-tasks` 로 강제한다.

## MVVM-UDF와 MVI 중 무엇을 쓰나

기본은 `ui/{{Feature}}ViewModel.kt`(MVVM-UDF)다. `MutableStateFlow`(private) + `StateFlow`(public) +
단일 UiState data class만 쓰고 Intent·Reducer 계층을 두지 않는다.

R-12-02 판단 매트릭스가 MVI를 가리킬 때만 `ui/mvi/{{Feature}}ViewModel.kt`로 바꾼다.
두 파일은 패키지·클래스 이름이 같으므로 **둘 중 하나만** 복사한다.

```bash
subst "$SRC/ui/mvi/{{Feature}}ViewModel.kt" "$MAIN_PKG/ui"   # 기본형을 덮어쓴다
```

바꾼 뒤 손으로 고칠 곳 4군데:

1. `{{Feature}}Route.kt` — `onEvent = viewModel::onEvent` → `onIntent = viewModel::dispatch`
2. `{{Feature}}Screen.kt` — 파라미터 `onEvent: ({{Feature}}Event) -> Unit` → `onIntent: ({{Feature}}Intent) -> Unit`,
   본문 `onEvent({{Feature}}Event.Retry)` → `onIntent({{Feature}}Intent.Retry)`, Preview 3개의 `onEvent = {}` → `onIntent = {}`
3. `{{Feature}}ViewModelTest.kt` — `onEvent({{Feature}}Event.Retry)` → `dispatch({{Feature}}Intent.Retry)` (import도 함께).
   `initialize()` 호출은 MVI 변형에도 그대로 있다
4. `{{Feature}}ScreenshotTest.kt` — `onEvent = {}` → `onIntent = {}` (2군데)

Orbit·Circuit·Mavericks 같은 MVI 라이브러리는 채택하지 않는다.

## 최초 로드는 `initialize()`가 시작한다 (R-12-07)

ViewModel의 `init` 블록에서 비동기 작업을 시작하지 않는다. 두 ViewModel 템플릿 모두 멱등 `initialize()`
(private `initialized` 플래그)를 두고, `{{Feature}}Route.kt` 가 `LaunchedEffect(viewModel) { viewModel.initialize() }`
로 한 번 부른다. 시작 시점을 UI가 통제하고 테스트가 로드를 명시적으로 켤 수 있게 하려는 것이다.
`{{Feature}}ViewModelTest.kt` 의 세 테스트도 `initialize()` 를 직접 부른다.

## 손댈 곳 표시

- `Default{{Feature}}RemoteDataSource.fetch()` — Retrofit 서비스 호출로 교체(인터페이스 `{{Feature}}RemoteDataSource`는 그대로 둔다)
- `Default{{Feature}}LocalDataSource` — Room DAO·DataStore로 교체(인터페이스 `{{Feature}}LocalDataSource`는 그대로 둔다)
- `{{Feature}}Error.toMessage()` — 리터럴 대신 `stringResource(R.string.…)` 사용
- `Get{{Feature}}UseCase` — 단순 위임뿐이면 파일을 지우고 ViewModel이 Repository를 직접 주입받는다 (R-16-02)

## 검증

```bash
grep -rn -E '\{\{[A-Za-z]+\}\}' "$PACK_ROOT/templates" | grep -v -E '\{\{(Feature|feature|package)\}\}'
```

출력이 없어야 한다.
