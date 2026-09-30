# 의존성 자동 업데이트 조사 노트
조사일: 2026-09-30

조사 방식: 아래 URL을 모두 직접 받아 읽었다(curl 원문 + GitHub API). 기억으로 쓴 내용은 없다.
인용부호 안의 영어 문장은 **해당 URL의 실제 페이지에 글자 그대로 있는지 스크립트로 재확인한 것**만 넣었다(비교 시 백틱·굵은 글씨 표시·연속 공백·둥근 따옴표만 정규화). 재확인하지 못한 내용은 인용부호 없이 "요약"이라고 적었다.
확정 전제: AGP 9.4.0 / Gradle 래퍼 9.7.1 / JDK 17 / Kotlin 2.4.20 / KSP 2.3.11 / Hilt 2.60.1, 버전은 `gradle/libs.versions.toml` 한 곳(R-10-12), `build-logic` included build, GitHub Actions 메이저 태그 고정(R-31-04), GitHub 비공개 저장소, 단일 개발자.

## 출처
| # | 조직 | 문서명 | URL | 종류 | 최종 갱신/버전 |
|---|---|---|---|---|---|
| S1 | Mend(Renovate) | Java Versions — Gradle·Gradle Wrapper 지원 범위 | https://docs.renovatebot.com/java/ | 공식 문서 | 미표기 |
| S2 | Mend(Renovate) | Gradle manager | https://docs.renovatebot.com/modules/manager/gradle/ | 공식 문서 | 미표기 |
| S3 | Mend(Renovate) | Gradle Wrapper manager | https://docs.renovatebot.com/modules/manager/gradle-wrapper/ | 공식 문서 | 미표기 |
| S4 | Mend(Renovate) | GitHub Actions manager | https://docs.renovatebot.com/modules/manager/github-actions/ | 공식 문서 | 미표기 |
| S5 | Mend(Renovate) | Maven datasource | https://docs.renovatebot.com/modules/datasource/maven/ | 공식 문서 | 미표기 |
| S6 | Mend(Renovate) | Automerge configuration and troubleshooting | https://docs.renovatebot.com/key-concepts/automerge/ | 공식 문서 | 미표기 |
| S7 | Mend(Renovate) | Configuration Options | https://docs.renovatebot.com/configuration-options/ | 공식 문서 | 미표기 |
| S8 | Mend(Renovate) | Noise Reduction | https://docs.renovatebot.com/noise-reduction/ | 공식 문서 | 미표기 |
| S9 | Mend(Renovate) | Upgrade best practices / Minimum release age | https://docs.renovatebot.com/upgrade-best-practices/ , https://docs.renovatebot.com/key-concepts/minimum-release-age/ | 공식 문서 | 미표기 |
| S10 | Mend(Renovate) | 프리셋·옵션 기본값 소스(`config`·`default`·`group`·`schedule`·`workarounds` preset, `monorepo.json`, `options/index.ts`) | https://github.com/renovatebot/renovate/tree/main/lib/config/presets/internal , https://raw.githubusercontent.com/renovatebot/renovate/main/lib/data/monorepo.json , https://raw.githubusercontent.com/renovatebot/renovate/main/lib/config/options/index.ts | 공식 저장소 소스 | main |
| S11 | Mend(Renovate) | Self-hosted configuration / Security and Permissions | https://docs.renovatebot.com/self-hosted-configuration/ , https://docs.renovatebot.com/security-and-permissions/ | 공식 문서 | 미표기 |
| S12 | Mend(Renovate) | Mend-hosted: Overview / Environment Variables / Apps Configuration / Job Scheduling | https://docs.renovatebot.com/mend-hosted/overview/ , https://docs.renovatebot.com/mend-hosted/environment-variables/ , https://docs.renovatebot.com/mend-hosted/hosted-apps-config/ , https://docs.renovatebot.com/mend-hosted/job-scheduling/ | 공식 문서 | 미표기 |
| S13 | Mend(Renovate) | Installing and onboarding / Running Renovate | https://docs.renovatebot.com/getting-started/installing-onboarding/ , https://docs.renovatebot.com/getting-started/running/ | 공식 문서 | 미표기 |
| S14 | Mend(Renovate) | Release 43.0.0 / Releases | https://github.com/renovatebot/renovate/releases/tag/43.0.0 , https://github.com/renovatebot/renovate/releases | 릴리스 노트 | 43.0.0(2026-01-29) / 최신 44.121.4(2026-09-29) |
| S15 | Mend(Renovate) | Gradle manager 소스(`index.ts`, `extract/catalog.ts`), Gradle Wrapper `artifacts.ts` | https://github.com/renovatebot/renovate/tree/main/lib/modules/manager/gradle , https://github.com/renovatebot/renovate/blob/main/lib/modules/manager/gradle-wrapper/artifacts.ts | 공식 저장소 소스 | main |
| S16 | Renovate 사용자·메인테이너 | Discussions #46534, #44189, #41492, #40790 | https://github.com/renovatebot/renovate/discussions/46534 , https://github.com/renovatebot/renovate/discussions/44189 , https://github.com/renovatebot/renovate/discussions/41492 , https://github.com/renovatebot/renovate/discussions/40790 | 커뮤니티 토론(보조) | 2026-01 ~ 2026-09 |
| S17 | GitHub | Dependabot supported ecosystems and repositories | https://docs.github.com/en/code-security/reference/supply-chain-security/supported-ecosystems-and-repositories | 공식 문서 | 미표기 |
| S18 | GitHub | Dependabot options reference | https://docs.github.com/en/code-security/reference/supply-chain-security/dependabot-options-reference | 공식 문서 | 미표기 |
| S19 | GitHub | Dependabot version updates / Optimizing the creation of pull requests / Keeping your actions up to date | https://docs.github.com/en/code-security/concepts/supply-chain-security/dependabot-version-updates , https://docs.github.com/en/code-security/tutorials/secure-your-dependencies/optimizing-pr-creation-version-updates , https://docs.github.com/en/code-security/how-tos/secure-your-supply-chain/secure-your-dependencies/auto-update-actions | 공식 문서 | 미표기 |
| S20 | GitHub | Automating Dependabot with GitHub Actions | https://docs.github.com/en/code-security/tutorials/secure-your-dependencies/automate-dependabot-with-actions | 공식 문서 | 미표기 |
| S21 | GitHub | Dependabot on GitHub Actions runners / Troubleshooting Dependabot on GitHub Actions | https://docs.github.com/en/code-security/concepts/supply-chain-security/dependabot-on-actions , https://docs.github.com/en/code-security/reference/supply-chain-security/troubleshoot-dependabot/dependabot-on-actions | 공식 문서 | 미표기 |
| S22 | GitHub | dependabot-core Gradle 소스(`file_parser.rb`, `file_fetcher.rb`) / Releases | https://github.com/dependabot/dependabot-core/tree/main/gradle/lib/dependabot/gradle , https://github.com/dependabot/dependabot-core/releases | 공식 저장소 소스·릴리스 노트 | v0.398.0(2026-09-28) |
| S23 | GitHub | Automatically merging a pull request / Managing auto-merge for pull requests in your repository | https://docs.github.com/en/pull-requests/how-tos/merge-and-close-pull-requests/automatically-merging-a-pull-request , https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/configuring-pull-request-merges/managing-auto-merge-for-pull-requests-in-your-repository | 공식 문서 | 미표기 |
| S24 | GitHub | About protected branches / About rulesets | https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches , https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-rulesets/about-rulesets | 공식 문서 | 미표기 |
| S25 | GitHub | GitHub security features | https://docs.github.com/en/code-security/getting-started/github-security-features | 공식 문서 | 미표기 |
| S26 | GitHub | Dependency graph / Dependency graph supported package ecosystems | https://docs.github.com/en/code-security/concepts/supply-chain-security/dependency-graph , https://docs.github.com/en/code-security/reference/supply-chain-security/dependency-graph-supported-package-ecosystems | 공식 문서 | 미표기 |
| S27 | GitHub | Dependabot alerts | https://docs.github.com/en/code-security/concepts/supply-chain-security/dependabot-alerts | 공식 문서 | 미표기 |
| S28 | GitHub | Using the dependency submission API / Configuring automatic dependency submission | https://docs.github.com/en/code-security/how-tos/secure-your-supply-chain/secure-your-dependencies/use-dependency-submission-api , https://docs.github.com/en/code-security/how-tos/secure-your-supply-chain/secure-your-dependencies/submit-dependencies-automatically | 공식 문서 | 미표기 |
| S29 | GitHub | Secure use reference(액션 고정) | https://docs.github.com/en/actions/reference/security/secure-use | 공식 문서 | 미표기 |
| S30 | GitHub | GitHub Actions billing | https://docs.github.com/en/billing/concepts/product-billing/github-actions | 공식 문서 | 미표기 |
| S31 | Gradle | `gradle/actions` dependency-submission 문서 / Releases | https://raw.githubusercontent.com/gradle/actions/main/docs/dependency-submission.md , https://github.com/gradle/actions/releases | 공식 저장소 문서·릴리스 노트 | v6.4.0(2026-09-28) |
| S32 | Gradle | Verifying Dependencies | https://docs.gradle.org/current/userguide/dependency_verification.html | 공식 문서 | 9.8.0 |
| S33 | Gradle | Locking Versions | https://docs.gradle.org/current/userguide/dependency_locking.html | 공식 문서 | 9.8.0 |
| S34 | Gradle | Version Catalogs | https://docs.gradle.org/current/userguide/version_catalogs.html | 공식 문서 | 9.8.0 |
| S35 | Gradle | Gradle Wrapper / 현재 버전 API | https://docs.gradle.org/current/userguide/gradle_wrapper.html , https://services.gradle.org/versions/current | 공식 문서 | 9.8.0(빌드 2026-09-24) |
| S36 | Google | Add build dependencies | https://developer.android.com/build/dependencies | 공식 가이드 | 2026-09-16 |
| S37 | Google | Dependency verification(Android) | https://developer.android.com/build/dependency-verification | 공식 가이드 | 2026-02-26 |
| S38 | Google | Compose BOM / Compose Compiler Gradle plugin | https://developer.android.com/develop/ui/compose/bom , https://developer.android.com/develop/ui/compose/compiler | 공식 가이드 | 후자 2026-09-16 |
| S39 | Google | AGP 9.4.0 릴리스 노트 / AGP·D8·R8 versions required for Kotlin versions | https://developer.android.com/build/releases/gradle-plugin , https://developer.android.com/build/kotlin-support | 공식 문서 | 2026-09-24 / 2026-07-06 |
| S40 | JetBrains | Configure a Gradle project(KGP 호환 표) / Compose compiler migration guide | https://kotlinlang.org/docs/gradle-configure-project.html , https://kotlinlang.org/docs/compose-compiler-migration-guide.html | 공식 문서 | 후자 2026-08-12 |
| S41 | JetBrains / Google | KSP quickstart / KSP Releases | https://kotlinlang.org/docs/ksp-quickstart.html , https://github.com/google/ksp/releases | 공식 문서·릴리스 노트 | 2.3.12(2026-09-09) |
| S42 | Google | Dagger KSP 가이드 / Dagger Releases | https://dagger.dev/dev-guide/ksp , https://github.com/google/dagger/releases | 공식 문서·릴리스 노트 | 2.60.1(2026-07-06) |
| S43 | ben-manes | gradle-versions-plugin README / Releases | https://raw.githubusercontent.com/ben-manes/gradle-versions-plugin/master/README.md , https://github.com/ben-manes/gradle-versions-plugin/releases | 오픈소스 README·릴리스 노트 | v0.64.0(2026-09-17) |
| S44 | littlerobots | version-catalog-update-plugin README / Releases | https://raw.githubusercontent.com/littlerobots/version-catalog-update-plugin/main/README.md , https://github.com/littlerobots/version-catalog-update-plugin/releases | 오픈소스 README·릴리스 노트 | v1.1.1(2026-08-01) |
| S45 | Google | Now in Android `.github/renovate.json`, `settings.gradle.kts`, `Build.yaml`, 과거 `.github/dependabot.yml`, PR #1803 | https://raw.githubusercontent.com/android/nowinandroid/main/.github/renovate.json , https://raw.githubusercontent.com/android/nowinandroid/main/settings.gradle.kts , https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml , https://raw.githubusercontent.com/android/nowinandroid/85129e4660f7a27c7081f4ac21169d19db89fbb6/.github/dependabot.yml , https://github.com/android/nowinandroid/pull/1803 | 오픈소스 코드 | main(최근 커밋 2026-09-22) |
| S46 | Google | `android/.github` 공유 Renovate 프리셋 `renovate-config.json` | https://github.com/android/.github/blob/master/renovate-config.json | 오픈소스 코드 | master(2024-12-10) |

