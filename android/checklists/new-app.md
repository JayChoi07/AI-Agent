# new-app — 0에서 새 앱 만들기
읽을 references: [10-project-structure, 18-app-shell, 19-build-config, 13-navigation, 14-di, 28-interaction-motion, 31-ci-cd, 33-dependency-updates, 40-performance-security, 41-rendering-memory]
함께 로드할 공식 스킬: [android-cli(프로젝트 생성·기기 실행), edge-to-edge, navigation-3]

"빈 폴더 → 첫 화면이 뜨는 앱"까지만 다룬다. 로그인·네트워크·DB·다국어는 이 유형의 범위가 아니다.

## 컨텍스트 수집 (구현 전)
- 프로젝트 지침 파일(CLAUDE.md/AGENTS.md) 유무 — 있으면 아래 결정보다 우선한다
- 앱 이름(PascalCase)·`applicationId`·패키지 루트를 사용자가 이미 정했는지
- 기존 저장소가 있는지(빈 디렉터리인지, 이미 커밋된 코드가 있는지). 코드가 있으면 new-app이 아니라 refactor다
- 사용자만 정할 수 있는 값 목록을 먼저 모아 한 번에 묻는다 — 앱 이름, `applicationId`, 첫 화면 이름, 브랜드 색·폰트 유무, GitHub 워크플로를 쓸지 여부(쓰면 PR 검사·계측·배포·의존성 업데이트 중 무엇을)
- `enforcement/README.md` "설치 순서" 8단계와 `templates/README.md` 치환 규칙을 읽는다
- 빌드 환경 — JDK 17이 잡혀 있는지(AGP 9.4.0의 최소·기본값), Gradle 래퍼를 새로 만들어야 하는지
- 기동 확인 수단 — 연결된 실기기나 에뮬레이터가 있는지(마지막 단계가 여기에 걸린다)

## 결정 항목 (구현 전 전부 명시)
| # | 결정 | 규칙 | 애매하면 질문할 것 |
|---|---|---|---|
| 1 | `applicationId` · `namespace` 루트 | R-19-03 | 스토어에 올릴 ID — 출시 후 못 바꾸므로 반드시 확정받는다 |
| 2 | compileSdk·targetSdk 37 / minSdk 26 | R-19-01, R-19-02 | 없음(확정값). minSdk를 26 아래로 내릴 단말이 실제로 있을 때만 묻는다 |
| 3 | buildType 2종 · product flavor 없음 | R-19-04, R-19-05, R-19-06 | 별도 `applicationId`로 따로 배포할 앱이 있는지 |
| 4 | 첫 모듈 그래프 — `:app` + 최소 `:core:*` | R-10-01, R-10-04 | 첫 화면이 실제로 쓰는 core가 무엇인지. 기본은 common·designsystem·testing 셋 |
| 5 | 시작 초기화 배치 | R-18-01, R-18-02 | 첫 프레임 전에 반드시 끝나야 하는 초기화가 있는지. 없으면 "지연"이 답이다 |
| 6 | 테마 — `:core:designsystem` `theme/`의 4파일, 진입점은 `AppTheme` 하나 | R-18-10, R-18-11, R-18-12 | 브랜드 색·폰트가 있는지, 다이나믹 컬러를 켤지 |
| 7 | 첫 화면 — 어떤 feature 하나로 시작하나 | R-10-13, R-13-01, R-13-03 | feature 이름과 화면 이름. 사용자만 정할 수 있다 |
| 8 | 로컬 검사·릴리스 스크립트와 pre-push 훅 | R-31-18, R-31-19, R-31-01 | 없음(확정값). GitHub 워크플로 없이도 게이트 4단계·릴리스 번들·versionCode 올리기가 돌아야 한다 |
| 9 | GitHub 워크플로 도입 여부 | R-31-02, R-31-18 | 기본은 "아니오". 비공개 저장소는 실행 시간이 요금제 포함 분량에서 차감되고 결제 수단이 있으면 초과분이 청구된다는 점을 알리고 묻는다 |
| 10 | 의존성 자동 업데이트 | R-33-01, R-33-02 | 저장소가 GitHub에 있는지. 있으면 Dependabot 설정은 골격 그대로이고 자동 병합은 하지 않는다 |
| 11 | 배포 | R-31-10, R-31-11, R-31-13, R-31-14 | Play 계정이 개인인지 조직인지, 첫 업로드를 Play Console에서 끝냈는지. 업로드는 사람이 한다 |
| 12 | 전체 플로우(계측) 테스트 | R-30-12, R-30-13, R-31-18 | 여러 화면을 지나는 대표 플로우가 이미 있는지. 첫 화면 하나뿐이면 "나중"이 답이다 |
| 13 | 모션 토큰·화면 전환 — `:core:designsystem` `motion/` 3파일, `NavDisplay` 전환 3종 | R-28-01, R-28-03, R-28-13, R-28-14 | 값은 규칙에 있어도 모양은 눈으로 확인된 것이 아니다 — "어느 앱의 어느 동작처럼 보여야 하나"(참조 앱)를 먼저 묻는다. 푸시·팝·탭 전환·하단 바·시스템 바 모양은 참조 앱을 실기기에서 애니메이터 배율 5배로 녹화해 프레임으로 확인하거나 임시 빌드를 폰에서 보여 주고 "맞아?"를 받기 전에는 결정 표에 확정으로 넣지 않는다. 브랜드가 다른 시간·곡선을 요구할 때도 묻는다 |
| 14 | Baseline Profile·벤치마크 모듈 `:baselineprofile`(`com.android.test`) — 만들 시점, 프로파일을 만들 기기, 저장·생성 방식 | R-40-03, R-41-17, R-10-01 | 첫 화면 기동 확인 뒤 바로 만들지 핵심 플로우(화면 이동·탭)가 생긴 뒤 만들지, 프로파일 생성 기기(실기기 API 33+ 또는 Gradle Managed Device), `saveInSrc`로 소스에 커밋할지 `automaticGenerationDuringBuild`로 릴리스 빌드마다 만들지 |
| 15 | 성능 기준 — 프레임 통과선·메모리 예산(제안값) | R-41-01, R-41-16 | 기본은 제안값 그대로다. 프로젝트 지침에 다른 값이 있을 때만 묻는다 |

