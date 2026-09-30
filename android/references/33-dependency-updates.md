# 33 의존성 자동 업데이트

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

버전을 어디에 적는지는 R-10-12(버전 카탈로그 한 곳), 액션 고정 방식은 R-31-04가 정한다. 이 문서는 그 버전들을 누가 언제 어떻게 올리는지만 정한다.

확정 결정 `DEP_UPDATE`(2026-09-30): 도구는 Dependabot, 자동 병합은 하지 않는다. 후보 비교는 `research/dependency-updates.md`.

## 결정 매트릭스 — 갱신 도구

| 판단 기준 | 선택지 A: Dependabot | 선택지 B: Renovate | 기본값 |
|---|---|---|---|
| 버전 카탈로그 | `gradle/libs.versions.toml` 표준 위치를 문서로 지원 [S178](https://docs.github.com/en/code-security/reference/supply-chain-security/supported-ecosystems-and-repositories) | 지원 | A |
| Gradle 래퍼 갱신 | Gradle을 실행해 래퍼 파일 4개를 갱신한다고 문서가 보장 [S178](https://docs.github.com/en/code-security/reference/supply-chain-security/supported-ecosystems-and-repositories) | v43부터 `gradlew` 실행이 기본 차단, 무료 호스팅 앱의 허용 여부는 문서에 없음 | A |
| 비공개 코드 접근 | GitHub 내장 기능 | 제3자(Mend) GitHub 앱 설치 | A |
| 자동 병합 | 내장 없음 | 내장 | 자동 병합을 하지 않으므로(R-33-02) 차이가 없다 |

## 규칙

### R-33-01 버전 갱신은 Dependabot이 여는 PR로 받는다
- 규칙: 저장소에 `.github/dependabot.yml`을 두고 `gradle`과 `github-actions` 두 생태계를 등록한다. 라이브러리·플러그인·Gradle 래퍼·액션 버전을 여러 개 모아 손으로 한 번에 올리지 않는다. 버전 카탈로그는 표준 위치 `gradle/libs.versions.toml` 하나만 둔다(R-10-12).
- 근거: Dependabot이 Gradle을 실행하지 않고 갱신하는 파일 목록에 "gradle/libs.versions.toml (for projects using a standard Gradle version catalog)"이 있고, 래퍼는 "To update the Gradle Wrapper, Dependabot runs Gradle and updates:" 뒤의 네 파일을 갱신한다 [S178](https://docs.github.com/en/code-security/reference/supply-chain-security/supported-ecosystems-and-repositories). `schedule.interval`은 필수 항목이다 [S179](https://docs.github.com/en/code-security/reference/supply-chain-security/dependabot-options-reference). 주기를 `weekly`로 둔 것은 출처가 정하지 않은 팩 선택이다.
- 예시:
  ```yaml
  # Good — .github/dependabot.yml
  version: 2
  updates:
    - package-ecosystem: "gradle"
      directory: "/"
      schedule: { interval: "weekly" }
    - package-ecosystem: "github-actions"
      directory: "/"
      schedule: { interval: "weekly" }
  # Bad: 분기마다 카탈로그의 버전 20개를 한 커밋으로 올린다
  ```
- 체크: `dependabot.yml`에 두 생태계가 모두 있는가. 카탈로그가 표준 위치 밖에 있지 않은가.

### R-33-02 업데이트 PR은 게이트를 통과한 뒤 사람이 병합한다
- 규칙: Dependabot PR을 자동으로 병합하지 않는다. `gh pr merge --auto`를 부르는 워크플로를 만들지 않는다. R-31-01의 네 게이트가 모두 통과한 PR만 병합하고, 실패한 PR은 고치거나 닫는다. 게이트는 PR 브랜치를 받아 `scripts/check.sh`로 돌린다(R-31-18). PR 워크플로를 쓰는 프로젝트는 그 워크플로가 시크릿 없이 돌아야 한다.
- 근거: Dependabot이 일으킨 워크플로는 Actions 시크릿을 읽지 못한다 — "the only secrets available to the workflow are Dependabot secrets" [S180](https://docs.github.com/en/code-security/reference/supply-chain-security/troubleshoot-dependabot/dependabot-on-actions). 동시에 열리는 버전 업데이트 PR은 다섯 개까지라 병합하거나 닫아야 다음 PR이 열린다 [S179](https://docs.github.com/en/code-security/reference/supply-chain-security/dependabot-options-reference). 자동 병합을 하지 않는다는 것은 팩 결정(`DEP_UPDATE`)이다.
- 예시:
  ```yaml
  # Good: gh pr checkout <번호> && scripts/check.sh 가 통과한 PR 을 사람이 병합한다
  # Bad: Dependabot PR 을 자동 병합하는 워크플로
  - run: gh pr merge --auto --merge "$PR_URL"
  ```
- 체크: 자동 병합 워크플로가 있는가. 병합한 PR마다 게이트를 돌렸는가. PR 게이트 워크플로가 있다면 `secrets.*`를 읽는가.

### R-33-03 모든 업데이트를 한 PR로 묶지 않는다
- 규칙: `groups`에 모든 의존성을 받는 패턴(`patterns: ["*"]`)을 쓰지 않는다. 묶음은 함께 올라가야 하는 항목에만 만들고 이유를 `dependabot.yml`의 주석으로 남긴다.
- 근거: "Grouping dependencies increases the chance that the branch has an error" 이고 "When you upgrade multiple dependencies in one PR, it takes longer to find out which package broke the build" [S181](https://docs.renovatebot.com/noise-reduction/). 이 문장은 Renovate 문서의 것이지만 묶음의 위험은 도구와 무관하다. 출처는 위험을 설명할 뿐이고, 묶음을 함께 올라가야 하는 항목으로 제한하고 주석을 요구하는 것은 팩 선택이다. NiA는 전체를 한 PR로 묶는 설정을 쓰고 그 PR이 2025-01부터 병합되지 않은 채 열려 있다(`research/dependency-updates.md`).
- 예시:
  ```yaml
  # Bad
  groups:
    all:
      patterns: ["*"]
  ```
- 체크: 전체를 받는 묶음이 있는가. 묶음마다 이유가 적혀 있는가.

### R-33-04 함께 올라가야 하는 버전은 버전 키 하나를 공유한다
- 규칙: Compose 컴파일러 플러그인은 `version.ref = "kotlin"`으로 Kotlin과 같은 키를 쓴다. Compose BOM이 관리하는 라이브러리는 BOM으로만 버전을 정하고 개별 버전을 적지 않는다. 같이 움직이는 항목에 버전 키를 따로 만들지 않는다.
- 근거: "the Compose compiler ships simultaneously with Kotlin and will always be compatible with Kotlin of the same version" [S183](https://kotlinlang.org/docs/compose-compiler-migration-guide.html). "When you update the BOM version, all the libraries that you're using are automatically updated to their new versions." [S182](https://developer.android.com/develop/ui/compose/bom). 키가 하나면 업데이트 PR도 한 줄만 바꾼다.
- 예시:
  ```toml
  # Good
  kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
  androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
  # Bad: 컴파일러 플러그인에 별도 키를 두어 Kotlin 과 따로 올라간다
  kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "composeCompiler" }
  ```
- 체크: BOM이 관리하는 Compose 라이브러리에 개별 버전이 적혀 있는가. Kotlin 계열 플러그인이 같은 키를 쓰는가.

### R-33-05 AGP·Gradle·Kotlin 업데이트 PR은 호환 표를 확인하고 병합한다
- 규칙: AGP를 올리는 PR은 AGP 릴리스 노트의 Gradle 최소 버전을, Kotlin을 올리는 PR은 Kotlin Gradle 플러그인 호환 표의 Gradle·AGP 범위를 확인한 뒤 병합한다. 표의 범위를 벗어나는 조합이면 PR 본문에 그 사실을 적고 네 게이트 통과로 확인한다.
- 근거: AGP 릴리스 노트가 버전마다 Gradle 최소 버전을 정한다 [S46](https://developer.android.com/build/releases/gradle-plugin). Kotlin 문서는 표 밖의 조합에 대해 "You can also use Gradle and AGP versions up to the latest releases, but if you do, keep in mind that you might encounter deprecation warnings or some new features might not work." 라고 한다 [S184](https://kotlinlang.org/docs/gradle-configure-project.html).
- 예시:
  ```text
  # Good: "AGP 9.4.0 → 9.5.0. 릴리스 노트의 Gradle 최소 버전 확인, 래퍼 PR 을 먼저 병합"
  # Bad: AGP PR 을 게이트 통과만 보고 병합해 래퍼가 최소 버전 아래로 남는다
  ```
- 체크: AGP·Kotlin·Gradle PR의 본문에 확인한 표가 적혀 있는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **Dependabot의 프리릴리스 기본 동작**: Gradle 한정자(alpha·beta·rc)를 인식한다는 설명은 있지만 기본으로 건너뛴다는 문장이 없다. 안정 버전을 쓰는 항목에 프리릴리스 PR이 열리는지는 첫 실행에서 확인한다.
- **included build(`build-logic`) 안의 의존성**: 두 도구 모두 문서가 언급하지 않는다. 팩은 버전을 카탈로그에만 두므로(R-10-12) `build-logic`에 버전 문자열이 없어야 한다.
- **Kotlin과 KSP를 한 PR로 묶을지**: KSP는 2.3.0부터 Kotlin과 버전을 분리했고 호환 범위 표는 없다. 묶음 규칙을 만들지 않는다.
- **Dependabot의 실행 비용**: Dependabot 자체의 실행은 요금제 포함 분량을 쓰지 않는다고 GitHub 문서가 적는다(`research/dependency-updates.md`). 분량을 쓰는 것은 그 PR이 일으키는 PR 워크플로이고, 워크플로를 넣지 않은 프로젝트(R-31-18)에는 해당이 없다.
- **업데이트 주기와 PR 개수 상한**: 권장값을 말하는 출처가 없다. 템플릿의 `weekly`와 Dependabot 기본값(동시 5개)을 그대로 쓴다.
- **액션을 SHA로 고정할지**: GitHub 문서는 SHA 고정을 권하고 R-31-04는 메이저 태그 고정이다. 이 충돌은 R-31-04가 소유한다. 메이저 태그로 두면 액션 PR은 메이저가 바뀔 때만 열린다.
- **dependency verification·의존성 잠금·의존성 그래프 제출**: 채택하지 않았다. 앞의 둘은 업데이트마다 메타데이터 갱신이 따라붙고, 잠금 문서가 드는 주 용도는 이 팩이 금지하는 동적 버전이다.

## 확정 버전과 호환 표 (2026-09-30 확인)

Kotlin Gradle 플러그인 2.4.20의 호환 표 상한은 Gradle 9.7.0 · AGP 9.3.1이고 이 팩의 확정값은 Gradle 9.7.1 · AGP 9.4.0이다 [S184](https://kotlinlang.org/docs/gradle-configure-project.html). 2026-09-09 스크래치 실빌드는 이 조합으로 통과했다. R-33-05가 말하는 "표 밖 조합"의 실제 사례다.