## 핵심 내용 (출처별)

### 질문 1. 도구 후보

#### 1-a. Renovate
- **갱신 대상**: "This includes libraries and plugins as well as the Gradle Wrapper." [S1]
- **버전 카탈로그**: 갱신 가능한 파일 목록에 "`*.versions.toml` files in any directory or `*.toml` files inside the `gradle` directory"가 들어 있다. [S1] 지원하지 않는 것으로 "Catalogs with version ranges", `reject`·`rejectAll`, 한 선언에 `require`·`strictly`·`prefer`를 둘 이상 쓴 경우, 이름이 `.toml`로 끝나지 않는 카탈로그를 든다. [S1]
- **`[versions]` 참조(version.ref)·`[plugins]`**: 문서 문장으로는 확인하지 못했다. 공식 저장소 소스 `extract/catalog.ts`가 `tomlContent.versions`·`libraries`·`plugins`를 읽고 `version.ref`를 공유 변수로 처리하며, 플러그인은 `depType: 'plugin'`, 패키지 이름 `<pluginId>:<pluginId>.gradle.plugin`으로 만든다(요약, 소스 코드 확인). [S15]
- **파싱 방식**: "It does not call gradle directly in order to extract a list of dependencies." 자체 파서를 쓴다. [S2]
- **included build(`build-logic`)**: 문서가 included build를 직접 언급하지 않는다. 기본 파일 패턴은 `\.gradle(\.kts)?$`, `gradle.properties`, `gradle/.+\.toml`, `buildSrc/.+\.kt`, `\.versions\.toml$` 이다(요약, 소스). [S15] 즉 `build-logic/**/*.gradle.kts` 는 패턴에 걸리지만 `build-logic` 안의 `.kt` 소스는 기본 패턴에 없다. 팩은 버전을 카탈로그에만 두므로(R-10-12) `build-logic`의 `.kt`에 버전 문자열이 없다면 영향이 없다.
- **BOM**: Renovate 문서에 BOM 전용 설명은 없다. 카탈로그의 BOM 항목은 일반 라이브러리 좌표로 취급된다고 볼 수 있으나 문서 문장은 없다 → "출처가 침묵".
- **플러그인 조회 제약**: "Dependencies that appear to be Gradle plugins (where the groupId or artifactId ends with `.gradle.plugin`) will **not** be looked up in Maven Central." [S5] `pluginManagement` 저장소가 `mavenCentral()`뿐이면 Kotlin·KSP·Hilt·detekt 플러그인 조회가 실패한다는 사용자 보고가 2026-09-28에 올라와 열려 있다. [S16 #46534] NiA는 `pluginManagement`에 `google()`·`mavenCentral()`·`gradlePluginPortal()` 셋을 모두 둔다. [S45]
- **Gradle 래퍼**: "Renovate will then invoke the Gradle Wrapper to update itself" — `gradle-wrapper.properties`, `gradlew`, `gradlew.bat`, `gradle-wrapper.jar`가 대상이다. [S1]
- **래퍼 실행 제한(v43부터)**: "Renovate will only execute the Gradle Wrapper (via `./gradlew` or `gradlew.bat`) if the self-hosted administrator configures `allowedUnsafeExecutions` to include the `gradleWrapper` option." [S2] 릴리스 노트는 "As of Renovate 43, this long-standing risk is disabled by default to make Renovate more "secure by default"."라고 적는다. [S14] 옵션 기본값은 빈 배열이고 전역 전용이라 저장소 설정으로는 켤 수 없다(요약, 소스). [S10] Mend 호스티드 문서는 `RENOVATE_ALLOWED_UNSAFE_EXECUTIONS`를 유료(Enterprise·AppSec) 사용자가 조정할 수 있는 변수 표에 넣고, 무료 사용자는 "Community (Free) users can control anything that is repository config, via environment variables."라고만 한다. [S12] **무료 호스티드 앱이 래퍼 실행을 기본으로 허용하는지는 문서에 없다.**
- **Android 관련 제한**: 지원하지 않는 것에 "Android projects that require extra configuration to run (e.g. setting the Android SDK)"가 있다. [S1] 호스티드 앱에서 NDK 라이선스 미동의로 lockfile·verification 갱신이 실패한 보고가 있다. [S16 #44189]
- **GitHub Actions 갱신**: `actions/checkout@v4`, `@v4.2.0` 같은 태그 참조를 갱신하고, 다이제스트 고정은 "If you want to automatically pin action digests add the `helpers:pinGitHubActionDigests` preset to the `extends` array". [S4]
- **묶음**: `packageRules`의 `groupName`으로 묶는다. 문서는 묶음의 부작용도 적는다 — "Grouping dependencies increases the chance that the branch has an error ("break" your build)", "When you upgrade multiple dependencies in one PR, it takes longer to find out which package broke the build". [S8] `monorepo.json`의 `repoGroups`에 `kotlin`, `ksp`, `kotlinx-coroutines`, `okhttp`, `retrofit`이 각각 따로 있고, `androidx`·`compose`·`dagger`·`hilt` 묶음은 없다(요약, 소스). [S10] 즉 Kotlin+KSP를 함께 묶거나 androidx를 한 PR로 묶으려면 `packageRules`를 직접 써야 한다.
- **일정**: `schedule` 옵션. "Renovate does not support scheduled minutes or "at an exact time" granularity." [S8] 프리셋 `schedule:weekly`는 `schedule:earlyMondays`(`* 0-3 * * 1`)를 확장한다(요약, 소스). [S10] 호스티드 무료 플랜은 활성 저장소를 4시간마다 돌린다(요약). [S12]
- **PR 개수 제한**: `prConcurrentLimit` 기본 10, `prHourlyLimit` 기본 2(요약, 소스). [S10] "Renovate always creates security PRs, even if the concurrent PR limit is already reached." [S7]
- **프리릴리스 제외**: "By default, Renovate won't update any package versions to unstable versions (e.g. `4.0.0-rc3`) unless the current version has the same `major.minor.patch` and was _already_ unstable (e.g. it was already on `4.0.0-rc2`)." [S7] `ignoreUnstable` 기본값 `true`(요약, 소스). [S10]
- **출시 후 대기**: `minimumReleaseAge`. Maven Central은 릴리스 시각을 제공하는 데이터소스 표에 들어 있다(요약). [S9] "Do _not_ use `minimumReleaseAge` to slow down fast releasing project updates." [S7]
- **설치·비용**: GitHub App(https://github.com/apps/renovate)을 저장소에 설치하면 온보딩 PR이 열리고, 그 PR을 병합하기 전에는 아무 변경도 하지 않는다(요약). [S13] 무료 플랜 설명은 "A generous free tier, available for all across an unlimited number of public and private repositories." [S12] 무료 플랜 자원은 동시 작업 1, 1 vCPU, 메모리 3GB, 작업 제한 30분(요약). [S12] 자체 호스팅은 npm 패키지, Docker 이미지, `renovatebot/github-action` 중에서 고른다(요약). [S13]
- **버전**: 최신 릴리스 44.121.4(2026-09-29 확인). 호스티드 앱은 "This means the Mend Renovate App can lag a few hours to a week behind the open source version." [S12][S14]
- **Gradle 9.x 관련**: Renovate는 Gradle을 실행하지 않고 파싱하므로 Gradle 버전에 묶이지 않는다. [S2] Gradle 9.x 전용 제약을 적은 공식 문서는 찾지 못했다.

#### 1-b. GitHub Dependabot
- **버전 카탈로그**: "Dependabot supports updates to the following files without needing to run Gradle:" 목록에 `build.gradle`·`build.gradle.kts`, "`gradle/libs.versions.toml` (for projects using a standard Gradle version catalog)", `gradle.lockfile`이 있다. [S17] 표준 위치 하나만 명시한다.
- **`[versions]` 참조·`[plugins]`·included build**: 문서는 침묵한다. 공식 저장소 소스 `file_parser.rb`가 카탈로그의 `libraries`와 `plugins`를 읽고 `version.ref` 형식을 처리하며, `file_fetcher.rb`가 `buildSrc`와 설정 파일의 included build 경로를 함께 가져온다(요약, 소스 코드 확인). [S22]
- **Gradle 래퍼**: "To update the Gradle Wrapper, Dependabot runs Gradle and updates:" 뒤에 `gradle-wrapper.properties`, `gradlew`, `gradlew.bat`, `gradle-wrapper.jar`를 든다. [S17]
- **GitHub Actions**: `package-ecosystem: "github-actions"`, `directory: "/"`. [S19] "Dependabot supports both public and private repositories for GitHub Actions." [S17]
- **묶음**: `groups`의 `patterns`·`exclude-patterns`·`update-types`. "If a dependency matches more than one rule, it's included in the first group that it matches." [S18] 생태계를 넘는 묶음은 `multi-ecosystem-groups`(요약). [S18]
- **일정**: `schedule.interval` 필수. "Use `weekly` to run once a week, by default on Monday." [S18]
- **PR 개수 제한**: "If five pull requests with version updates are open, no further pull requests are raised until some of those open requests are merged or closed." [S18]
- **출시 후 대기**: "Apply a **default cooldown period of 3 days** to version updates, even when `cooldown` is not configured." 보안 업데이트에는 적용되지 않는다. Gradle은 `default-days`와 SemVer별 일수 모두 지원(요약). [S18]
- **프리릴리스**: Gradle 한정자(`alpha`, `beta`, `milestone`, `rc`, `snapshot` 등)를 인식하고 "Free-form identifiers not in this list are treated as stable." [S18] **기본으로 프리릴리스를 건너뛰는지는 읽은 문서에 문장이 없다.** 제외하려면 `ignore`를 쓰라고만 한다(요약). [S18]
- **설치·비용**: "You enable Dependabot version updates by checking a `dependabot.yml` configuration file into your repository." [S19] 모든 플랜에서 제공된다 — "The following security features are available for you to use, regardless of the GitHub plan you are on." 목록에 Dependabot alerts·security updates·version updates가 들어 있다. [S25] "Running Dependabot on standard GitHub-hosted or self-hosted runners **does not** count towards your included GitHub Actions minutes." [S21]
- **봇 PR이 기존 워크플로를 돌릴 때의 제약**: "When a Dependabot event triggers a workflow, the only secrets available to the workflow are Dependabot secrets." [S21] 토큰도 읽기 전용이다(요약). [S21] PR 게이트가 시크릿을 요구하면 같은 이름으로 Dependabot 시크릿을 따로 등록해야 한다(요약). [S21]
- **버전**: dependabot-core v0.398.0(2026-09-28 확인). [S22]

#### 1-c. Gradle 플러그인 방식
- **ben-manes/gradle-versions-plugin**: 보고만 한다 — "plugin only reports—it never edits build files or the catalog." [S43] 카탈로그로 선언한 의존성도 보고에 포함하고 Gradle 자체 업데이트도 확인한다(요약). [S43] "An included build is a separate build with its own settings script, so its projects are not part of this build's report." 포함하려면 included build의 설정 스크립트에도 플러그인을 적용해야 한다. [S43] "The plugin requires Gradle 8.4 or later, checked when the plugin is applied." 설정 캐시·isolated projects 지원(요약). [S43] 플러그인 id는 `io.github.ben-manes.versions.settings`(설정 스크립트에 적용 권장). 최신 v0.64.0(2026-09-17 확인). [S43]
- **littlerobots/version-catalog-update-plugin**: `./gradlew versionCatalogUpdate`로 카탈로그를 직접 고친다. `versions`·`libraries`·`bundles`·`plugins` 키를 다루고 기존 `version.ref`를 유지한다(요약). [S44] "This plugin requires Gradle 7.2 or up." [S44] 기본 선택 규칙은 "By default, a stable version will be selected, based on the version name, unless the current version is already considered unstable and a newer version exists." [S44] README 스스로 "Updating all dependencies at once is without testing is generally not recommended."라고 하고 `--interactive` 모드를 권한다. [S44] 제약으로 "The TOML file will be updated and formatted by this plugin; this is by design." [S44] 최신 v1.1.1(2026-08-01 확인). Gradle 9.x 지원 여부는 README·릴리스 노트에 문장이 없다. [S44]
- **두 플러그인의 관계**: ben-manes README는 littlerobots 플러그인이 "this plugin's report"를 바탕으로 갱신한다고 소개하지만, littlerobots는 1.0.0(2025-03-28)부터 ben-manes 플러그인 없이 Gradle API로 버전을 해석한다고 적는다(요약). [S43][S44] 서로 어긋나며 littlerobots 쪽 설명이 자기 제품에 대한 것이므로 그쪽을 따른다.
- **공통 한계**: PR을 만들지 않고, Gradle 래퍼 파일과 GitHub Actions 버전을 고치지 않는다. 일정·자동 병합은 워크플로를 직접 짜야 한다(README에 해당 기능 없음). [S43][S44]

### 질문 2. Now in Android의 실제 구성
- `main`에 **`.github/renovate.json`이 있고 `.github/dependabot.yml`은 없다**(직접 확인, 404). [S45]
- `renovate.json` 내용은 `"extends": ["local>android/.github:renovate-config"]`, `"baseBranches": ["main"]`, `gitIgnoredAuthors` 3건뿐이다. 묶음 규칙은 공유 프리셋에 있다. [S45]
- 공유 프리셋 `android/.github`의 `renovate-config.json`: `"extends": ["config:base", "group:all", ":dependencyDashboard", "schedule:daily", ":automergeStableNonMajor"]`, `"onboarding": false`, `"requireConfig": "optional"`. [S46] `config:base`는 현재 `config:recommended`의 옛 이름이다(요약, 소스). [S10] `group:all`은 모든 업데이트를 한 PR로 묶고 `separateMajorMinor: false`다(요약, 소스). [S10]
- 공유 프리셋 마지막 커밋(2024-12-10)의 제목은 "Remove kotlin group", 본문은 "This is because compose compiler is part of kotlin starting 2.0"이다. [S46]
- NiA는 2024-12-17 커밋 "Add Renovate, remove Dependabot"으로 도구를 바꿨다. 그 직전 `dependabot.yml`은 gradle·github-actions 둘 다 `weekly`, `open-pull-requests-limit: 10`, 묶음 `kotlin-ksp`(패턴 `org.jetbrains.kotlin:*`, `org.jetbrains.kotlin.jvm`, `com.google.devtools.ksp`)였고, Kotlin 2.0 이전에는 같은 묶음에 `androidx.compose.compiler:compiler`가 들어 있었다. [S45]
- **운영 실태**: Renovate가 만든 "Update all dependencies" PR #1803은 2025-01-09에 열려 2026-09-30 현재도 열려 있다(봇이 계속 갱신 중). `actions/checkout v4 → v7` 같은 메이저 업데이트가 같은 PR에 섞여 있다. [S45] `main`의 `Build.yaml`도 `actions/checkout@v4`, `gradle/actions/setup-gradle@v4`에 머물러 있다. [S45] 모두 한 PR로 묶는 구성이 실제로는 병합되지 않고 쌓인 사례다.
- NiA는 `gradle/verification-metadata.xml`과 lockfile을 두지 않는다(`gradle/` 디렉터리에 `libs.versions.toml`, `wrapper`만 있음). 대신 CI에서 `dependencyGuard`로 의존성 목록 변화를 검사한다. [S45]

### 질문 3. 함께 올라가야 하는 버전 쌍
- **Kotlin ↔ Compose 컴파일러 플러그인**: "the Compose compiler ships simultaneously with Kotlin and will always be compatible with Kotlin of the same version." [S40] "As of Kotlin 2.0, the Compose compiler is managed alongside the Kotlin compiler and uses the same version as the Kotlin compiler." [S38] 공식 예시는 `compose-compiler` 플러그인을 `version.ref = "kotlin"`으로 적는다. [S38][S40] 같은 `version.ref`를 쓰면 한 줄만 바뀌므로 별도 묶음 규칙이 필요 없다.
- **Kotlin ↔ KSP**: KSP 2.3.0 릴리스 노트 — "KSP version is no longer tied to the Kotlin compiler version (moving away from the old <kotlinversion>-<kspversion> format)." [S41] 공식 퀵스타트는 Kotlin 2.4.20 + KSP 2.3.10 조합을 예시로 든다. [S41] **KSP 버전별로 지원하는 Kotlin 범위를 적은 호환 표는 찾지 못했다.** KSP 2.3.12는 "Update minimum supported Android Gradle Plugin (AGP) version to 8.12.0"을 포함한다. [S41]
- **KGP ↔ Gradle ↔ AGP**: "The Kotlin Gradle plugin (KGP) and Kotlin share the same version numbering." [S40] 호환 표에서 KGP 2.4.20의 완전 지원 범위는 Gradle 7.6.3–9.7.0, AGP 8.5.2–9.3.1이다(표 값 직접 확인). [S40] "You can also use Gradle and AGP versions up to the latest releases, but if you do, keep in mind that you might encounter deprecation warnings or some new features might not work." [S40] **팩의 AGP 9.4.0·Gradle 9.7.1은 이 표의 "완전 지원" 상한(9.3.1 / 9.7.0)보다 위다.**
- **AGP ↔ Gradle 최소 버전**: AGP 9.4는 Gradle 최소·기본 9.6.0, JDK 17, SDK Build Tools 36.0.0(표 값 직접 확인). "The maximum API level that Android Gradle plugin 9.4 supports is API level 37." [S39]
- **Kotlin ↔ AGP(R8)**: "The following table shows the minimum required versions of AGP, D8 and R8 for each Kotlin version." Kotlin 2.4는 AGP 8.5.2+ / R8 9.1.29(표 값 직접 확인). [S39]
- **Hilt ↔ KSP**: "Dagger's KSP support is stable as of Dagger 2.60+ and KSP 2.3.9+." [S42] `androidx.hilt:hilt-compiler`의 KSP 지원은 1.4.x(요약). [S42] Dagger 2.60 릴리스 노트는 자체 빌드 Kotlin을 2.3.21로 올렸다고 적는다(요약). [S42] **Hilt 2.60.1과 Kotlin 2.4.20의 호환을 명시한 문장은 찾지 못했다.**
- **Compose BOM**: "When you update the BOM version, all the libraries that you're using are automatically updated to their new versions." [S38] BOM 아래 라이브러리는 버전을 적지 않으므로 갱신 도구가 볼 값은 BOM 버전 하나다.

### 질문 4. 의존성 검증·보안
- **Gradle dependency verification**: 목적은 "Dependency verification is meant to protect yourself from compromised dependencies, not to prevent you from including vulnerable dependencies." [S32] 파일은 `gradle/verification-metadata.xml` 하나이고 "The dependency verification configuration is global; a single file is used to verify the entire build." [S32] included build는 "If an included build has its own verification metadata, that configuration is ignored in favor of the current build's settings." [S32]
- **버전 갱신과의 충돌**: "When you add a new library or update a version, the build will fail because the new checksum isn't in your XML file yet." [S32] Android 문서도 체크섬은 "they change with every release, requiring you to update `gradle/verification-metadata.xml` whenever you upgrade them."라고 적는다. [S37] 즉 검증을 켜면 자동 업데이트 PR마다 메타데이터 갱신이 따라붙어야 한다.
- **자동 생성의 한계**: 부트스트랩은 현재 저장소에 있는 것을 그대로 믿는다 — "This is why you must review the generated verification file." [S32] "Gradle cannot automatically determine that an entry is outdated." [S32]
- **Renovate와의 연동**: `verification-metadata.xml`이 있으면 `./gradlew --write-verification-metadata <hashTypes> dependencies`로 갱신하지만 "This requires your self-hosted administrator to allow the Gradle Wrapper to execute". [S2] 호스티드 앱에서 이 명령이 15분 제한에 걸려 실패한 보고가 있다. [S16 #41492]
- **Android 문서의 권고 수준**: "When adding dependencies, consider enabling Dependency verification" — "consider" 수준이다. [S36]
- **GitHub dependency graph**: "The dependency graph is a summary of the manifest and lock files stored in a repository and any dependencies that are submitted for the repository using the dependency submission API." [S26] 지원 표에서 Gradle 행은 정적 전이 의존성 "Not supported", Dependabot graph jobs "Not supported", Automatic dependency submission "Supported", 권장 파일 없음(표 값 직접 확인). [S26] Gradle은 매니페스트 정적 분석만으로는 그래프가 채워지지 않는다.
- **Dependabot alerts와 Gradle**: "For Dependabot security updates, Gradle support is limited to manual uploads of the dependency graph data using the dependency submission API." [S17] 전이 의존성에서 나온 경고는 Dependabot이 저장소에서 위치를 찾지 못해 보안 업데이트 PR을 만들지 않는다(요약). [S17] "For GitHub Actions, alerts are only generated for actions that use semantic versioning, not SHA versioning." [S27]
- **`gradle/actions/dependency-submission`**: "This action will attempt to detect all dependencies used by your build without building and testing the project itself." [S31] 예시 워크플로는 `main` 푸시에서 실행, `permissions: contents: write`, `gradle/actions/dependency-submission@v6`. [S31] v6의 기본 캐시 제공자는 "is currently available as a **Free Preview** for private repositories."이고, 오픈소스 쪽은 "and can be enabled at any time by setting `cache-provider: basic`."이다. [S31] 최신 v6.4.0(2026-09-28 확인). [S31]
- **자동 제출(설정만으로)**: 저장소 설정에서 "Automatic dependency submission"을 켜면 GitHub가 푸시를 감시해 제출한다. 조건은 dependency graph 활성화와 GitHub Actions 활성화(요약). [S28]
- **비공개 저장소 사용 조건**: dependency graph, Dependabot alerts·security updates·version updates는 플랜과 무관하게 제공된다. [S25] Dependency review는 GitHub Code Security 구매 대상이며 공개 저장소에서만 기본 제공된다(요약). [S25]
- **Renovate의 취약점 PR**: dependency graph와 Dependabot alerts를 켜고 앱에 읽기 권한을 주면 Renovate가 수정 PR을 만든다(요약). "There's a small chance that a wrong vulnerability alert results in a flapping/looping vulnerability fix." [S7]

### 질문 5. Gradle 의존성 잠금
- **용도**: "Using dynamic dependency versions (e.g., `1.+` or `[1.0,2.0)`) can cause builds to break unexpectedly because the exact version of a dependency that gets resolved can change over time:" → "To ensure reproducible builds, it's necessary to lock versions of dependencies and their transitive dependencies." [S33] 문서의 출발점은 동적 버전이다.
- **제한**: "Dependency locking is effective with dynamic versions, but it should not be used with changing versions (e.g., `-SNAPSHOT`), where the coordinates remain the same, but the content may change." [S33] "The above will lock all project configurations, but not the buildscript ones." [S33]
- **운영**: "Lockfiles should be checked in to source control." [S33] "This is why updating a direct dependency version without also updating the lock state can cause a build failure" — 버전을 올릴 때마다 lock 상태도 갱신해야 한다. [S33]
- **버전 카탈로그와의 관계**: 잠금 문서는 카탈로그를 언급하지 않는다. 카탈로그 문서는 "Version catalogs declare requested versions but do not enforce them."라고 한다. [S34] 카탈로그는 요청 버전을 적는 곳이고, 전이 의존성까지 실제 해석 결과를 고정하는 것은 잠금의 역할이다(두 문서를 합친 요약).
- **Android 쪽 입장**: "Caution: When specifying dependencies, you shouldn't use dynamic version numbers, such as `'com.android.tools.build:gradle:3.+'`." [S36] Android 문서는 잠금 사용을 권하지도 막지도 않는다. 동적 버전을 쓰지 않으면 잠금 문서가 말하는 주된 문제는 생기지 않는다.
- **도구 지원**: Renovate는 `gradle.lockfile`을 갱신하되 Gradle 실행이 필요하다(요약). [S2] Dependabot은 `gradle.lockfile`을 Gradle 실행 없이 갱신 대상으로 든다. [S17] Renovate 문서는 "The lowest risk type of update to automerge is probably `lockFileMaintenance`."라고 한다. [S6]

### 질문 6. 자동 병합의 안전 조건
- **Renovate 기본 동작**: "Renovate will wait for the required tests to pass before it automerges." [S6] "By default, Renovate will not automerge until it sees passing status checks / check runs for the branch." [S6] "Currently Renovate's default behavior is to only automerge if every status check has succeeded." [S7]
- **테스트 없이 병합 금지에 가까운 경고**: `ignoreTests`에 대해 "Beware: configuring Renovate to automerge without any tests can lead to broken builds on your base branch, please think again before enabling this!" [S7] "We strongly recommend you have tests in any project where you are regularly updating dependencies." [S6]
- **무엇을 자동 병합할지**: "Keep automerge _disabled_ for updates where you want to read the changelogs or code before the merge." [S6] "Usually you won't want to automerge _all_ PRs, for example most people would want to leave major dependency updates to a human to review first." [S7] 비메이저 자동 병합 예시는 `matchUpdateTypes: ["minor", "patch"]` + `matchCurrentVersion: "!/^0/"`(0.x 제외)다(요약). [S6]
- **출시 후 대기**: "If you `automerge` third-party dependencies, we recommend setting `minimumReleaseAge` to `"14 days"`." [S9]
- **브랜치 보호와의 관계(Renovate)**: "By default, Renovate uses platform-native automerge to speed up automerging." [S6] 이때 "If you use the default `platformAutomerge=true` then you should enable your Git hosting platform's capabilities to enforce test passing before PR merge." [S7] 플랫폼 기능을 쓸 수 없으면 "It falls back to Renovate-based automerge if the platform-native automerge is not available." [S7] 리뷰 필수 규칙이 있으면 "If you have mandatory Pull Request reviews then it means Renovate can't automerge its own PR until such a review has happened." [S6]
- **속도**: "As merging more than one branch in a row does not work _reliably_, Renovate will only automerge one branch/PR, per target branch, per run." [S6]
- **Dependabot**: 자동 병합 옵션이 `dependabot.yml`에 없다. 문서가 제시하는 방법은 `dependabot/fetch-metadata`로 업데이트 종류를 읽고 `gh pr merge --auto`를 실행하는 워크플로다(요약). [S20] 안전 조건은 "If you use status checks to test pull requests, you should enable **Require status checks to pass before merging** for the target branch for Dependabot pull requests." "This branch protection rule ensures that pull requests are not merged unless **all the required status checks pass**." [S20]
- **GitHub auto-merge 자체**: "Auto-merge merges a pull request automatically after all required reviews and status checks pass." [S23]
- **비공개 저장소에서의 제공 범위**: "Auto-merge for pull requests is available in public repositories with GitHub Free and GitHub Free for organizations, and in public and private repositories with GitHub Pro, GitHub Team, GitHub Enterprise Cloud, and GitHub Enterprise Server." [S23] "Protected branches are available in public repositories with GitHub Free and GitHub Free for organizations." "Protected branches are also available in public and private repositories with GitHub Pro, GitHub Team, GitHub Enterprise Cloud, and GitHub Enterprise Server." [S24] Rulesets도 같은 범위다. [S24] **GitHub Free 플랜의 비공개 저장소에서는 auto-merge·브랜치 보호·rulesets를 쓸 수 없다.** 이 경우 Dependabot의 `gh pr merge --auto` 방식은 성립하지 않고, Renovate는 자체 자동 병합으로 넘어간다.
- **CI 비용**: "For **private repositories**, each GitHub account receives a quota of free minutes, artifact storage, and cache storage for use with GitHub-hosted runners, depending on the account's plan." GitHub Free 2,000분/월, GitHub Pro 3,000분/월(표 값 직접 확인). [S30] 무료인 것은 Dependabot 자체 실행이고, 봇 PR이 돌리는 PR 게이트는 일반 워크플로 실행이다.

## 후보 비교
| 항목 | Renovate(Mend 호스티드 앱) | GitHub Dependabot | Gradle 플러그인(ben-manes + littlerobots) |
|---|---|---|---|
| 버전 카탈로그 | 지원. `*.versions.toml`(모든 디렉터리), `gradle/*.toml` [S1] | 지원. `gradle/libs.versions.toml` 표준 위치만 [S17] | littlerobots가 직접 수정 [S44] |
| `version.ref`·`[plugins]` | 문서 문장 없음, 소스로 확인 [S15] | 문서 문장 없음, 소스로 확인 [S22] | README에 명시 [S44] |
| included build(`build-logic`) | 문서 침묵. `*.gradle.kts`는 기본 패턴에 포함, `build-logic`의 `.kt`는 미포함(소스) [S15] | 문서 침묵. 소스는 included build 경로를 가져옴 [S22] | ben-manes는 제외가 기본, included build에도 따로 적용 필요 [S43] |
| Gradle 래퍼 | 지원하나 `gradlew` 실행이 v43부터 기본 차단. 무료 호스티드 허용 여부 미확인 [S1][S2][S14] | 지원. Gradle을 실행해 4개 파일 갱신 [S17] | ben-manes가 새 Gradle 버전을 보고만 함 [S43] |
| GitHub Actions | 지원(태그·다이제스트) [S4] | 지원 [S17][S19] | 미지원 |
| 묶음 | `packageRules` + `groupName`. Kotlin·KSP는 기본 묶음이 각각 따로, androidx 묶음 없음 [S8][S10] | `groups`의 `patterns`. 생태계 간 묶음은 `multi-ecosystem-groups` [S18] | 개념 없음(한 번에 전체 갱신) |
| 일정 | `schedule`(시간 단위), 호스티드 앱 4시간 주기 실행 [S8][S12] | `schedule.interval` 필수 [S18] | 직접 워크플로 작성 |
| PR 개수 제한 | 동시 10, 시간당 2가 기본 [S10] | 동시 5가 기본 [S18] | PR을 만들지 않음 |
| 프리릴리스 제외 | 기본 제외(`ignoreUnstable` true) [S7][S10] | 한정자 인식. 기본 제외 여부는 문서 문장 없음 [S18] | littlerobots 기본 PREFER_STABLE [S44] |
| 출시 후 대기 | `minimumReleaseAge`, 기본 없음. 자동 병합 시 14일 권고 [S9] | `cooldown`, 기본 3일 [S18] | 없음 |
| 자동 병합 | 내장. 상태 체크 통과가 기본 조건. 플랫폼 auto-merge 불가 시 자체 병합 [S6][S7] | 내장 없음. `gh pr merge --auto` 워크플로 → GitHub auto-merge 필요 [S20][S23] | 없음 |
| 비공개 저장소 비용 | 앱 무료 플랜 제공 [S12]. 봇 PR의 CI는 Actions 분 차감 [S30] | 플랜 무관 제공 [S25]. Dependabot 실행 자체는 분 미차감 [S21], 봇 PR의 CI는 차감 [S30] | 도구 무료. 실행·PR 생성 워크플로를 직접 유지 |
| 봇 PR의 CI 제약 | 문서에 제약 서술 없음 | Actions 시크릿 접근 불가, 읽기 전용 토큰 [S21] | 해당 없음 |
| 제3자 접근 | Mend 앱이 비공개 저장소 코드에 접근 | GitHub 내부 기능 | 없음 |
| NiA 채택 | **현재 채택**(2024-12-17 전환) [S45] | 2024-12-17 이전 사용 [S45] | 미채택 |
| 1인 개발자 적합성 | 설정 파일 하나 + 앱 설치. 묶음·자동 병합을 세밀하게 조정 가능. 래퍼 갱신은 첫 PR에서 확인 필요 | 설치 없이 파일 하나. 래퍼 갱신이 문서로 보장됨. 자동 병합은 유료 플랜 전제 | PR·일정·병합을 모두 손으로 만들어야 해 유지 부담이 가장 큼 |

**조사자 의견(팩 결정 필요)**: Gradle 플러그인 방식은 래퍼·액션을 갱신하지 못하고 PR 흐름이 없어 기본 도구로 삼기 어렵다. Renovate와 Dependabot은 팩 구성(표준 위치 카탈로그, GitHub Actions)을 둘 다 문서상 지원한다. 갈리는 지점은 세 가지다. ① 래퍼 갱신은 Dependabot만 문서로 보장된다. ② 자동 병합은 GitHub 플랜에 따라 Dependabot 방식이 막힐 수 있고 Renovate는 자체 병합이 있다. ③ Renovate는 비공개 코드를 제3자 앱에 여는 선택이다. NiA가 Renovate를 쓰지만 그 구성(`group:all`)은 PR이 20개월째 열려 있어 그대로 따를 근거가 되지 못한다.

## 규칙 후보
- 의존성·플러그인·Gradle 래퍼·GitHub Actions 버전 갱신은 봇이 여는 PR로만 받는다(수동으로 몰아서 올리지 않는다). [S1][S17][S9]
- 갱신 도구 선택(Renovate / Dependabot). **팩 결정 필요** — 두 도구 모두 출처가 기능을 뒷받침하고, 어느 쪽이 낫다는 공식 문장은 없다. [S1][S17]
- 버전 카탈로그는 표준 위치 `gradle/libs.versions.toml` 하나만 둔다(Dependabot은 이 위치만 명시 지원). [S17][S1]
- 동적 버전(`1.+`, 범위)을 쓰지 않는다. 카탈로그의 범위 버전은 Renovate도 갱신하지 못한다. [S36][S1]
- Compose 컴파일러 플러그인은 `version.ref = "kotlin"`으로 Kotlin과 같은 버전 키를 쓴다(별도 버전 키 금지). [S38][S40]
- Compose 라이브러리는 BOM으로만 버전을 정하고 개별 버전을 적지 않는다(갱신 대상은 BOM 버전 하나). [S38]
- Kotlin과 KSP 업데이트는 한 PR로 묶는다. [S45] — 근거는 NiA의 과거 `kotlin-ksp` 묶음 하나뿐이다. KSP 2.3.0부터 버전이 분리됐으므로 [S41] 묶지 않아도 된다는 해석도 가능하다. **팩 결정 필요**.
- 모든 업데이트를 한 PR로 묶지 않는다(`group:all` 금지). [S8][S45]
- 메이저 업데이트는 자동 병합하지 않는다. [S7][S6]
- 자동 병합은 PR 게이트(R-31-01)의 상태 체크가 모두 통과한 PR에만 허용하고, 체크 무시 옵션(`ignoreTests`)은 쓰지 않는다. [S6][S7][S20]
- 자동 병합을 켤지 여부와 대상 범위(패치만 / 마이너까지 / 끔). **팩 결정 필요** — 출처는 조건을 말할 뿐 켜라고 하지 않는다. [S6][S20]
- 자동 병합을 켠다면 출시 후 대기 기간을 둔다(Renovate 권고 14일, Dependabot 기본 3일). 값은 **팩 결정 필요**. [S9][S18]
- 프리릴리스(alpha/beta/rc)로는 올리지 않는다. 이미 프리릴리스를 쓰는 항목(detekt 2.0.0-alpha 등)은 같은 계열 안에서만 올린다. [S7][S44] — Dependabot을 고르면 기본 동작이 문서에 없어 `ignore` 규칙을 직접 써야 한다. [S18]
- `pluginManagement` 저장소에 `gradlePluginPortal()`을 포함한다(Renovate는 플러그인 마커를 Maven Central에서 찾지 않는다). [S5][S45] — Renovate를 고를 때만 해당.
- AGP를 올릴 때는 AGP 릴리스 노트의 Gradle 최소 버전을 먼저 확인하고 래퍼를 같이 올린다. [S39]
- Kotlin을 올릴 때는 KGP 호환 표의 Gradle·AGP 범위를 확인한다. [S40]
- Gradle 빌드의 의존성 그래프를 `gradle/actions/dependency-submission`(또는 저장소 설정의 자동 제출)으로 GitHub에 제출하고 Dependabot alerts를 켠다. [S17][S26][S28][S31]
- `gradle/verification-metadata.xml` 도입 여부. **팩 결정 필요** — Gradle·Android 문서는 가치를 설명하지만 Android 문서는 "consider" 수준이고, 켜면 업데이트마다 메타데이터 갱신과 검토가 따라붙는다. NiA는 쓰지 않는다. [S32][S36][S37][S45]
- 의존성 잠금(`gradle.lockfile`) 도입 여부. **팩 결정 필요** — 문서가 드는 주 용도는 동적 버전이고 팩은 동적 버전을 금지한다. NiA는 쓰지 않는다. [S33][S36][S45]
- Dependabot을 고르고 PR 게이트가 시크릿을 쓴다면 같은 이름의 Dependabot 시크릿을 등록한다. [S21]

## 출처가 침묵하는 것
- **Renovate와 Dependabot 중 무엇이 나은가.** 어느 공식 문서도 비교하거나 권하지 않는다. NiA가 Renovate로 옮긴 이유도 커밋 메시지에 없다.
- **Android 프로젝트용 권장 묶음.** Renovate 기본 묶음에 androidx·Compose·Hilt가 없고, Google 문서도 "androidx를 한 PR로 묶어라"고 말하지 않는다. androidx 묶음은 관행이지 출처가 있는 규칙이 아니다.
- **Kotlin과 KSP를 같이 올려야 하는가.** KSP는 버전 분리를 선언했을 뿐 호환 범위 표를 내지 않았다.
- **업데이트 주기.** 주간·월간 중 무엇이 맞는지 말하는 출처가 없다. Renovate는 권고 목록에 "Update your dependencies often"이라고만 적는다. [S9]
- **included build 안의 의존성 갱신.** Renovate·Dependabot 문서 모두 included build를 언급하지 않는다(소스로만 확인).
- **BOM 항목 갱신.** 두 도구 문서 모두 Gradle BOM(`platform(...)`) 갱신을 따로 설명하지 않는다.
- **Dependabot의 Gradle 프리릴리스 기본 동작.** 한정자 목록은 있으나 "기본으로 건너뛴다"는 문장이 없다.
- **액션을 메이저 태그로 둘지 SHA로 고정할지와 갱신 도구의 관계.** GitHub는 "Pinning an action to a full-length commit SHA is currently the only way to use an action as an immutable release."라 하고 [S29] Renovate 권장 프리셋도 "We recommend pinning _all_ Actions."라고 한다. [S9] 팩의 R-31-04(메이저 태그 고정)와 방향이 다르다. 메이저 태그로 두면 봇 PR은 메이저가 바뀔 때만 생긴다. 이 충돌은 R-31-04 소유 문서에서 다룰 일이다.
- **1인 개발자에게 맞는 PR 개수 상한.** 기본값(Renovate 10, Dependabot 5)만 있고 권장값은 없다.

## 미확인
- **무료 Mend 호스티드 앱이 `gradlew` 실행을 허용하는지.** 문서에 문장이 없다. v43 이후에도 호스티드 앱 로그에 `./gradlew` 실행 기록이 있는 사용자 보고(43.36.2, 43.235.0)가 있으나 [S16 #41492, #44189] 둘 다 공개 저장소이고 플랜을 알 수 없다. 허용되지 않으면 래퍼의 jar·스크립트와 `verification-metadata.xml`·lockfile이 갱신되지 않는다(소스상 아티팩트 갱신을 건너뜀). 이때 PR에 무엇이 남는지는 확인하지 못했다. 검색 요약에 "호스티드 앱은 켜지 않는다"는 문장이 나왔으나 1차 출처를 찾지 못해 채택하지 않았다.
- **검색 요약에 나온 Renovate 취약점 번호(CVE-2026-88886).** Renovate 저장소의 보안 권고 목록(GitHub API, 최근 10건)에서 같은 번호를 찾지 못했다. 노트에 반영하지 않았다.
- **Renovate 플러그인 마커 조회 문제의 처리 상태.** Discussion #46534는 2026-09-28에 열렸고 메인테이너 답변이 아직 없다. `gradlePluginPortal()`을 선언하면 피할 수 있다는 것은 문서 [S5]와 보고 내용을 합친 추론이며 직접 실행해 보지 않았다.
- **KSP 2.3.11과 Kotlin 2.4.20의 조합.** 퀵스타트 예시는 2.4.20 + 2.3.10이다. 2.3.11·2.3.12 조합을 명시한 문장은 없다(기존 `testing-ci.md` 미확인 항목과 같음).
- **Hilt 2.60.1과 Kotlin 2.4.20 호환.** 명시 문장을 찾지 못했다.
- **KGP 2.4.20 "완전 지원" 상한을 넘는 팩 구성의 실제 영향.** 표는 Gradle 9.7.0 / AGP 9.3.1까지이고 팩은 9.7.1 / 9.4.0이다. 문서는 경고 가능성만 말한다. 빌드로 확인하지 않았다.
- **littlerobots 플러그인의 Gradle 9.x 지원.** README는 "Gradle 7.2 or up"만 적고 9.x 검증 여부를 말하지 않는다.
- **Dependabot의 included build 처리 결과.** 소스에 경로 수집 코드가 있는 것만 확인했고, `build-logic` 안의 의존성에 실제로 PR이 열리는지는 실행해 보지 않았다.
- **Renovate가 만든 PR의 워크플로에서 시크릿을 쓸 수 있는지.** Dependabot 같은 제한 서술이 Renovate 문서에 없다는 것만 확인했다. GitHub App이 만든 PR의 시크릿 처리 규칙 문서는 열지 않았다.
- **GitHub 요금제 페이지.** 플랜별 제공 범위는 각 기능 문서의 안내 문장으로 확인했고, 가격 페이지는 열지 않았다.
- **조사 중 확인된 최신 버전(팩 확정값과 다름, 참고용)**: Gradle 9.8.0(2026-09-24 빌드) [S35], KSP 2.3.12(2026-09-09) [S41], gradle/actions v6.4.0(2026-09-28) [S31], Compose BOM 문서 예시 2026.09.00 [S38]. 팩 값을 바꾸라는 뜻이 아니라 자동 업데이트를 켜면 첫 주에 이 PR들이 열린다는 뜻이다.
- **GitHub API 제한.** 조사 막바지에 검색 API의 2차 제한에 걸려 NiA의 Dependabot PR 전체 기간 통계는 끝까지 받지 못했다. 최근 PR 6건(2024-12-16)과 설정 파일 이력은 확인했다.