2·3은 규칙이 값을 이미 정했으므로 묻지 않는다. 13은 값이 있어도 위 "애매하면 질문할 것"대로 모양을 참조 녹화나 시제품으로 확인받은 뒤에만 확정하고, 사용자가 확인하지 않은 값은 결정자를 "사용자"가 아니라 "제안"으로 적는다(`references/91-decisions.md` "결정자 표기 기준"). 15는 공식이 정하지 않은 제안값이 기본이므로 프로젝트 지침에 다른 값이 있을 때만 묻는다. 1·7과 6의 브랜드 값은 사용자만 정할 수 있으므로 임의로 고르지 않는다.
4는 "언젠가 쓸 것 같아서" 모듈을 미리 만들지 않는다 — 두 번째 사용처가 생길 때 올린다 (R-10-04).
5는 `references/18-app-shell.md`의 "초기화 배치 결정 매트릭스" 세 행을 그대로 채워 보여 준다. 세 행이 모두 "지연"을
가리키면 시작 경로에 아무것도 넣지 않는 것이 답이고, `Application.onCreate`를 열지 않는다 (R-18-01).
8은 묻지 않는다 — 스크립트와 훅은 항상 설치한다. 9가 "아니오"면 `.github/workflows/`는 만들지 않고, 12는 `scripts/instrumented.sh`로 로컬 기기·에뮬레이터에서 돌린다.
9가 "예"인 프로젝트만 워크플로 골격(`android-ci`·`android-instrumented`·`android-release`)을 고른 만큼 복사한다. 배포 워크플로의 업로드도 사람이 수동 실행했을 때만 돌므로(R-31-10) 워크플로를 넣는 것만으로는 스토어에 아무것도 올라가지 않는다.

## 구현 순서
1. 프로젝트 디렉터리 준비 — `android-cli` 스킬로 생성한다(Gradle 래퍼가 함께 생긴다). 빈 디렉터리에서 시작하면 첫 Gradle 호출 전에 래퍼부터 만든다(`gradle wrapper --gradle-version 9.7.1`, 로컬 Gradle 필요)
   → 검증: `gradlew`와 `gradle/wrapper/gradle-wrapper.properties`가 있고 distributionUrl이 9.7.1이다 (R-31-03), 저장소 루트에 `buildSrc/`가 없다 (R-10-09)
2. 강제 장치 설치 — `enforcement/README.md` "설치 순서" 1~7단계를 그대로 실행(여기서 다시 적지 않는다)
   → 검증: convention plugin id가 6종 목록 안 (R-10-10), 카탈로그 별칭↔`findLibrary` 문자열 1:1 (R-19-07, R-10-12)
3. 루트 3파일 — `templates/app/settings.gradle.kts`·`build.gradle.kts`·`gradle.properties`
   → 검증: 루트에 `subprojects {`·`allprojects {`가 없다 (R-10-11), `org.gradle.jvmargs`와 configuration cache 2줄이 있다 (R-19-09, R-19-10)
