# 31 CI/CD

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

CI는 규칙 집행의 두 번째 층이다. 기계가 판정할 수 있는 규칙은 린터가 잡고, 린터가 잡은 것을 사람이 우회하지 못하게 막는 것이 이 문서의 게이트다.

게이트를 돌리는 기본 위치는 개발자 PC다(R-31-18). 검사 순서(R-31-01)·Gradle 래퍼 사용(R-31-03)·R8 릴리스 빌드 검증(R-31-09)은 모든 프로젝트의 공통 의무이고, 로컬에서는 `scripts/check.sh`·`./gradlew`·`scripts/release.sh`가 그것을 지킨다. 워크플로 구성을 다루는 부분 — PR 필수 체크 등록(R-31-01), 워크플로의 래퍼 검증 스텝(R-31-03), 별도 릴리스 잡(R-31-09), R-31-02, R-31-04 ~ R-31-08, R-31-12, R-31-15 ~ R-31-17 — 은 GitHub Actions를 쓰기로 한 프로젝트에만 적용한다.

## 결정 매트릭스 — CI JDK

| 판단 기준 | 선택지 A: JDK 17 | 선택지 B: JDK 21 | 기본값 |
|---|---|---|---|
| AGP 9.4.0 요구 | 최소·기본 17 [S46](https://developer.android.com/build/releases/gradle-plugin) | 17 이상이면 동작 | A |
| NiA 워크플로 | — | Zulu 21 사용 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml) | A(팩은 최소 버전으로 고정) |
| Robolectric SDK 조합 | 4.16.1은 SDK 34·35가 Java 17 [S90](https://github.com/robolectric/robolectric/releases), 실빌드 실증 2026-09-09 | SDK 36 이미지가 Java 21을 요구(37은 미지원) | A. 스크린샷은 `@Config(sdk = [35])`로 렌더링하고, SDK 36이 필요해지면 그 잡만 B로 분리 |

## 규칙

### R-31-01 PR 게이트는 ktlintCheck → detektDebug → 단위 테스트 → assembleDebug 순으로 고정한다
- 규칙: PR 워크플로는 `ktlintCheck` → `detektDebug` → `testDebugUnitTest`(Konsist 아키텍처 테스트와 스크린샷 검증 `verifyRoborazziDebug` 포함) → `assembleDebug` 순으로 돌린다. 정적 분석은 반드시 variant 태스크 `detektDebug`를 쓴다 — plain `detekt`는 타입 해석이 없어 `UnsafeCallOnNullableType`(R-20-01)·`InjectDispatcher`(R-22-02)·`GlobalCoroutineUsage`(R-22-01)가 조용히 통과한다. 값싸고 자주 깨지는 검사를 앞에 두어 빠르게 실패시키고, 이 네 단계는 모두 PR 필수 체크로 등록해 실패 시 머지를 막는다.
- 근거: 순서는 이 팩의 선택이다 — 값싸고 빠른 검사를 앞에 둬 실패를 일찍 드러내는 원칙으로 정했다. NiA는 포맷 검사 → 스크린샷 verify(`verifyRoborazziDemoDebug`) → 단위 테스트(`testDemoDebug`) 순으로 돌리므로 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml), 위 순서를 NiA에서 가져왔다고 말할 수 없다. 출처에서 가져온 것은 이 검사들을 CI 게이트로 둔다는 점과 태스크 이름뿐이다 — 태스크 이름은 각 도구 문서 확인본이다 — ktlint-gradle 14.2.0의 `ktlintCheck` [S85](https://raw.githubusercontent.com/JLLeitschuh/ktlint-gradle/main/README.md), detekt 2.0.0-alpha.6의 `detekt` [S78](https://detekt.dev/docs/gettingstarted/gradle)(실빌드 실증 2026-09-09에서 variant 태스크 `detektDebug`가 등록되고 타입 해석이 이쪽에만 붙는 것을 확인해 CI는 `detektDebug`를 쓴다), Roborazzi의 `verifyRoborazziDebug` [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md). Konsist 검증은 `src/test`의 일반 테스트라 단위 테스트 단계에서 함께 돈다 [S76](https://docs.konsist.lemonappdev.com/getting-started/getting-started/add-konsist-dependency.md).
- 예시:
  ```yaml
  # Good
  - run: ./gradlew ktlintCheck
  - run: ./gradlew detektDebug
  - run: ./gradlew testDebugUnitTest verifyRoborazziDebug
  - run: ./gradlew assembleDebug
  # Bad: assembleDebug 를 먼저 돌려 포맷 오류를 10분 뒤에 알게 한다
  # Bad: ./gradlew detekt — 타입 해석이 없어 !! 과 하드코딩 디스패처를 통과시킨다
  ```
- 체크: 네 단계가 이 순서로 있는가. 정적 분석 스텝이 `detekt`가 아니라 `detektDebug`인가. 저장소 설정에서 네 잡이 모두 필수 체크로 지정돼 있는가.

### R-31-02 워크플로 골격은 checkout → setup-java(17) → setup-gradle로 고정한다
- 규칙: 모든 Gradle 잡은 `actions/checkout` → `actions/setup-java`(distribution `temurin`, java-version `17`) → `gradle/actions/setup-gradle` 세 스텝으로 시작한다. Robolectric 실행이 17에서 깨지는 경우에만 그 잡 하나를 21로 올리고 이유를 워크플로 주석에 남긴다.
- 근거: AGP 9.4.0의 최소·기본 JDK는 17이다 [S46](https://developer.android.com/build/releases/gradle-plugin). NiA는 setup-java와 `gradle/actions/setup-gradle` 조합을 쓴다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml), 액션 사용법은 릴리스·README 확인본이다 [S71](https://github.com/gradle/actions/releases).
- 예시:
  ```yaml
  # Good
  - uses: actions/checkout@v7
  - uses: actions/setup-java@v6
    with: { distribution: temurin, java-version: '17' }
  - uses: gradle/actions/setup-gradle@v6
  # Bad: JDK 를 지정하지 않고 러너 기본 JDK 에 맡긴다
  ```
- 체크: 모든 Gradle 잡이 같은 JDK를 쓰는가. 21을 쓰는 잡에 이유가 적혀 있는가.

### R-31-03 Gradle은 래퍼로만 실행하고 래퍼 검증을 켠다
- 규칙: CI에서 Gradle 호출은 항상 `./gradlew`로 한다. 러너에 설치된 Gradle이나 별도 배포판을 쓰지 않고, 래퍼 JAR 검증(`gradle/actions/wrapper-validation` 또는 setup-gradle의 검증 옵션)을 워크플로에 넣는다.
- 근거: `gradle/actions`는 래퍼 실행과 래퍼 JAR 검증을 함께 제공한다 [S71](https://github.com/gradle/actions/releases). 래퍼 버전은 확인본 9.7.1로 고정한다 [S70](https://gradle.org/releases/).
- 예시:
  ```yaml
  # Good
  - run: ./gradlew testDebugUnitTest
  # Bad
  - run: gradle testDebugUnitTest
  ```
- 체크: 워크플로에 `gradle ` 직접 호출이 있는가. 래퍼 검증 스텝이 있는가.

### R-31-04 액션은 메이저 태그로 고정한다
- 규칙: GitHub Actions는 메이저 태그로 고정한다 — `actions/checkout@v7`, `actions/setup-java@v6`, `gradle/actions/setup-gradle@v6`, `actions/upload-artifact@v7`. `@main`·`@master` 같은 이동 참조를 쓰지 않는다.
- 근거: 확인일 기준 각 액션의 최신 메이저는 checkout v7·setup-java v6·setup-gradle v6·upload-artifact v7이다 [S71](https://github.com/gradle/actions/releases).
- 예시:
  ```yaml
  # Good
  - uses: actions/upload-artifact@v7
  # Bad
  - uses: actions/upload-artifact@main
  ```
- 체크: 브랜치 참조로 고정된 액션이 있는가. 메이저 태그가 확인본과 같은가.

### R-31-05 Gradle 캐시는 기본 브랜치에서만 쓰고 PR에서는 읽기 전용으로 둔다
- 규칙: 캐시는 `gradle/actions/setup-gradle`이 관리하게 두고 손으로 `actions/cache`를 조립하지 않는다. PR 잡은 `cache-read-only: true`로 두어 포크·기능 브랜치가 공용 캐시를 덮어쓰지 못하게 한다.
- 근거: setup-gradle이 Gradle User Home 캐시와 읽기 전용 옵션을 제공한다 [S71](https://github.com/gradle/actions/releases).
- 예시:
  ```yaml
  # Good
  - uses: gradle/actions/setup-gradle@v6
    with: { cache-read-only: ${{ github.ref != 'refs/heads/main' }} }
  # Bad: PR 잡이 캐시를 쓰기 모드로 열어 둔다
  ```
- 체크: PR 잡의 캐시가 읽기 전용인가. 수동 캐시 스텝이 남아 있지 않은가.

### R-31-06 실패한 검사의 리포트는 항상 아티팩트로 올린다
- 규칙: detekt(HTML·SARIF), ktlint, 테스트 리포트, Roborazzi 비교 이미지는 잡이 실패해도 `actions/upload-artifact`로 업로드한다(`if: always()`). 로그 텍스트만 남기고 끝내지 않는다.
- 근거: detekt는 xml·html·md·sarif 리포트를 낸다 [S78](https://detekt.dev/docs/gettingstarted/gradle), Roborazzi는 비교 이미지를 산출한다 [S87](https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md), NiA도 CI 산출물을 업로드한다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml).
- 예시:
  ```yaml
  # Good
  - uses: actions/upload-artifact@v7
    if: always()
    with: { name: reports, path: '**/build/reports/**' }
  # Bad: 실패 시 스텝이 건너뛰어 diff 이미지를 볼 수 없다
  ```
- 체크: 업로드 스텝에 `if: always()`가 있는가. 스크린샷 diff가 아티팩트에 포함되는가.

### R-31-07 계측 테스트는 별도 잡으로 분리하고 PR 필수 게이트에 넣지 않는다
- 규칙: `connectedDebugAndroidTest`는 PR 필수 체크와 분리한 잡(야간 또는 기본 브랜치 푸시)에서 돌린다. PR 게이트는 R-31-01의 네 단계로 유지한다.
- 근거: NiA도 계측 테스트를 단위 테스트와 별도 잡으로 분리해 실행한다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml). 계측 테스트는 대표 플로우로 한정한다(R-30-12) [S30](https://developer.android.com/training/testing/fundamentals).
- 예시:
  ```yaml
  # Good: on: schedule 또는 push(main) 트리거의 instrumented 잡
  # Bad: PR 마다 에뮬레이터를 띄워 필수 체크를 5분 이상 늘린다
  ```
- 체크: PR 필수 체크 목록에 계측 잡이 들어가 있는가. 계측 잡 실패가 알림으로 전달되는가.

### R-31-08 키·서명 자료는 저장소에 두지 않고 CI 시크릿으로 주입한다
- 규칙: API 키·서명 키스토어·서비스 계정 JSON은 저장소에 커밋하지 않고 GitHub Actions secrets로 넣어 환경 변수나 임시 파일로 주입한다. 워크플로 로그에 값을 출력하지 않고, 포크 PR에서 시크릿이 필요한 잡을 돌리지 않는다.
- 근거: 보안 권장사항은 API 키를 소스에 커밋하지 말고 빌드 시점에 주입하며 주기적으로 로테이션하라고 정한다 [S38](https://developer.android.com/privacy-and-security/security-tips). 출처는 "커밋 금지·빌드 주입"까지 정하고, 주입 수단을 Actions secrets로 고정한 것은 이 팩의 선택이다.
- 예시:
  ```yaml
  # Good
  env: { MAPS_API_KEY: ${{ secrets.MAPS_API_KEY }} }
  # Bad: gradle.properties 에 키를 적어 커밋한다
  ```
- 체크: 저장소 검색에 키 문자열이 남아 있는가. 포크 PR에서 시크릿 잡이 도는가.

### R-31-09 릴리스 검증은 R8을 켠 릴리스 빌드로 별도 잡에서 한다
- 규칙: `assembleRelease`(최적화 켬 — DSL은 R-19-04)와 성능 측정은 PR 게이트가 아니라 릴리스 준비 잡에서 돌린다. 디버그 빌드 결과로 성능이나 난독화 안정성을 판단하지 않는다.
- 근거: 성능 측정은 디버그 모드가 비용을 얹기 때문에 릴리스 빌드에서 해야 한다 [S16](https://developer.android.com/develop/ui/compose/performance). Baseline Profile도 릴리스 변이에서 최적화를 켠 상태를 전제한다 [S37](https://developer.android.com/topic/performance/baselineprofiles/overview).
- 예시:
  ```yaml
  # Good: release 잡에서 ./gradlew assembleRelease 와 매크로벤치마크
  # Bad: PR 마다 assembleRelease 를 돌려 게이트 시간을 늘린다
  ```
- 체크: 릴리스 잡이 R8을 켠 변이를 빌드하는가. 성능 수치를 디버그 빌드에서 재지 않았는가.

## 실행 위치 (R-31-18, R-31-19)

확정 결정 `RUN_LOCATION`(2026-09-30): 기본은 로컬 실행이고 GitHub 워크플로는 선택이다.

### R-31-18 게이트·계측 테스트·릴리스 빌드는 로컬 스크립트로 돌리고 GitHub 워크플로는 선택이다
- 규칙: 저장소에 `scripts/check.sh`(R-31-01의 네 게이트를 같은 순서로), `scripts/instrumented.sh`(`connectedDebugAndroidTest`), `scripts/release.sh`(게이트 → versionCode 올리기 → `bundleRelease`)를 둔다. 워크플로(`android-ci`·`android-instrumented`·`android-release`)는 프로젝트가 고를 때만 넣고, 넣더라도 스크립트와 같은 Gradle 태스크를 부른다. 워크플로가 하나도 없어도 이 팩의 규칙을 전부 지킬 수 있어야 한다.
- 근거: 비공개 저장소의 GitHub 호스팅 러너 사용은 요금제 포함 분량에서 차감되고, 결제 수단이 있는 계정은 초과분이 청구된다 [S177](https://docs.github.com/en/billing/concepts/product-billing/github-actions). 실행 위치를 로컬로 둔 것은 출처가 아니라 팩 결정(`RUN_LOCATION`)이다.
- 예시:
  ```bash
  # Good
  scripts/check.sh          # 게이트 4단계
  scripts/release.sh        # 게이트 → versionCode +1 → bundleRelease
  # Bad: 게이트를 돌릴 방법이 워크플로뿐이라 GitHub 분량이 떨어지면 검사를 못 한다
  ```
- 체크: `scripts/check.sh`의 태스크와 순서가 R-31-01과 같은가. 워크플로에만 있고 스크립트에는 없는 검사가 있는가.

### R-31-19 푸시 전에 pre-push 훅이 게이트를 돌린다
- 규칙: `scripts/hooks/pre-push`가 `scripts/check.sh`를 부르고 실패하면 푸시를 막는다. 훅은 검사 전에 두 가지를 확인한다 — 브랜치 푸시의 대상 커밋이 지금 체크아웃한 `HEAD`와 같은지, 추적 파일에 커밋되지 않은 변경이 없는지. 하나라도 아니면 검사를 돌리지 않고 푸시를 막는다(작업 트리를 검사해 놓고 다른 커밋을 올리는 일을 막으려는 것이다). 저장소를 받은 뒤 `git config core.hooksPath scripts/hooks`를 한 번 실행한다. `--no-verify`로 건너뛴 푸시는 다음 푸시 전에 게이트를 직접 돌린다. 셸 스크립트와 훅은 `.gitattributes`에서 `eol=lf`로 고정한다.
- 근거: pre-push 훅은 "can be used to prevent a push from taking place" 이고 "If this hook exits with a non-zero status, git push will abort without pushing anything." 이며, 훅 위치는 `core.hooksPath`로 바꾼다 [S162](https://git-scm.com/docs/githooks). 푸시 시점에 자동으로 돌리는 것은 팩 결정(`RUN_LOCATION`)이다.
- 예시:
  ```bash
  # Good — 저장소를 받은 뒤 한 번
  git config core.hooksPath scripts/hooks
  # Bad: 훅을 .git/hooks 에 손으로 복사해 저장소의 훅과 어긋난다
  ```
- 체크: `git config core.hooksPath`가 `scripts/hooks`인가. 훅이 `check.sh` 말고 다른 명령을 따로 들고 있지 않은가.

## 배포 (R-31-10 ~ R-31-15)

확정 결정 `RELEASE`(2026-09-30): 업로드는 사람이 한다 — 기본은 Play Console에 직접, 워크플로를 쓰면 수동 실행으로만. versionCode는 저장소의 카운터 파일, 테스터 채널은 Play 내부 테스트 트랙 하나, 워크플로의 업로드 도구는 `r0adkll/upload-google-play`. 후보 비교는 `research/release-cd.md`.

### R-31-10 Play 업로드는 사람이 한다 — 기본은 Play Console, 워크플로를 쓰면 수동 실행으로만
- 규칙: 기본 경로는 `scripts/release.sh`가 만든 AAB를 사람이 Play Console의 내부 테스트 트랙에 올리는 것이다. 배포 워크플로를 넣은 프로젝트는 트리거를 `workflow_dispatch` 하나만 둔다. `push`·`schedule`·태그 트리거로 업로드하지 않는다. 실행 버튼을 누르는 것이 승인이다. 공개 저장소이거나 GitHub Enterprise라서 환경(environment)의 required reviewers를 쓸 수 있을 때만 "자동 빌드 → 승인 대기 → 업로드"로 바꿀 수 있고, 바꾸면 그 사실을 프로젝트 지침 파일에 적는다.
- 근거: 릴리스는 Play Console에서 만들어 트랙에 올릴 수 있다 [S166](https://support.google.com/googleplay/android-developer/answer/9859348). 수동 실행은 `workflow_dispatch` 이벤트가 있는 워크플로만 가능하고 저장소 쓰기 권한이 있어야 한다 [S185](https://docs.github.com/en/actions/how-tos/manage-workflow-runs/manually-run-a-workflow). required reviewers는 Free·Pro·Team 요금제에서 공개 저장소에만 제공된다 [S186](https://docs.github.com/en/actions/reference/workflows-and-actions/deployments-and-environments). 사람이 올리거나 승인한 뒤에만 업로드한다는 것은 출처가 아니라 팩 결정(`RELEASE`)이다.
- 예시:
  ```yaml
  # Good — .github/workflows/android-release.yml
  on:
    workflow_dispatch:
  # Bad: main 에 머지될 때마다 스토어로 올라간다
  on:
    push:
      branches: [main]
  ```
- 체크: 배포 워크플로의 `on:`에 `workflow_dispatch` 말고 다른 트리거가 있는가. 업로드 스텝이 PR 게이트 워크플로(R-31-01)에 섞여 있지 않은가.

### R-31-11 versionCode는 저장소의 카운터 파일에서 읽고 릴리스 스크립트가 올린다
- 규칙: versionCode는 `distribution/version-code.txt`에 정수 하나로 둔다. `:app`의 `defaultConfig.versionCode`는 그 파일을 읽고 파일이 없으면 `1`을 쓴다. 내용이 비었거나 숫자가 아니면 조용히 `1`로 넘어가지 않고 빌드가 실패해야 한다. 값을 올리는 곳은 `scripts/bump-version-code.sh` 하나이고 `scripts/release.sh`가 번들을 만들기 전에 부른다. 번들 빌드가 실패해도 올린 값을 되돌리지 않는다. 올린 파일은 그 릴리스와 함께 커밋한다. 사람이 빌드 파일의 숫자를 고치지 않는다. 배포 워크플로를 쓰는 프로젝트도 같은 파일을 읽고 실행 번호는 쓰지 않는다. versionName의 형식은 이 규칙이 정하지 않는다.
- 근거: "The greatest value Google Play allows for versionCode is 2100000000." 이고 릴리스마다 더 큰 값이어야 하며 이미 쓴 값으로는 올릴 수 없다 [S161](https://developer.android.com/studio/publish/versioning). 값을 만드는 방식은 출처가 정하지 않아 팩 결정(`RELEASE`)이다 — 로컬과 워크플로가 같은 값을 쓰도록 출처를 파일 하나로 뒀다. 같은 식을 AGP 없이 Gradle 9.7.1과 configuration cache로 확인했다 — 파일이 없으면 1, 값이 바뀌면 캐시가 무효화되고 같은 값이면 재사용되며, 빈 파일·숫자가 아닌 내용은 빌드 실패다(2026-09-30). AGP 9.4의 `defaultConfig.versionCode` 대입으로는 아직 돌려 보지 않았다.
- 예시:
  ```kotlin
  // Good — :app/build.gradle.kts
  val versionCodeFile = rootProject.layout.projectDirectory.file("distribution/version-code.txt")
  defaultConfig {
      versionCode = providers.fileContents(versionCodeFile).asText
          .map { it.trim().toInt() }
          .getOrElse(1)
  }
  // Bad — 릴리스마다 사람이 숫자를 고쳐 커밋한다
  defaultConfig { versionCode = 42 }
  ```
- 체크: `versionCode`에 숫자 리터럴이 기본값 말고 남아 있는가. 카운터 파일의 값이 Play에 이미 올린 가장 큰 versionCode 이상인가. 올린 카운터 파일이 커밋됐는가.

### R-31-12 업로드는 완성된 AAB를 `r0adkll/upload-google-play`에 넘기고 트랙을 항상 적는다
- 규칙: 배포 워크플로를 쓰는 프로젝트의 배포 잡은 `bundleRelease`가 만든 AAB를 `r0adkll/upload-google-play@v1`에 넘긴다. `tracks`를 생략하지 않는다. 폐기 예정 입력 `track`·`releaseFile` 대신 `tracks`·`releaseFiles`를 쓴다. 업로드를 Gradle 빌드에 묶는 플러그인은 쓰지 않는다.
- 근거: 이 액션의 `tracks` 기본값은 `production`이라 생략하면 프로덕션으로 올라간다 [S163](https://github.com/r0adkll/upload-google-play). Gradle Play Publisher는 README가 유지보수 모드라고 밝히고 AGP에 묶인다 [S170](https://github.com/Triple-T/gradle-play-publisher). 도구를 추천하는 공식 문서는 없어 선택은 팩 결정(`RELEASE`)이고, 이 액션이 AGP 9.4.0 산출물을 올리는 것은 아직 실행으로 확인하지 않았다.
- 예시:
  ```yaml
  # Good
  - uses: r0adkll/upload-google-play@v1
    with:
      serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
      packageName: com.example.app
      releaseFiles: app/build/outputs/bundle/release/*.aab
      tracks: internal
  # Bad: tracks 를 빼서 기본값 production 으로 올라간다
  ```
- 체크: 업로드 스텝에 `tracks`가 있는가. 액션이 메이저 태그로 고정돼 있는가(R-31-04).

### R-31-13 워크플로가 올리는 트랙은 내부 테스트까지다
- 규칙: 배포 워크플로의 `tracks` 값은 `internal`만 쓴다. 사람이 Play Console로 올릴 때도 첫 트랙은 내부 테스트다. 비공개·공개 테스트와 프로덕션으로의 승격, 단계적 출시 비율 조정은 Play Console에서 사람이 한다. 테스터에게 빌드를 전달하는 채널도 내부 테스트 트랙 하나로 둔다.
- 근거: "An internal test can have up to 100 testers per app." 이고 올린 번들은 몇 분 안에 테스터에게 전달된다 [S164](https://support.google.com/googleplay/android-developer/answer/9845334). 단계적 출시 비율은 자동으로 오르지 않아 사람이 올려야 한다 [S165](https://support.google.com/googleplay/android-developer/answer/6346149). 끝나지 않은 릴리스가 남아 있으면 새 릴리스를 만들 수 없다 [S166](https://support.google.com/googleplay/android-developer/answer/9859348). 자동화 범위를 내부 테스트로 끊는 것은 팩 결정(`RELEASE`)이다.
- 예시:
  ```yaml
  # Good
  tracks: internal
  # Bad: 워크플로가 프로덕션 단계적 출시까지 연다
  tracks: production
  userFraction: 0.1
  ```
- 체크: 워크플로에 `internal` 말고 다른 트랙 이름이 있는가. 프로덕션 승격을 자동화한 스텝이 있는가.

### R-31-14 새 앱의 첫 업로드는 Play Console에서 사람이 하고, 개인 계정의 프로덕션 요건은 일정에 넣는다
- 규칙: 새 앱의 첫 AAB는 Play Console에서 직접 올리고, 배포 워크플로를 쓰는 프로젝트도 워크플로는 그다음 릴리스부터 쓴다. 2023-11-13 이후에 만든 개인 개발자 계정이면 프로덕션 접근 조건인 비공개 테스트 12명·14일 연속 요건을 출시 일정에 넣는다. 이 요건은 내부 테스트 업로드의 선행 조건이 아니고, 내부 테스트 트랙 참여로 채워지지도 않는다.
- 근거: 앱 등록은 Play Developer API로 할 수 없어 첫 APK·App Bundle은 Play Console로 올려야 한다 [S170](https://github.com/Triple-T/gradle-play-publisher). 개인 계정은 "must run a closed test for their app with a minimum of 12 testers who have been opted in continuously for at least 14 days" 이다 [S167](https://support.google.com/googleplay/android-developer/answer/14151465). 이 문서는 조직 계정을 언급하지 않는다.
- 예시:
  ```text
  # Good: Play Console 에서 첫 AAB 업로드 → 서비스 계정 초대 → 다음 릴리스부터 워크플로
  # Bad: 앱을 등록하자마자 워크플로부터 돌리고 업로드 실패 원인을 워크플로에서 찾는다
  ```
- 체크: 첫 업로드를 Play Console에서 끝냈는가. 계정 유형(개인·조직)과 생성일을 확인했는가.

### R-31-15 Play 업로드 인증은 서비스 계정으로 하고, 릴리스 노트는 저장소 파일로 둔다
- 규칙: 배포 워크플로의 업로드는 Play Console의 사용자·권한에 초대한 서비스 계정으로 한다. 키 JSON을 쓰면 R-31-08대로 시크릿으로만 넣는다. Workload Identity Federation을 쓰면 잡에 `permissions: id-token: write`를 주고 `actions/checkout`을 인증 스텝보다 먼저 실행한다. 릴리스 노트는 `distribution/whatsnew/whatsnew-<언어 태그>` 파일로 커밋하고 언어마다 500자를 넘기지 않으며 홍보·행동 유도 문구를 넣지 않는다.
- 근거: "In most cases, you should use a service account to access the API." [S168](https://developers.google.com/android-publisher/getting_started). 키 JSON은 만료되지 않는 자격이라 비밀번호처럼 다뤄야 하고 Workload Identity Federation이 권장된다 [S169](https://github.com/google-github-actions/auth). "You can enter release notes using up to 500 Unicode characters per language." 이고 홍보 목적으로 쓰지 말라고 한다 [S166](https://support.google.com/googleplay/android-developer/answer/9859348). 파일 이름 형식은 액션의 `whatsNewDirectory` 입력이 정한다 [S163](https://github.com/r0adkll/upload-google-play). 디렉터리 위치 `distribution/whatsnew/`는 팩 결정이다.
- 예시:
  ```yaml
  # Good
  with:
    serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
    whatsNewDirectory: distribution/whatsnew
  # Bad: 키 JSON 파일을 저장소에 커밋하고 경로로 넘긴다
  with:
    serviceAccountJson: play-key.json
  ```
- 체크: 저장소에 서비스 계정 JSON이 커밋돼 있는가. `whatsnew-*` 파일이 언어별 500자 이내인가.

## 계측 잡 실행 환경

확정 결정 `E2E`(2026-09-30): 계측 테스트의 기본 실행 위치는 로컬 기기·에뮬레이터다(R-31-18). GitHub에서 돌리기로 한 프로젝트만 아래 두 규칙을 따르고, 그때는 러너에서 에뮬레이터를 직접 띄운다. 후보 비교는 `research/e2e-testing.md`.

### R-31-16 계측 잡은 `ubuntu-latest`에서 KVM 권한을 먼저 주고 `android-emulator-runner`로 돌린다
- 규칙: GitHub에서 계측 테스트를 돌리는 프로젝트의 계측 잡은 `ubuntu-latest`에서 KVM 권한 스텝 → R-31-02의 세 스텝 → `reactivecircus/android-emulator-runner@v2` 순으로 구성하고 `script`에서 `./gradlew connectedDebugAndroidTest`를 부른다. `arch: x86_64`, `disable-animations: true`를 적는다. API 레벨은 minSdk와 최근 이미지 하나를 매트릭스로 둔다. macOS 러너와 Gradle Managed Devices는 이 잡에 쓰지 않는다. 실행 시점은 R-31-07, 리포트 업로드는 R-31-06을 따른다.
- 근거: "GitHub-hosted Linux runners support hardware acceleration for Android SDK tools, which makes running Android tests much faster and consumes fewer minutes." [S176](https://docs.github.com/en/actions/reference/runners/github-hosted-runners). 액션 README는 Ubuntu 러너를 권하고 실행 전에 KVM을 켜라고 한다 [S175](https://github.com/ReactiveCircus/android-emulator-runner). NiA도 같은 구성으로 API 26·34를 돌린다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml). 비공개 저장소는 GitHub Free 기준 월 2,000분 안에서 차감되고, 결제 수단이 없으면 분량을 다 쓴 뒤 실행이 막힌다 [S177](https://docs.github.com/en/billing/concepts/product-billing/github-actions). 템플릿의 예약 주기(주 2회)는 이 분량을 아끼려는 팩 선택이다. 환경 선택과 API 레벨 조합은 출처가 정하지 않아 팩 결정(`E2E`)이다.
- 예시:
  ```yaml
  # Good
  - name: Enable KVM group perms
    run: |
      echo 'KERNEL=="kvm", GROUP="kvm", MODE="0666", OPTIONS+="static_node=kvm"' | sudo tee /etc/udev/rules.d/99-kvm4all.rules
      sudo udevadm control --reload-rules
      sudo udevadm trigger --name-match=kvm
  - uses: reactivecircus/android-emulator-runner@v2
    with: { api-level: 26, arch: x86_64, disable-animations: true, script: ./gradlew connectedDebugAndroidTest }
  # Bad: KVM 스텝 없이 에뮬레이터를 띄워 소프트웨어 에뮬레이션으로 돈다
  ```
- 체크: KVM 스텝이 에뮬레이터 스텝보다 앞에 있는가. 매트릭스에 minSdk가 들어 있는가.

### R-31-17 계측 워크플로는 이번 달 Actions 사용량이 기준을 넘으면 테스트를 건너뛴다
- 규칙: GitHub에서 계측 테스트를 돌리는 프로젝트는 계측 워크플로의 첫 잡이 billing usage API로 이번 달 Actions 사용 분을 읽고, 요금제 포함 분량의 95% 이상이면 계측 잡을 실행하지 않는다. 토큰이 없거나 조회에 실패해도 실행하지 않는다. 예약 실행과 수동 실행 모두에 적용한다. PR 게이트와 배포 워크플로에는 이 검사를 넣지 않는다 — 남은 분량을 그쪽에 남기려는 것이다. 이 검사는 계측 잡만 막으므로, 요금 상한은 계정의 Actions 예산에 "Stop usage when budget limit is reached"를 켜서 따로 건다.
- 근거: 사용량은 `GET /organizations/{org}/settings/billing/usage`(개인 계정은 `/users/{username}/…`)가 돌려주고, 토큰에는 조직이면 "Administration" 조직 권한(read), 개인이면 "Plan" 사용자 권한(read)이 필요하다 [S187](https://docs.github.com/en/rest/billing/usage). 결제 수단이 있는 계정은 포함 분량을 넘긴 사용이 청구되고 예산으로 제한할 수 있다 [S177](https://docs.github.com/en/billing/concepts/product-billing/github-actions). 기준 95%와 조회 실패 시 건너뛰기는 출처가 아니라 팩 결정(`E2E`)이다.
- 예시:
  ```yaml
  # Good
  connected:
    needs: guard
    if: needs.guard.outputs.run == 'true'
  # Bad: 사용량을 보지 않고 예약 실행이 포함 분량을 넘겨 청구된다
  ```
- 체크: 계측 잡이 `guard` 잡의 출력에 걸려 있는가. `INCLUDED_MINUTES`가 실제 요금제의 포함 분량과 같은가. 계정에 Actions 예산이 걸려 있는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **빌드 시간 예산**: 조사한 어떤 출처도 CI 빌드 시간 상한을 제시하지 않는다. 숫자를 규칙으로 쓰지 않고, 게이트 순서(R-31-01)와 캐시(R-31-05)로 시간을 관리한다.
- **릴리스 브랜치·태그 이름 규칙**: `release/*`·`v1.2.3` 같은 관행은 널리 쓰이지만 이 팩의 출처 중 어느 것도 정하지 않는다. 프로젝트 지침 파일에서 정할 자리다.
- **versionName 형식**: Google 문서는 형식을 강제하지 않고 시맨틱 버저닝을 소개만 한다. 프로젝트 지침 파일에서 정한다.
- **단계적 출시의 시작 비율과 관찰 기간**: 수치를 권하는 출처가 없다. R-31-13이 승격을 사람에게 맡기는 이유다.
- **계측 잡의 재시도 횟수와 Test Orchestrator**: 공식 문서는 재시도를 두라고만 하고 횟수를 정하지 않으며, Orchestrator는 이점과 비용을 함께 적을 뿐 권하지 않는다. 둘 다 규칙으로 쓰지 않는다.
- **릴리스 빌드 대상 전체 플로우 테스트**: 공식 문서는 R8을 켠 빌드 검증에 UI Automator를 지목하지만 이 팩은 채택하지 않았다(`E2E`). 채택하지 않은 범위는 릴리스 변이를 대상으로 한 전체 플로우 테스트 하나이고, R-31-09의 릴리스 빌드·성능 측정은 그대로다.
- **configuration cache 활성화**: **해소됨(2026-09-16).** setup-gradle 문서에는 없지만 Gradle 공식 문서(9.0부터 preferred mode)와 Android 빌드 속도 가이드가 근거를 준다 → `R-19-10`이 소유한다.
