# 31 CI/CD

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

CI는 규칙 집행의 두 번째 층이다. 기계가 판정할 수 있는 규칙은 린터가 잡고, 린터가 잡은 것을 사람이 우회하지 못하게 막는 것이 이 문서의 게이트다.

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
- 규칙: `assembleRelease`(`isMinifyEnabled = true`)와 성능 측정은 PR 게이트가 아니라 릴리스 준비 잡에서 돌린다. 디버그 빌드 결과로 성능이나 난독화 안정성을 판단하지 않는다.
- 근거: 성능 측정은 디버그 모드가 비용을 얹기 때문에 릴리스 빌드에서 해야 한다 [S16](https://developer.android.com/develop/ui/compose/performance). Baseline Profile도 릴리스 변이에서 `isMinifyEnabled = true`를 전제한다 [S37](https://developer.android.com/topic/performance/baselineprofiles/overview).
- 예시:
  ```yaml
  # Good: release 잡에서 ./gradlew assembleRelease 와 매크로벤치마크
  # Bad: PR 마다 assembleRelease 를 돌려 게이트 시간을 늘린다
  ```
- 체크: 릴리스 잡이 R8을 켠 변이를 빌드하는가. 성능 수치를 디버그 빌드에서 재지 않았는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **빌드 시간 예산**: 조사한 어떤 출처도 CI 빌드 시간 상한을 제시하지 않는다. 숫자를 규칙으로 쓰지 않고, 게이트 순서(R-31-01)와 캐시(R-31-05)로 시간을 관리한다.
- **릴리스 브랜치·태그 이름 규칙**: `release/*`·`v1.2.3` 같은 관행은 널리 쓰이지만 이 팩의 출처 중 어느 것도 정하지 않는다. 프로젝트 지침 파일에서 정할 자리다.
- **configuration cache 활성화**: setup-gradle 확인본에서 configuration cache 사용을 명시한 문구를 확인하지 못했다. 켤지 여부는 스크래치 빌드(Task 16)에서 실증한 뒤 결정한다.