4. `:core:*` 3개 — 빌드 파일은 `templates/core/{common,testing,designsystem}/build.gradle.kts`(모듈별 `namespace`·최소 의존 포함). `:core:common`에 `templates/di/Dispatchers.kt`, `:core:testing`에 `templates/test/MainDispatcherRule.kt`, `:core:designsystem`에 `templates/designsystem/Color.kt`·`Type.kt`·`Shape.kt`·`Theme.kt`와 `motion/` 패키지의 `templates/designsystem/motion/Motion.kt`·`NavTransitions.kt`·`Feedback.kt`
   → 검증: 세 모듈의 `namespace`가 서로 다르고 `applicationId`가 없다 (R-19-03), `:core:testing`은 프로덕션 소스셋에서 참조되지 않는다 (R-10-03)
   → 검증: `theme/` 패키지의 공개 API가 `AppTheme` 하나, `Color(0x…)` 리터럴이 이 모듈 밖에 없다 (R-18-10, R-18-11)
   → 검증: `tween`·`spring`의 숫자 리터럴이 `:core:designsystem` 밖에 없다 — Konsist `ArchitectureTest`의 R-28-13 테스트가 잡는다 (R-28-13)
5. `:app` 빌드 파일 — `templates/app/app/build.gradle.kts`
   → 검증: `targetSdk` 명시 (R-19-01), `namespace`+`applicationId` 둘 다 (R-19-03), release `optimization { enable = true }`·debug `applicationIdSuffix` (R-19-04, R-19-05), `productFlavors` 없음 (R-19-06), 안 쓰는 `buildFeatures`가 꺼져 있음 (R-19-13)
6. 앱 셸 — `templates/app/app/src/main/`의 `AndroidManifest.xml`·`res/values/themes.xml`·`strings.xml`, `{{App}}Application.kt`·`MainActivity.kt`·`Navigator.kt`·`{{App}}App.kt`
   → 검증: `onCreate` 순서가 `installSplashScreen()`→`super.onCreate()`→`enableEdgeToEdge()`→`setContent` (R-18-03, R-18-06, R-18-07), `<activity>` 1개·`configChanges` 없음·`launchMode` 없음(딥링크를 받는 단일 Activity만 `singleTask` 예외) (R-18-05, R-18-14), Hilt 진입점 2개 (R-14-03), 인셋 소비 1회 (R-18-08), `currentWindowAdaptiveInfoV2()` 호출이 앱 루트뿐 (R-18-13), XML 테마에 색·타이포 없음 (R-18-12), `NavDisplay`의 전환 3종이 `NavTransitions`로 꽂혀 있음 (R-28-01, R-28-03)
7. 첫 화면 — `checklists/new-feature-module.md` 실행 후 `checklists/new-screen.md`
   → 검증: `{{Feature}}Key`가 feature 모듈 소유 (R-13-01), `rememberNavBackStack` 호출이 앱 전체에 하나 (R-13-03), `entryProvider` DSL에 등록되고 데코레이터 첫 항목이 SaveableStateHolder (R-13-04, R-13-05)
   → 검증: 화면이 생겼으므로 `enforcement/README.md` 설치 8단계(`recordRoborazziDebug` 골든 png 커밋)를 여기서 끝낸다 (R-30-04)
8. 로컬 스크립트와 훅 — `enforcement/scripts/`의 `check.sh`·`instrumented.sh`·`bump-version-code.sh`·`release.sh`를 `scripts/`로, `hooks/pre-push`를 `scripts/hooks/`로 복사하고 실행 권한을 준 뒤 `git config core.hooksPath scripts/hooks`를 실행한다
   → 검증: `scripts/check.sh`의 태스크가 ktlintCheck → detektDebug → 단위 테스트+스크린샷 검증 → assembleDebug 순서 (R-31-01, R-31-18), `git config core.hooksPath`가 `scripts/hooks` (R-31-19), 루트 `.gitattributes`가 셸 스크립트를 `eol=lf`로 고정 (R-31-19)
   → 결정 12가 "예"면 `templates/androidtest/` 두 파일을 `:app/src/androidTest/`로 복사하고 `:app/build.gradle.kts`의 계측 테스트 주석 블록을 켠다. 검증: 러너가 `HiltTestApplication`을 넘긴다 (R-30-14), `kspAndroidTest` 선언 (R-30-14)
   → 결정 9가 "예"면 고른 워크플로만 `enforcement/.github/workflows/`에서 복사한다. 검증: checkout → setup-java(17) → setup-gradle (R-31-02), 게이트 순서가 스크립트와 같음 (R-31-01, R-31-18). 계측 워크플로는 KVM 스텝이 에뮬레이터 스텝보다 앞이고 `guard` 잡의 출력에 걸려 있으며 `INCLUDED_MINUTES`가 요금제와 같다 (R-31-16, R-31-17). 배포 워크플로는 트리거가 `workflow_dispatch` 하나이고 `tracks: internal`이며 `packageName`이 `applicationId`와 같다 (R-31-10, R-31-12, R-31-13)
   → 결정 10이 "예"면 `enforcement/.github/dependabot.yml` 복사. 검증: `gradle`·`github-actions` 두 생태계, 전체 묶음 없음 (R-33-01, R-33-03)
