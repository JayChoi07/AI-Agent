# 32 코드 리뷰

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

집행 순서는 린터(기계 판정) → CI 게이트 → 사람 리뷰다. 린터와 CI가 판정할 수 있는 것을 사람이 다시 지적하지 않는 것이 이 문서의 전제다.

## 규칙

### R-32-01 작업을 마치면 표준 준수 보고를 정해진 표로 낸다
- 규칙: 에이전트나 작성자는 구현을 끝낸 뒤 아래 표를 그대로 채워 PR 본문(또는 응답 마지막)에 붙인다. 항목 이름과 순서를 바꾸지 않고, 각 행에 근거가 된 규칙 ID를 적는다. 어긴 규칙이 있으면 숨기지 않고 "어긴 규칙" 행에 이유와 함께 적는다.
- 근거: 리뷰어는 변경이 전체 코드 건강도를 개선하는지 판단해야 하며, 판단은 개인 취향이 아니라 기술적 사실과 명문화된 가이드 위에서 이뤄진다 [S39](https://google.github.io/eng-practices/review/reviewer/standard.html). 팀 규칙을 문장으로 명문화해 도구·에이전트가 같은 기준을 따르게 하는 방식이 실제 조직에서 쓰인다 [S114](https://toss.tech/article/52631).
- 예시:
  ```markdown
  ## 표준 준수 보고
  | 항목 | 내용 |
  |---|---|
  | 요청 유형 | new-screen |
  | 모듈 위치 | :feature:login (R-10-01) |
  | 네비게이션 | LoginKey, 진입 :app NavDisplay, 결과 없음 (R-13-01) |
  | 상태 아키텍처 | MVVM-UDF — R-12-02 매트릭스 MVI 해당 0/5 (전이 합법성·이탈 취소·전이 이력·순수 리듀서 테스트·부수효과 1급 모두 해당 없음) |
  | UseCase | 없음 — 정책 "로직이 있을 때만 생성" (R-16-02) |
  | 테스트 | LoginViewModelTest 4건 통과, LoginScreenshotTest 2건 (R-30-01, R-30-03) |
  | CI | 변경 없음 |
  | 어긴 규칙 | 없음 |
  ```
- 체크: 표의 8개 행이 모두 있는가. 각 결정에 규칙 ID가 붙어 있는가. "어긴 규칙"이 "없음"인데 실제로 우회한 규칙이 있지 않은가.

### R-32-02 승인 기준은 완벽함이 아니라 코드 건강도 개선이다
- 규칙: 리뷰어는 변경이 전체 코드 건강도를 확실히 개선하는 상태가 되면 승인한다. 완벽한 코드를 요구해 머지를 무한정 미루지 않고, 남은 개선은 후속 이슈로 넘긴다.
- 근거: "reviewers should favor approving a CL once it is in a state where it definitely improves the overall code health of the system", "there is no such thing as 'perfect' code—there is only better code" [S39](https://google.github.io/eng-practices/review/reviewer/standard.html).
- 예시:
  ```markdown
  <!-- Good -->
  LGTM. 네이밍은 후속 PR에서 정리하죠(이슈 #124 남김).
  <!-- Bad -->
  이 정도로는 머지 못 합니다. 전부 다시 써 주세요.
  ```
- 체크: 승인 보류 사유가 코드 건강도 저하인가, 취향 차이인가. 후속으로 미룰 수 있는 지적이 머지를 막고 있지 않은가.

### R-32-03 선택적 지적은 `Nit:`을 붙이고 머지를 막지 않는다
- 규칙: 반드시 고쳐야 하는 것이 아닌 제안에는 `Nit:` 접두를 붙인다. `Nit:` 코멘트만 남은 PR은 승인한다.
- 근거: 선택적 의견에 `Nit:` 접두를 붙여 필수와 구분하도록 안내한다 [S39](https://google.github.io/eng-practices/review/reviewer/standard.html).
- 예시:
  ```markdown
  <!-- Good -->
  Nit: 이 변수는 `isVisible` 이 더 읽기 쉬울 것 같아요.
  <!-- Bad -->
  이 변수 이름 바꾸기 전엔 승인 못 합니다.
  ```
- 체크: 필수 지적과 선택 지적이 구분돼 있는가. `Nit:`만 남았는데 승인이 보류돼 있지 않은가.

### R-32-04 포맷·스타일 지적은 리뷰 코멘트가 아니라 도구 설정으로 해결한다
- 규칙: 들여쓰기·줄 길이·import 정리 같은 기계 판정 가능한 사항은 리뷰에서 지적하지 않는다. 규칙을 바꾸고 싶으면 `.editorconfig`·detekt 설정을 바꾸는 PR을 따로 낸다. 도구가 통과시킨 코드를 사람이 스타일 이유로 되돌리지 않는다.
- 근거: 스타일 문제는 스타일 가이드가 최종 권위이며 가이드에 없는 것은 개인 취향의 문제다 [S39](https://google.github.io/eng-practices/review/reviewer/standard.html). ktlint는 `.editorconfig`로 코드 스타일과 규칙 활성화를 관리하고 [S84](https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/configuration-ktlint.md), 스타일 프리셋 자체를 문서가 정의한다 [S83](https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/code-styles.md).
- 예시:
  ```markdown
  <!-- Good -->
  이 규칙을 팀 전체에 적용하고 싶어서 .editorconfig PR 을 따로 올렸습니다(#131).
  <!-- Bad -->
  여기 줄바꿈 위치가 마음에 안 듭니다. 고쳐 주세요.
  ```
- 체크: 리뷰 코멘트 중 린터가 잡을 수 있는 것이 있는가. 있다면 설정 변경 PR로 옮겼는가.

### R-32-05 동등하게 타당한 설계 대안이면 작성자 선택을 존중한다
- 규칙: 리뷰어의 방식과 작성자의 방식이 모두 타당하면 작성자 선택을 받아들인다. 다른 방식을 요구하려면 왜 더 나은지 공학적 근거를 제시한다.
- 근거: 설계는 공학 원칙 위에서 판단하고, 대안이 동등하게 타당하면 작성자 선택을 존중한다는 원칙이다 [S39](https://google.github.io/eng-practices/review/reviewer/standard.html).
- 예시:
  ```markdown
  <!-- Good -->
  두 방식 다 되지만 A 는 화면 회전 시 상태를 잃습니다(R-12-01). B 로 가는 게 좋겠습니다.
  <!-- Bad -->
  저는 이런 식으로 안 짜는데요.
  ```
- 체크: 변경 요구에 근거가 붙어 있는가. 취향 차이를 필수 지적으로 올리지 않았는가.

### R-32-06 지적에는 규칙 ID나 출처를 붙인다
- 규칙: 필수 지적에는 이 팩의 규칙 ID(`R-NN-MM`) 또는 공식 문서 링크를 함께 적는다. 팩에 규칙이 없는 사안이면 "규칙 없음, 제안"이라고 밝히고 `Nit:`으로 낸다.
- 근거: 리뷰 판단은 기술적 사실과 명문화된 가이드에 근거해야 한다 [S39](https://google.github.io/eng-practices/review/reviewer/standard.html). 규칙을 문서화하고 판정 근거로 삼는 방식이 조직 사례에서 확인된다 [S114](https://toss.tech/article/52631).
- 예시:
  ```markdown
  <!-- Good -->
  Composable 이 ViewModel 을 직접 받고 있습니다(R-00-01).
  <!-- Bad -->
  이건 원래 이렇게 하는 게 맞아요.
  ```
- 체크: 필수 지적에 근거가 있는가. 팩에 없는 규칙을 필수처럼 요구하고 있지 않은가.

### R-32-07 PR 본문은 고정 템플릿을 채운다
- 규칙: PR 본문은 아래 템플릿의 네 섹션(변경 요약·표준 준수 보고·검증·위험)을 모두 채운다. 검증 섹션에는 실제로 돌린 Gradle 태스크와 결과를 적고, 돌리지 않았으면 돌리지 않았다고 적는다.
- 근거: 리뷰어가 코드 건강도를 판단하려면 변경 의도와 검증 상태를 알아야 한다 [S39](https://google.github.io/eng-practices/review/reviewer/standard.html). 게이트 태스크 목록은 R-31-01과 같다 [S56](https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml).
- 예시:
  ```markdown
  <!-- Good -->
  ## 검증
  - ./gradlew ktlintCheck detektDebug testDebugUnitTest — 통과
  - verifyRoborazziDebug — 기준 이미지 2건 갱신(로그인 버튼 색 변경)
  <!-- Bad -->
  ## 검증
  - 테스트 통과
  ```
- 체크: 네 섹션이 모두 채워졌는가. 검증 섹션의 태스크 이름과 결과가 구체적인가.

## 리뷰 체크리스트

PR을 열 때 작성자가 먼저 훑고, 리뷰어가 같은 순서로 본다. 각 항목은 해당 규칙 문서가 판정 기준을 갖는다.

| # | 확인 | 규칙 |
|---|---|---|
| 1 | 상태는 아래로, 이벤트는 위로만 흐르는가 | R-00-01 |
| 2 | 계층 의존 방향을 지켰는가(UI → Domain → Data) | R-11-01 |
| 3 | 계층 간 통신이 인터페이스 경계를 지나는가 | R-11-02 |
| 4 | 화면 상태가 불변 UiState 한 덩어리로 노출되는가 | R-12-01 |
| 5 | 상태 아키텍처 선택이 결정 매트릭스로 설명되는가 | R-12-02 |
| 6 | NavKey·진입점·결과 전달 방식이 규칙대로인가 | R-13-01 |
| 7 | 의존성이 생성자 주입과 Hilt 모듈 규칙을 따르는가 | R-14-01 |
| 8 | UseCase 생성 여부가 정책("로직이 있을 때만")에 맞는가 | R-16-02 |
| 9 | Composable 시그니처·호이스팅 규칙을 지켰는가 | R-17-01 |
| 10 | 새 ViewModel에 단위 테스트가 있는가 | R-30-01 |
| 11 | 새 Screen에 스크린샷 테스트가 있는가 | R-30-03 |
| 12 | CI 게이트 네 단계가 통과했는가 | R-31-01 |

## PR 템플릿 본문

`.github/pull_request_template.md`에 그대로 넣는다.

```markdown
## 변경 요약
<!-- 무엇을 왜 바꿨는지 3줄 이내 -->

## 표준 준수 보고
| 항목 | 내용 |
|---|---|
| 요청 유형 | |
| 모듈 위치 | |
| 네비게이션 | |
| 상태 아키텍처 | |
| UseCase | |
| 테스트 | |
| CI | |
| 어긴 규칙 | |

## 검증
- [ ] `./gradlew ktlintCheck`
- [ ] `./gradlew detektDebug`
- [ ] `./gradlew testDebugUnitTest verifyRoborazziDebug`
- [ ] `./gradlew assembleDebug`
<!-- 돌리지 않은 항목은 체크를 비우고 이유를 적는다 -->

## 위험
<!-- 되돌리기 방법, 영향 범위, 후속 이슈 번호 -->
```