9. `./gradlew ktlintFormat` → `./gradlew assembleDebug` → 기기·에뮬레이터에 설치해 첫 화면 기동 확인
   → 검증: `assembleDebug` 통과, 앱을 실행하면 스플래시 뒤 첫 화면이 그려지고 회전 후에도 같은 화면이 남는다 (R-18-14)
   → 검증: 푸시·팝·탭 전환·시스템 바 모양을 기기에서 눈으로 봤다(애니메이터 배율을 올린 녹화). 못 봤으면 "미확인"으로 보고하고 결정 13을 확정으로 올리지 않는다
10. 결정 14가 "지금"이면 Baseline Profile 모듈 — `templates/baselineprofile/`을 `templates/README.md`의 "Baseline Profile 모듈 치환"대로 만든다(`{{package}}` 치환 → 카탈로그 병합 → 루트·`settings`·`:app` 주석 블록 해제 → `Labels` 교체). 템플릿은 `InfocarEV_Android`의 `:baselineprofile`을 일반화한 것이고 컴파일·`ktlintCheck`·`detektNonMinifiedRelease`까지 확인했다(2026-10-15). 기기 실행(프로파일 생성·벤치마크)은 하지 않았다(미확인). `:app`의 `androidComponents.finalizeDsl` 블록은 AGP 9 + 플러그인 1.5.0 보정이라 플러그인이 새 DSL 을 따르면 지운다 (R-41-17)
    → 검증: 최상위 모듈이 `:app`·`:core:*`·`:feature:*` 외에 `:baselineprofile` 하나뿐 (R-10-01, R-41-17), 빌드 파일이 컨벤션 플러그인 없이 직접 구성 (R-10-14), `:app` 말고 이 모듈에 의존하는 모듈이 없음 (R-41-17), 앱 프로파일 파일이 저장소에 있고 릴리스 APK의 `assets/dexopt/baseline.prof`가 생성 전보다 커졌으며(파일은 생성 전에도 라이브러리 프로파일로 있다) `buildTypes`에 `benchmarkRelease`·`nonMinifiedRelease`를 손으로 적지 않음 (R-40-03), 생성기가 시작뿐 아니라 화면 이동·탭 전환을 돈다 (R-41-17), `Journeys.kt`의 `Labels`가 자리 표시가 아니라 앱의 실제 화면 글자다, `nonMinifiedRelease` 빌드가 R8(`minify…WithR8`)을 돌지 않는다 (R-41-17)
    → 이 모듈의 detekt·컴파일은 게이트 밖이므로 손으로 돌린다: `./gradlew :baselineprofile:ktlintCheck :baselineprofile:detektNonMinifiedRelease :baselineprofile:compileNonMinifiedReleaseKotlin`
    → 첫 프레임 측정은 R-41-02·R-41-03 절차(릴리스 상당 변이·실기기)로 하고 못 돌렸으면 "미실행"으로 적는다

템플릿 치환은 `templates/README.md`의 "앱 골격 치환"(3~6단계)과 "치환 명령"(7단계) 두 스크립트를 그 순서로 쓴다.
`:app`이 첫 화면의 키·엔트리를 import 하므로 7단계를 끝내기 전에는 `assembleDebug`가 통과하지 않는다.

## 산출물 검증
`checklists/review.md`를 실행해 "표준 준수 보고" 표를 응답 끝에 붙인다. "모듈 위치" 행에 만든 모듈 목록을, "네비게이션" 행에 백스택 소유 위치와 시작 키를 적는다 (R-32-01).

제출 전 자기검사 2가지:
- 응답 순서: 유형 → 결정 항목 표 → 구현 → 검증 → 표준 준수 보고
- 인용한 R-ID 전부 제목 확인(오인용 0)
