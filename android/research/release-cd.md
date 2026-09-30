# 조사 노트 — 배포(CD)와 버전 번호 부여

조사일: 2026-09-30. 대상: 팩에 새로 넣을 "배포·버전 번호" 규칙의 근거 조사(규칙 문장은 이 노트에 쓰지 않는다).
확정 전제: AGP 9.4.0 / Gradle 래퍼 9.7.1 / JDK 17 / Kotlin 2.4.20 / compileSdk·targetSdk 37 / minSdk 26 / CI는 GitHub Actions(checkout v7, setup-java v6, setup-gradle v6, upload-artifact v7).
중복 회피: R-19-04(R8 최적화), R-19-12(keystore.properties 분리), R-31-08(키·서명 CI 시크릿 주입), R-31-09(R8 켠 릴리스 검증 잡)는 다시 조사하지 않았다.

조사 방식
- 웹 문서는 `curl`로 HTML 원문을 받아 태그만 제거한 본문에서 읽었다. 요약 모델을 거치지 않았으므로 아래 큰따옴표 인용은 페이지 본문을 그대로 옮긴 것이다(줄바꿈과 인라인 코드 서식만 제거).
- GitHub 저장소는 README·설정 파일을 `raw.githubusercontent.com`에서, 릴리스 태그·날짜·본문을 GitHub REST API(`/repos/<repo>/releases`)에서 받았다. 요약 도구가 돌려준 릴리스 날짜는 연도가 2년 어긋나 있었고(예: GPP 4.1.1을 2024-08-11로 표시), API의 `published_at` 값으로 바로잡았다.
- 큰따옴표가 없는 서술은 요약이다.

## 출처

| # | 조직 | 문서명 | URL | 종류 | 최종 갱신/버전 |
|---|---|---|---|---|---|
| S1 | Google | Version your app | https://developer.android.com/studio/publish/versioning | 공식 가이드 | 2025-08-20 |
| S2 | Google | Create and set up your app — "App version requirements for Play Console" 절 | https://support.google.com/googleplay/android-developer/answer/9859152 | Play Console 도움말 | 미표기 |
| S3 | Google | Write Gradle plugins (Extend AGP) | https://developer.android.com/build/extend-agp | 공식 가이드 | 2026-09-16 |
| S4 | Google | AGP API 레퍼런스 9.4 — `VariantOutput`, `ApplicationVariant` | https://developer.android.com/reference/tools/gradle-api/9.4/com/android/build/api/variant/VariantOutput , https://developer.android.com/reference/tools/gradle-api/9.4/com/android/build/api/variant/ApplicationVariant | 공식 레퍼런스 | 2026-09-18 |
| S5 | Google | AGP 9.0.0 릴리스 노트 (팩 S130과 같은 문서) | https://developer.android.com/build/releases/agp-9-0-0-release-notes | 공식 문서 | AGP 9.0.0 |
| S6 | Google | AGP 9.4.0 릴리스 노트 | https://developer.android.com/build/releases/agp-9-4-0-release-notes | 공식 문서 | 2026-09-24 / AGP 9.4.0 |
| S7 | Triple-T | Gradle Play Publisher — README, Releases, `settings.gradle.kts`(4.1.1 태그), 이슈 #1188 | https://github.com/Triple-T/gradle-play-publisher , https://github.com/Triple-T/gradle-play-publisher/releases , https://raw.githubusercontent.com/Triple-T/gradle-play-publisher/4.1.1/settings.gradle.kts , https://github.com/Triple-T/gradle-play-publisher/issues/1188 | 오픈소스 README·릴리스 노트 | 4.1.1 (2026-08-11) |
| S8 | fastlane | `upload_to_play_store`(supply) 문서, fastlane 문서 홈, Releases, `fastlane.gemspec` | https://docs.fastlane.tools/actions/upload_to_play_store/ , https://docs.fastlane.tools/ , https://github.com/fastlane/fastlane/releases , https://raw.githubusercontent.com/fastlane/fastlane/master/fastlane.gemspec | 공식 문서·릴리스 노트 | 2.240.1 (2026-09-15) |
| S9 | r0adkll | upload-google-play — README, Releases, `action.yml` | https://github.com/r0adkll/upload-google-play , https://github.com/r0adkll/upload-google-play/releases , https://raw.githubusercontent.com/r0adkll/upload-google-play/master/action.yml | 오픈소스 README·릴리스 노트 | v1.1.5 (2026-04-21) |
| S10 | Google | Play Developer API — Getting started | https://developers.google.com/android-publisher/getting_started | 공식 가이드 | 2025-12-18 |
| S11 | Google | Play Developer API — Edits | https://developers.google.com/android-publisher/edits | 공식 가이드 | 2025-12-18 |
| S12 | Google | Play Developer API — APKs and Tracks | https://developers.google.com/android-publisher/tracks | 공식 가이드 | 2025-12-18 |
| S13 | Google | Play Developer API 레퍼런스 — `edits.tracks` | https://developers.google.com/android-publisher/api-ref/rest/v3/edits.tracks | 공식 레퍼런스 | 2026-09-28 |
| S14 | Google | Play Developer API 레퍼런스 — `edits.bundles.upload`, `edits.commit` | https://developers.google.com/android-publisher/api-ref/rest/v3/edits.bundles/upload , https://developers.google.com/android-publisher/api-ref/rest/v3/edits/commit | 공식 레퍼런스 | 2025-05-21 / 2026-03-18 |
| S15 | Google | Set up an open, closed, or internal test | https://support.google.com/googleplay/android-developer/answer/9845334 | Play Console 도움말 | 미표기 |
| S16 | Google | App testing requirements for new personal developer accounts | https://support.google.com/googleplay/android-developer/answer/14151465 | Play Console 도움말 | 미표기 |
| S17 | Google | Release app updates with staged rollouts | https://support.google.com/googleplay/android-developer/answer/6346149 | Play Console 도움말 | 미표기 |
| S18 | Google | Prepare and roll out a release | https://support.google.com/googleplay/android-developer/answer/9859348 | Play Console 도움말 | 미표기 |
| S19 | Google | Share app bundles and APKs internally (internal app sharing) | https://support.google.com/googleplay/android-developer/answer/9844679 | Play Console 도움말 | 미표기 |
| S20 | Google | Use Play App Signing | https://support.google.com/googleplay/android-developer/answer/9842756 | Play Console 도움말 | 미표기 |
| S21 | Google | Sign your app (팩 S134와 같은 문서) | https://developer.android.com/studio/publish/app-signing | 공식 가이드 | 2026-03-06 |
| S22 | Google | About Android App Bundles | https://developer.android.com/guide/app-bundle | 공식 가이드 | 2026-06-24 |
| S23 | Google (Firebase) | Distribute Android apps to testers using Gradle (APK 탭 / AAB 탭) | https://firebase.google.com/docs/app-distribution/android/distribute-gradle , https://firebase.google.com/docs/app-distribution/android/distribute-gradle?apptype=aab | 공식 가이드 | 2026-09-24 |
| S24 | Google (Firebase) | Distribute Android apps to testers using the Firebase CLI | https://firebase.google.com/docs/app-distribution/android/distribute-cli | 공식 가이드 | 2026-09-24 |
| S25 | Google (Firebase) | Best practices for distributing Android apps to QA testers using CI/CD | https://firebase.google.com/docs/app-distribution/best-practices-distributing-android-apps-to-qa-testers-with-ci-cd | 공식 가이드 | 2026-09-24 |
| S26 | Google (Firebase) | App Distribution 문제 해결·FAQ (테스터·릴리스 한도) | https://firebase.google.com/docs/app-distribution/troubleshooting | 공식 가이드 | 2026-09-24 |
| S27 | Google (Firebase) | Firebase Android SDK 릴리스 노트, Google Maven 메타데이터 | https://firebase.google.com/support/release-notes/android , https://dl.google.com/dl/android/maven2/com/google/firebase/firebase-appdistribution-gradle/maven-metadata.xml | 릴리스 노트 | 플러그인 5.3.0 (2026-06-16) |
| S28 | Google Cloud | Best practices for managing service account keys | https://docs.cloud.google.com/iam/docs/best-practices-for-managing-service-account-keys | 공식 가이드 | 2026-09-24 |
| S29 | Google Cloud | Workload Identity Federation | https://docs.cloud.google.com/iam/docs/workload-identity-federation | 공식 가이드 | 2026-09-29 |
| S30 | Google (google-github-actions) | `auth` 액션 README, Releases | https://github.com/google-github-actions/auth , https://github.com/google-github-actions/auth/releases | 오픈소스 README·릴리스 노트 | v3.0.0 (2025-08-28) |
| S31 | GitHub | Configuring OpenID Connect in Google Cloud Platform | https://docs.github.com/en/actions/how-tos/secure-your-work/security-harden-deployments/oidc-in-google-cloud-platform | 공식 가이드 | 미표기 |
| S32 | wzieba | Firebase-Distribution-Github-Action README, Releases, `action.yml` | https://github.com/wzieba/Firebase-Distribution-Github-Action | 오픈소스 README(서드파티) | v1.7.1 (2025-03-28) |
| S33 | Google (Firebase) | firebase-tools Releases, fastlane-plugin-firebase_app_distribution Releases | https://github.com/firebase/firebase-tools/releases , https://github.com/firebase/fastlane-plugin-firebase_app_distribution/releases | 릴리스 노트 | v15.32.0 (2026-09-28) / v1.0.0 (2026-03-04) |

`cloud.google.com/iam/...` 주소는 `docs.cloud.google.com/iam/...`로 리다이렉트된다. 표에는 최종 주소를 적었다.
팩 기존 번호와 겹치는 문서: S5 = 팩 S130, S21 = 팩 S134. 병합할 때 새 번호를 주지 말고 기존 번호를 재사용한다.

## 핵심 내용 (출처별)

### 질문 1 — versionCode / versionName

**제약 (Google 공식)**
- versionCode는 양의 정수이고 사용자에게 보이지 않는다. "A positive integer used as an internal version number." [S1]
- 증가 요구: "You can set the value to any positive integer. However, make sure that each successive release of your app uses a greater value." [S1]
- 최대값: "The greatest value Google Play allows for versionCode is 2100000000." [S1] Play Console 도움말도 같은 값을 적는다 — "To upload your app to Play Console, the greatest possible value for versionCode is 2100000000. If the versionCode of your app exceeds this value, Play Console will prevent you from submitting a new app bundle." [S2]
- 재사용 금지: "You can't upload an APK to the Play Store with a versionCode you have already used for a previous version." [S1] 문장의 대상은 APK로 적혀 있고 AAB를 따로 언급하지 않는다. AAB는 S2의 "you'll need to increase the versionCode for every update and still stay below the maximum"이 받친다. [S2]
- 다운그레이드 방지: "The Android system uses the versionCode value to protect against downgrades by preventing users from installing an APK with a lower versionCode than the version currently installed on their device." [S1]
- 관행 서술: "Typically, you release the first version of your app with versionCode set to 1, then monotonically increase the value with each release, regardless of whether the release constitutes a major or minor release." [S1] "Typically"로 시작하는 관행 설명이지 요구가 아니다.
- 예외: 내부 앱 공유는 재사용을 허용한다 — "Version codes don't need to be new or unique, and you can reuse version codes for app bundles or APKs that you’re sharing." [S19]
- versionName: "A string used as the version number shown to users. This setting can be specified as a raw string or as a reference to a string resource." / "The versionName is the only value displayed to users." [S1]
- versionName 형식은 강제되지 않는다. S1은 `<major>.<minor>.<point>` 또는 "any other type of absolute or relative version identifier"라고 하고, 시맨틱 버저닝은 소개만 한다 — "many developers consider Semantic Versioning a good basis for a versioning strategy." [S1]
- 설정 위치: `defaultConfig {}`에 기본값을 두고 build type·product flavor에서 덮어쓸 수 있으며 빌드 중 매니페스트로 병합된다. [S1]

**빌드 시점 주입 (AGP 9.x)**
- AGP 9.0은 옛 variant API 접근을 없앴다. "AGP 9.0 uses our new DSL interfaces exclusively, and the implementations have changed to new types that are fully hidden. This also removes access to the old, deprecated variant API." / "Replace any use of the applicationVariants and similar APIs with the new androidComponents API." [S5] 즉 `android.applicationVariants.all { output.versionCodeOverride = … }` 방식은 `android.newDsl=false`로 물러나지 않는 한 쓸 수 없다.
- 공식 대체 경로는 `androidComponents.onVariants`에서 출력의 `versionCode` 프로퍼티를 설정하는 것이다. S3의 예시 코드(원문 그대로):
  ```kotlin
  onVariants(selector().withBuildType("release")) { variant ->
      // Gather the output when we are in single mode (no multi-apk).
      val mainOutput = variant.outputs.single { it.outputType == OutputType.SINGLE }
      ...
      mainOutput.versionCode.set(versionCodeTask.map { it.outputFile.get().asFile.readText().toInt() })
  }
  ```
  [S3]
- AGP 9.4 레퍼런스에 `VariantOutput`이 그대로 있다. `val versionCode: Property<Int>` — "The version code for this output. This will be initialized with the variant's merged flavor value or read from the manifest file if unset. It is safe to modify it." `val versionName: Property<String>` — "It is safe to modify it." 둘 다 "Added in 4.2.0". `ApplicationVariant`에는 `val outputs: List<VariantOutput>`이 있다. [S4]
- 주의: `VariantOutput` 설명은 "This only applies to APKs as AARs and Bundles (AABs) do not support multiple outputs."라고 한다. [S4] 이 문장은 "출력이 여러 개"인 경우가 APK에만 있다는 뜻으로 읽힌다. 단일 출력에 설정한 `versionCode`가 AAB 산출물에도 반영되는지는 레퍼런스가 문장으로 밝히지 않는다(미확인 참조).
- AGP 9.4 릴리스 노트는 버전 주입 API의 변경을 언급하지 않는다. 같은 문서에 "Starting with AGP 10, the new Variant API is mandatory for all projects."가 있다. [S6]

### 질문 2 — Play 업로드 자동화 도구

**(a) Gradle Play Publisher (GPP)** [S7]
- 최신 4.1.1(2026-08-11), 직전 4.1.0(2026-08-11), 4.0.0(2026-01-25), 3.13.0(2025-12-08). Gradle Plugin Portal 메타데이터도 `<latest>4.1.1</latest>`.
- 스스로를 비공식이라고 밝힌다 — "Gradle Play Publisher (GPP) is Android's unofficial release automation Gradle Plugin."
- 유지보수 상태는 README가 직접 적는다 — "Project status: maintenance mode" / "Issues are ignored, but pull requests are not. If you need to get something done, submit a PR!" 같은 내용의 이슈 #1188이 2026-04-27에 열렸다. 저장소는 보관(archived) 상태가 아니고 마지막 push는 2026-08-26.
- AGP 9: 4.0.0 릴리스 노트 — "Support Android Gradle Plugin 9 ... Compatibility with older AGP or Gradle versions is not supported—stay on GPP 3.x if you don't wish to upgrade." 4.1.1 릴리스 노트 — "Fix Isolated Projects error in Gradle 9.7".
- **AGP 9.4 지원은 명시 없음.** 4.1.1 태그의 `settings.gradle.kts`는 `version("agp", "9.0.0")`이고 AGP는 `compileOnly`로 걸려 있다("Compile only to not force a specific AGP version"). 빌드 기준이 9.0.0이라는 사실만 확인된다.
- 설정 위치: 모듈 `build.gradle.kts`의 `play { }` 블록. `track` 기본값 `internal`, `releaseStatus` 기본값 `ReleaseStatus.COMPLETED`, `userFraction` 기본값 `0.1`.
- 인증: `serviceAccountCredentials.set(file(...))` 또는 환경 변수 — "put the contents of your JSON file in the `ANDROID_PUBLISHER_CREDENTIALS` environment variable and don't specify the `serviceAccountCredentials` property." 또는 `useApplicationDefaultCredentials = true`(+ 선택적 `impersonateServiceAccount`).
- 첫 업로드는 수동: "The first APK or App Bundle needs to be uploaded via the Google Play Console because registering the app with the Play Store cannot be done using the Play Developer API."
- 버전 충돌 처리: `ResolutionStrategy.IGNORE` 또는 `ResolutionStrategy.AUTO`("Automatically pick the correct version code so you don't have to manually update it"). AUTO 후처리 예시는 `androidComponents.onVariants`의 `output.versionCode`/`output.versionName`을 쓴다.

**(b) fastlane supply (`upload_to_play_store`)** [S8]
- fastlane 최신 2.240.1(2026-09-15), 2.240.0(2026-09-14), 2.239.0(2026-09-04). 릴리스가 월 단위로 이어진다.
- Gradle 플러그인이 아니라 빌드가 끝난 AAB/APK 파일을 올리는 도구다(`fastlane supply --aab path/to/app.aab`). 그래서 **AGP 버전과 직접 결합하지 않는다**(AGP 9 관련 문장은 문서에 없음 — 명시 없음).
- 런타임: "fastlane supports Ruby versions 3.2 or newer, but prefers Ruby 3.3 or greater." gemspec은 `required_ruby_version = '>= 3.2'`. "fastlane is officially supported to run on macOS." Linux와 Windows는 부분 지원(partially supported)이라고 적혀 있다.
- 인증: "fastlane supports Workload Identity Federation and service account keys". `json_key` 옵션 설명 — "The path to a Google credentials JSON file (Application Default, Workload Identity, or Service Account), used to authenticate with Google".
- 트랙: "The default available tracks are: production, beta, alpha, internal". `rollout`, `release_status`(completed, draft, halted, inProgress), `track_promote_to` 지원.

**(c) r0adkll/upload-google-play** [S9]
- 최신 v1.1.5(2026-04-21), v1.1.4(2026-04-20). 그 앞 릴리스는 v1.1.3(2024-02-09)으로 **약 2년 2개월 공백**이 있었다. 마지막 push 2026-09-10, 보관 상태 아님.
- `action.yml`의 런타임은 `using: 'node24'`. v1.1.4 릴리스 노트에 "Upgrade to Node24".
- 빌드가 끝난 파일을 올리는 액션이라 AGP와 결합하지 않는다(AGP 관련 문장 없음 — 명시 없음).
- 설정 위치: 워크플로 YAML의 `with:`. `tracks` 기본값은 **`production`**("Defaults to `production`"), `status` 기본값 `completed`. `track`·`releaseFile` 입력은 폐기 예정이고 `tracks`·`releaseFiles`로 바꿔야 한다.
- 인증: `serviceAccountJsonPlainText`(시크릿 내용) 또는 `serviceAccountJson`(파일 경로). README는 두 길을 이렇게 구분한다 — "Account key in GitHub secrets (simpler)" / "Workload identity authentication (more secure, recommended by GCP)". WIF 예시는 `google-github-actions/auth@v2`를 쓰고 `serviceAccountJson: ${{ steps.auth.outputs.credentials_file_path }}`로 넘긴다(예시의 액션 버전이 v2로 남아 있음. 최신은 v3 [S30]).
- 첫 프로덕션 업로드 제약(FAQ): "Before you can target `production`, push at least one release through an earlier track."

**(d) Play Developer Publishing API 직접 호출** [S10][S11][S12][S13][S14]
- 트랜잭션 모델: 편집(edit)을 만들고 → 수정하고 → 커밋한다. "Changes made within an edit are not live until the edit is committed." [S11]
- 동시성 제약: "Each user may have only a single edit open at a time." / "if anyone commits an edit or makes changes to an app through the Play Console, all other edits for the app (owned by any user) are invalidated." [S11]
- AAB 업로드는 `edits.bundles.upload`. "If you are using the Google API client libraries, please increase the timeout of the http request before calling this endpoint (a timeout of 2 minutes is recommended)." [S14]
- 커밋에 `changesNotSentForReview` 쿼리 파라미터가 있다(심사 제출 시점을 늦춤). [S14]
- 인증 권고: "You need to configure access to the Google Play Developer API with an OAuth client or a service account. In most cases, you should use a service account to access the API." [S10]
- API 자체는 HTTP이므로 AGP와 무관하다. 유지보수 주체는 Google.

### 질문 3 — Play 트랙과 테스터 배포

**트랙 설명** [S15][S18]
- 내부 테스트: "Create an internal testing release to quickly distribute your app to up to 100 testers for initial quality assurance checks." / "An internal test can have up to 100 testers per app." / "You can start an internal test before completing app setup."
- 내부 테스트 속도: "When you publish a new Android App Bundle to the internal test track, it becomes available to testers within minutes."
- 내부 테스트 심사: "Policy and security reviews: Internal tests might not be subject to standard Play policy or security reviews." — "might not"이라 심사가 없다고 단정하지는 않는다.
- 비공개(closed) 테스트: "You can create up to 200 lists, and each list can contain up to 2,000 users. You can create up to 50 lists per track." 추가 비공개 트랙을 만들 수 있고 이름을 붙인다 — "The track title identifies the track in Play Console and Google Play Developer API."
- 공개(open) 테스트: "Anyone can join an open testing program and submit private feedback." 인원 제한을 걸면 "must be at least 1,000".
- 프로덕션: "Production releases are available to all Google Play users in your chosen countries and regions." [S18]
- 동시 실행: "You can run multiple closed tests and one open test at the same time." 내부 테스트에 옵트인한 사용자는 공개·비공개 테스트를 받을 수 없다.
- 미완료 릴리스가 있으면 새 릴리스를 만들 수 없다 — "You cannot create a new release when you have outstanding releases. Roll out any staged releases to 100%, or remove changes on the Publishing overview page and discard any unpublished releases first." [S18]

**API 트랙 이름** [S12]
- S12의 표는 프로덕션 `production`, 공개 테스트 `beta`, **내부 테스트 `qa`** 로 적는다("The internal testing track: "qa""). 폼 팩터 트랙은 `[prefix]:defaultTrackName`(예: `wear:production`).
- 반면 GPP·fastlane·r0adkll 문서는 모두 내부 테스트 트랙을 `internal`로 쓴다. [S7][S8][S9] 공식 가이드와 도구 문서의 표기가 다르다(미확인 참조).

**단계적 출시** [S17][S13]
- "Staged rollouts can only be used for app updates, not when publishing an app for the first time."
- "Keep in mind, your app's staged rollout percentage won't increase automatically."
- 중단: "When you halt a staged rollout, no additional users will receive the app version in your existing staged rollout. Users who already received the app version in your staged rollout version will remain on that version."
- API의 `userFraction`: "Fraction of users who are eligible for a staged release. 0 < fraction < 1. Can only be set when status is "inProgress" or "halted"." [S13]
- 프로덕션과 테스트 트랙 모두에서 쓸 수 있다 — "You can release an app update to production and test tracks using a staged rollout."

**신규 개인 개발자 계정의 테스트 요건** [S16]
- 대상: "personal Google Play Console accounts created after November 13, 2023".
- 요건: "must run a closed test for their app with a minimum of 12 testers who have been opted in continuously for at least 14 days."
- 연속성: "If a tester opts out and opts back in later, the 14 days must be consecutive to count toward the minimum requirement of 12 continuous opted-in testers."
- 요건 충족 전에는 Production과 Pre-registration이 비활성이고, 공개 테스트도 프로덕션 접근 권한을 받은 뒤에 열린다("Open testing becomes available after you gain production access.").
- 트랙별 접근 조건 표: 내부 테스트 "None." / 비공개 테스트 "Complete app setup." / 공개 테스트 "Gain access to production." / 프로덕션 "Run a closed test with at least 12 opted-in testers continuously for 14 days."
- 신청 후 심사: "Review usually takes seven days or less, but can occasionally take longer."
- **조직 계정**: 이 문서는 요건의 대상을 개인 계정으로만 한정하고 조직 계정은 언급하지 않는다. "조직 계정은 면제"라는 문장은 페이지에 없다 — 대상에서 빠져 있다는 것만 확인된다.

**내부 앱 공유(internal app sharing)** [S19] — 내부 테스트 트랙과 다른 기능이다.
- "Uploaded artifacts for internal app sharing aren’t shown in your app bundle explorer, nor can they be included in releases on testing or production tracks."
- "Uploaded artifacts for internal app sharing can be signed with any key, and don’t need to be signed with a production or upload key."
- "a maximum of 100 users will be able to download your app using the link." / "Download links expire 60 days after the upload date."
- "You can upload and share debuggable app bundles or APKs."

### 질문 4 — Firebase App Distribution

**연동 방법** [S25][S23][S24]
- 공식 권고: "If you want to automate building and releasing apps to your testers and you're using CI/CD, we recommend that you use fastlane or Gradle. Another option is to use the Firebase CLI". [S25]
- **GitHub Actions 전용 공식 문서·공식 액션은 없다.** Firebase 문서가 제시하는 길은 Gradle 플러그인, fastlane 플러그인, Firebase CLI, 공개 API 넷이다. [S25] 흔히 쓰는 `wzieba/Firebase-Distribution-Github-Action`은 서드파티이고 최신 v1.7.1(2025-03-28) 이후 push가 없으며 Docker 컨테이너 액션이다(`using: 'docker'`). [S32]
- Gradle 플러그인: id `com.google.firebase.appdistribution`, 문서 예시 버전 `5.3.0`. 태스크 `appDistributionUpload<Variant>` — `./gradlew assembleRelease appDistributionUploadRelease`. [S23]
- 설정은 build type·product flavor 안의 `firebaseAppDistribution { }`. 주요 파라미터 `artifactType`("AAB" 또는 "APK"), `artifactPath`, `releaseNotes`/`releaseNotesFile`, `testers`/`testersFile`, `groups`/`groupsFile`, `serviceCredentialsFile`. [S23]
- CLI: `firebase appdistribution:distribute test.apk --app <앱 ID> --release-notes "..." --testers-file testers.txt`. [S24] firebase-tools 최신 v15.32.0(2026-09-28). [S33]

**플러그인 버전과 AGP 9** [S27]
- Google Maven 메타데이터의 최신은 5.3.0(`lastUpdated` 2026-06-17). 릴리스 노트상 5.3.0은 2026-06-16 갱신분 — "Added authentication support for Application Default Credential and Workload Identity Federation."
- 5.2.1(2026-02-05 갱신분) — "Declared dependency on Google Services plugin."
- 5.2.0(2025-10-30 갱신분) — "Fixed compatibility with AGP 9.0.0." / "Deprecated support for AGP < 8.1.0 (and Gradle < 8.0)."
- 5.2.0의 DSL 변경: 앱 빌드 스크립트 루트의 `firebaseAppDistribution { }`는 `firebaseAppDistributionDefault { }`로 이름이 바뀌었고, 원래 DSL은 앞으로 buildTypes·productFlavors 안에서만 동작한다.
- **AGP 9.4 지원은 명시 없음.** 확인되는 것은 AGP 9.0.0 호환 수정뿐이다.

**인증** [S23][S25]
- Gradle 플러그인은 서비스 계정 키 파일(`serviceCredentialsFile`) 또는 `GOOGLE_APPLICATION_CREDENTIALS` 환경 변수(ADC)를 쓴다. 역할은 "Firebase App Distribution Admin". "By default, the Gradle plugin looks for credentials from the Firebase CLI if no other authentication method is used." [S23]
- WIF: "If you're using workload identity federation, you can generate and use a credential configuration file instead of a service account key." [S25]

**AAB 배포 조건** [S23 AAB 탭]
- "To upload AABs to App Distribution, you must link your Firebase app to an app in Google Play."
- 구조: "App Distribution integrates with Google Play's internal app sharing service to process the AABs you upload and serve APKs that are optimized for your testers' device configurations."
- 요건 세 가지: ① "The app in Google Play and the Firebase Android app are both registered using the same package name." ② "The app in Google Play is set up on the app dashboard and is distributed to one of the Google Play tracks (Internal testing, Closed testing, Open testing, or Production)." ③ "The app's review in Google Play is complete and the app is published."
- 게시 상태 판정: "Your app is published if the App status column displays one of the following statuses: Internal testing (not Draft internal testing), Closed testing, Open testing, or Production."
- 권한: 연결에는 Firebase Owner/Admin + Play 개발자 계정 Admin 접근이 필요하다. 이미 연결돼 있으면 Play 쪽 접근 없이 AAB를 올릴 수 있다.
- APK는 이 조건이 없다. CLI 문서 — "You must sign the APK with your debug key or app signing key." [S24]

**한도** [S26][S23]
- "Add a maximum of 500 testers to a Firebase project" / "Add a maximum of 200 testers to an App Distribution group". 증가 요청은 무료.
- "App Distribution supports a maximum of 1,000 releases per app." 릴리스는 150일 뒤 만료. [S25][S23]
- 테스터 초대는 30일 안에 수락해야 한다.

### 질문 5 — 서비스 계정 인증

**Google의 권고 문장**
- 서비스 계정 키: "Service account keys can become a security risk if not managed carefully. You should choose a more secure alternative for authentication whenever possible." / "The best way to mitigate these threats is to avoid user-managed service account keys and to use other methods to authenticate service accounts whenever possible." [S28]
- 키를 쓸 수밖에 없을 때의 지침: "Don't submit service account keys to source code repositories." / "Rotate service account keys to reduce security risk caused by leaked keys." / "Whenever possible, avoid storing service account keys on a file system." [S28]
- WIF: "service account keys are powerful credentials, and can present a security risk if they are not managed correctly. Workload Identity Federation eliminates the maintenance and security burden associated with service account keys." GitHub가 지원 대상으로 명시된다("deployment services, such as GitHub and GitLab"). [S29]
- "We recommend that you use Workload Identity Federation to provide access directly to a Google Cloud resource. Although most Google Cloud APIs support Workload Identity Federation, some APIs have limitations. As an alternative, you can use service account impersonation." [S29]

**google-github-actions/auth** [S30]
- 최신 v3.0.0(2025-08-28), 메이저 태그 `v3`. "This action runs using Node 24."
- "Workload Identity Federation is recommended over Service Account Keys as it obviates the need to export a long-lived credential and establishes a trust delegation relationship between a particular GitHub Actions workflow invocation and permissions on Google Cloud."
- 세 가지 방식 순서: "(Preferred) Direct Workload Identity Federation" → "Workload Identity Federation through a Service Account" → "Service Account Key JSON".
- 키 방식 경고: "Service Account Key JSON credentials are long-lived credentials and must be treated like a password." / "By default, these credentials never expire, which is why the former authentication options are much preferred."
- 전제: "Run the `actions/checkout@v7` step _before_ this action." 워크플로에 `id-token: 'write'` 권한이 필요하다.
- 제약: "As of the time of this writing, the GitHub OIDC token expires in 5 minutes, which means any derived credentials also expire in 5 minutes." Direct WIF 토큰은 최대 10분.
- 제약: WIF는 Firebase Admin SDK에서 지원되지 않는다는 경고가 있다("not supported by Firebase Admin SDK"). 이 경고의 대상은 Admin SDK이고 App Distribution Gradle 플러그인은 5.3.0에서 WIF 지원을 추가했다. [S27]
- 산출 파일 누출 방지: `.gitignore`에 `gha-creds-*.json`을 넣으라고 한다.

**GitHub 문서** [S31]
- "OpenID Connect (OIDC) allows your GitHub Actions workflows to access resources in Google Cloud Platform (GCP), without needing to store the GCP credentials as long-lived GitHub secrets."
- "Setting id-token: write in the workflow’s permissions does not give the workflow permission to modify or write to any resources."

**Play Developer API와 WIF의 관계**
- Play Developer API 문서(S10)는 서비스 계정을 권하지만 WIF를 언급하지 않는다. Play Console 권한은 서비스 계정 이메일을 "Users & Permissions"에 초대해 부여하므로 [S10], WIF를 쓰려면 Direct 방식이 아니라 **서비스 계정을 거치는 방식**이어야 한다는 것이 S30의 구조 설명에서 따라 나온다. 다만 "Play Developer API는 WIF(서비스 계정 가장)로 호출할 수 있다"는 문장을 Google 공식 문서에서 찾지는 못했다. 도구 쪽 문서(fastlane [S8], r0adkll [S9], GPP의 ADC + impersonation [S7])가 그 경로를 제공한다.

### 질문 6 — Play App Signing / 업로드 키

- 두 키의 구분 [S20]: 업로드 키는 개발자가 보관 — "You use this key to sign your app bundle before uploading it to the Play Console. Google uses it to verify your identity. If compromised or lost, Google can reset this key for you." 앱 서명 키는 Google Play가 보관 — "Google uses this key to sign the final APKs delivered to users' devices."
- 업로드 키 요건: "Must be an RSA key of 2048 bits or more." Google 생성 앱 서명 키는 RSA 4096-bit. [S20]
- 분리 권고: "For maximum security, your upload key and app signing key should be different." [S20]
- **신규 앱에서 필수인가**: "When publishing your app to Google Play for the first time, you must also configure Play App Signing. Play App Signing is optional for apps created before August 2021." / "configuring Play App Signing is required to sign your app for distribution through Google Play (except for apps created before August 2021, which may continue distributing self-signed APKs)." [S21] 배경은 AAB 의무화 — "From August 2021, new apps are required to publish with the Android App Bundle on Google Play." [S22]
- 신규 앱의 기본 동작: "To have Google Play generate an app signing key for you and use it to sign your app, you don't have to do anything. The key you use to sign your first release becomes your upload key, and you should use it to sign future releases." [S21]
- 앱 서명 키 변경 가능 시점: 공개 테스트 또는 프로덕션 트랙에 릴리스가 나가기 전까지. "Once you publish an app to an open track, its signing key is fixed." [S18][S20]
- 업로드 키 분실: "If you lose your upload key or suspect that it was compromised, you are not locked out of your app." Play Console에서 재설정을 요청한다. [S20]
- 키를 회수할 수 없다: "after you configure Play App Signing with either an auto-generated key, or a key that you supply, you cannot retrieve a copy of your app's signing key". [S21]
- 여러 스토어에 같은 키를 쓰려면 Google 생성 대신 자체 키를 올려야 한다. [S21]
- 외부 API 지문 등록: "you must register the Google-held app signing key fingerprint with your API providers, not just your local upload key." [S20]
- 신규 앱은 양자 대비 하이브리드 서명에 자동 등록된다 — "Your app will be automatically enrolled in quantum-ready, hybrid signing with Google-generated keys." 이 경우 API 제공자에 등록할 지문이 세 개다(신규 고전 키, PQC 키, 구형 기기용 고전 키). [S20]

### 질문 7 — 릴리스 노트

- 글자 수 제한(Play 공식): "Note: You can enter release notes using up to 500 Unicode characters per language." [S18]
- 용도 제한: "Inform users about recent updates made in your release. Do not use release notes for promotional purposes or to solicit user actions." [S18]
- Play Console 입력 형식: 언어 태그를 별도 줄에 둔다. [S18]
  ```
  <en-US>
  The release notes description can take up multiple lines.
  </en-US>
  ```
- API 형식: `releaseNotes[]`는 `LocalizedText` 배열이고 `language`는 "a BCP-47 language tag; for example, "de-AT" for Austrian German", `text`는 문자열이다. **API 레퍼런스에는 글자 수 제한이 적혀 있지 않다.** [S13]
- 릴리스 이름: "The release name is used only in Play Console and is not visible to users." API 필드 설명은 "Not required to be unique. If not set, the name is generated from the APK's versionName." [S18][S13]
- 도구별 파일 형식

  | 도구 | 경로·이름 규칙 | 글자 수 제한 언급 |
  |---|---|---|
  | GPP [S7] | `src/[sourceSet]/play/release-notes/[language]/[track].txt`, 트랙 미지정 시 `default.txt` | "the Play Store limits your release notes to a maximum of 500 characters." |
  | fastlane supply [S8] | `fastlane/metadata/android/<locale>/changelogs/<versionCode>.txt`, 없으면 `default.txt` | 문서에 없음 |
  | r0adkll [S9] | `whatsNewDirectory` 안의 `whatsnew-<LOCALE>` (BCP 47) | 문서에 없음 |
  | API 직접 [S13] | 요청 본문 `releaseNotes: [{language, text}]` | 레퍼런스에 없음 |
  | Firebase App Distribution [S23] | `releaseNotes` 문자열 또는 `releaseNotesFile`(평문 파일 하나, 언어 구분 없음) | 문서에 없음 |

- GPP는 릴리스 노트가 없으면 이전 릴리스 것을 복사한다 — "If no release notes are found, GPP will try to copy release notes from the previous release." [S7]
- fastlane은 파일 이름이 versionCode와 정확히 같아야 한다 — "The filename should exactly match the version code of the APK that it represents." [S8]

## 후보 비교

### Play 업로드 도구 4종

| 항목 | (a) Gradle Play Publisher | (b) fastlane supply | (c) r0adkll/upload-google-play | (d) Publishing API 직접 |
|---|---|---|---|---|
| 최신 버전 (확인일 2026-09-30) | 4.1.1 (2026-08-11) | fastlane 2.240.1 (2026-09-15) | v1.1.5 (2026-04-21) | v3 (레퍼런스 갱신 2026-09-28) |
| 유지보수 | README가 "maintenance mode" 선언. 이슈는 무시, PR만 받음. 2026년에 릴리스 3건 | 활발. 2026-06~09 사이 매달 릴리스 | 2024-02 → 2026-04 공백 뒤 재개. 마지막 push 2026-09-10 | Google 소유 |
| 공식 여부 | 비공식("unofficial") | 서드파티 오픈소스 | 서드파티 오픈소스 | Google 공식 |
| AGP 9 지원 | 4.0.0부터 AGP 9 지원 명시. 빌드 기준 AGP 9.0.0. **9.4는 명시 없음** | AGP와 결합 없음(완성된 AAB를 올림). 명시 없음 | AGP와 결합 없음. 명시 없음 | AGP와 무관 |
| Gradle 9.7 | 4.1.1이 "Isolated Projects error in Gradle 9.7" 수정 | 해당 없음 | 해당 없음 | 해당 없음 |
| 설정 위치 | 모듈 `build.gradle.kts`의 `play { }` | `fastlane/Fastfile`·`Appfile` | 워크플로 YAML `with:` | 직접 작성한 스크립트 |
| 추가 런타임 | 없음(Gradle 안에서 동작) | Ruby 3.2 이상 | Node 24(러너 제공) | 호출 코드에 따름 |
| 인증 | 키 JSON 파일 / `ANDROID_PUBLISHER_CREDENTIALS` 환경 변수 / ADC(+가장) | 키 JSON / WIF / ADC (`json_key`, `json_key_data`) | 키 JSON 평문 또는 파일 경로(WIF는 auth 액션의 자격 파일을 넘김) | 서비스 계정 또는 OAuth 클라이언트 |
| 기본 트랙 | `internal` | 문서의 옵션 설명에서 기본값을 확인하지 못함 | `production` | 없음(호출자가 지정) |
| 릴리스 노트 | `play/release-notes/<lang>/<track>.txt` | `changelogs/<versionCode>.txt` | `whatsnew-<LOCALE>` | JSON 본문 |
| 장점 | 빌드와 업로드가 한 Gradle 호출. 버전 충돌 자동 해소, 승격·단계적 출시 태스크 | 유지보수가 가장 활발. 스토어 등록 정보·스크린샷까지 관리. WIF 문서화 | 설정이 가장 짧음. 빌드 도구와 분리돼 AGP 업그레이드의 영향을 안 받음 | 의존성 없음. 공식 계약에만 묶임 |
| 단점 | 유지보수 모드. AGP 내부 API 변경에 노출되는 구조(Gradle 플러그인). 9.4 검증 문장 없음 | Ruby 도구 체인을 CI와 로컬에 추가. Linux/Windows는 "partially supported" | 릴리스 공백 이력. 기본 트랙이 프로덕션이라 지정 누락 시 위험. CI에서만 동작 | 편집 생성·업로드·트랙 갱신·커밋·오류 처리를 직접 구현 |

### 테스터 배포 채널

| 항목 | Play 내부 테스트 트랙 | Firebase App Distribution |
|---|---|---|
| 테스터 수 | 앱당 최대 100명 [S15] | 프로젝트당 500명, 그룹당 200명(무료 증가 요청 가능) [S26] |
| 심사 | "might not be subject to standard Play policy or security reviews" [S15] | Play 심사와 무관. 단 AAB는 Play에 게시된 앱과 연결돼 있어야 함 [S23] |
| 반영 속도 | "within minutes" [S15] | 문서에 수치 없음 |
| 산출물 형식 | AAB(2021-08 이전 앱만 APK 가능) [S18] | APK는 조건 없음. AAB는 Play 연결 필요 [S23] |
| 서명 | 업로드 키로 서명, Play가 앱 서명 키로 재서명 [S20] | APK는 디버그 키 또는 앱 서명 키 [S24]. AAB는 내부 앱 공유를 거쳐 제공 [S23] |
| versionCode | 재사용 불가, 매번 증가 [S1] | 문서에 제약 서술 없음 |
| 디버그 빌드 | 트랙 문서에 서술 없음(내부 앱 공유는 debuggable 허용 [S19]) | APK를 디버그 키로 서명해 배포 가능 [S24] |
| 테스터 조건 | Google 계정 또는 Workspace 계정, 옵트인 링크 [S15] | 이메일 초대, 30일 안에 수락 [S26] |
| 프로덕션과의 관계 | 같은 산출물을 다른 트랙으로 승격 가능 | 프로덕션 배포와 별개 경로 |
| 보관 | 문서에 만료 서술 없음 | 150일 뒤 만료, 앱당 1,000개 [S25] |
| 첫 배포 전 준비 | 앱 설정 완료 전에도 시작 가능 [S15] | Firebase 프로젝트·앱 등록, (AAB면) Play 연결 [S23] |
| 자동화 | 위 업로드 도구 4종 | Gradle 플러그인 5.3.0 / CLI / fastlane 플러그인 / API [S25] |
| 개인 계정 12명·14일 요건 | **충족에 기여하지 않음**(요건은 비공개 테스트 기준) [S16] | 기여하지 않음 |

## 규칙 후보

출처가 뒷받침하는 것
- versionCode는 릴리스마다 이전보다 큰 양의 정수로 올리고 2100000000을 넘기지 않는다. [S1][S2]
- 한 번 Play에 올린 versionCode는 다시 쓰지 않는다(업로드 실패 후 재시도도 새 값). [S1]
- 사용자에게 보이는 버전은 versionName 하나뿐이므로 versionCode를 화면에 노출하지 않는다. [S1]
- 빌드 시점에 버전을 주입할 때는 `androidComponents.onVariants`에서 `variant.outputs`의 `versionCode`/`versionName` 프로퍼티를 설정하고, `applicationVariants`·`versionCodeOverride`를 쓰지 않는다. [S3][S4][S5]
- 버전 값을 계산하는 로직은 `Provider`로 지연 평가한다(설정 단계에서 파일·git을 읽지 않는다). [S3]
- 신규 앱은 AAB로 게시하고 Play App Signing을 쓴다. [S21][S22]
- CI가 보관하는 서명 키는 업로드 키이고 앱 서명 키와 다른 키로 둔다. [S20][S21]
- Maps·OAuth 등 외부 API에는 업로드 키가 아니라 Play가 보관하는 앱 서명 키의 지문을 등록한다. [S20]
- 앱의 첫 업로드는 Play Console에서 수동으로 하고 자동화는 그다음 릴리스부터 적용한다. [S7][S9]
- 자동 업로드의 대상 트랙은 항상 명시한다(도구마다 기본값이 `internal`과 `production`으로 다르다). [S7][S9]
- 단계적 출시는 업데이트에만 쓰고, 비율은 자동으로 오르지 않으므로 올리는 절차를 따로 둔다. [S17]
- 미완료 단계적 출시가 남아 있으면 새 릴리스를 만들 수 없으므로 다음 릴리스 전에 100%로 완료하거나 중단·폐기한다. [S18]
- 릴리스 노트는 언어별 500자(유니코드) 이내로 쓰고 홍보·행동 유도 문구를 넣지 않는다. [S18]
- Play Developer API 접근은 서비스 계정으로 하고 Play Console의 사용자·권한에서 필요한 권한만 준다. [S10]
- 서비스 계정 키 JSON을 저장소에 커밋하지 않는다. [S28][S7]
- GitHub Actions에서 Google 인증은 Workload Identity Federation을 우선하고, 키 JSON은 WIF를 쓸 수 없을 때만 쓴다. [S28][S29][S30][S31]
- WIF를 쓰는 잡에는 `permissions: id-token: write`를 주고 `actions/checkout`을 `google-github-actions/auth`보다 먼저 실행한다. [S30][S31]
- `gha-creds-*.json`을 `.gitignore`에 넣는다. [S30]
- 개인 개발자 계정(2023-11-13 이후 생성)으로 출시하는 앱은 비공개 테스트에 12명 이상을 14일 연속 옵트인시킨 뒤 프로덕션 접근을 신청한다. [S16]
- Firebase App Distribution으로 AAB를 배포하려면 Firebase 앱을 같은 패키지 이름의 Play 앱과 연결하고, 그 Play 앱이 트랙 중 하나에 게시된 상태여야 한다. [S23]
- Firebase App Distribution 설정 블록은 build type·product flavor 안에 둔다(루트 위치는 `firebaseAppDistributionDefault`로 바뀜). [S27]

팩 결정 필요 (출처가 선택지를 주거나 침묵)
- 업로드 도구를 무엇으로 고정할지 — **팩 결정 필요.** 네 후보 모두 AGP 9.4 지원을 문장으로 확인해 주지 않는다. [S7][S8][S9][S14]
- versionCode 부여 방식(수동 증가 / CI 실행 번호 / 날짜 기반 / git 커밋 수) — **팩 결정 필요.** S1은 증가만 요구한다.
- versionName 형식(시맨틱 버저닝 등) — **팩 결정 필요.** S1은 형식을 강제하지 않고 시맨틱 버저닝을 소개만 한다.
- 버전 값을 어디에 둘지(`libs.versions.toml` / `gradle.properties` / 모듈 빌드 파일 / CI 환경 변수) — **팩 결정 필요.**
- 테스터 배포 채널(Play 내부 테스트 / Firebase App Distribution / 둘 다) — **팩 결정 필요.** Firebase를 쓰면 `google-services` 플러그인과 Firebase 프로젝트가 의존성으로 들어온다. [S23][S27]
- 인증 방식을 WIF로 강제할지, 키 JSON 시크릿을 허용할지 — **팩 결정 필요.** Google은 WIF를 권하지만 [S28][S30] R-31-08은 이미 "CI 시크릿 주입"을 규칙으로 두고 있고, Play Developer API 공식 문서는 WIF를 언급하지 않는다. [S10]
- 자동 배포가 닿는 최종 트랙(내부 테스트까지만 자동, 프로덕션 승격은 수동 등) — **팩 결정 필요.**
- 단계적 출시의 시작 비율과 증가 단계 — **팩 결정 필요.** 출처는 수치를 권하지 않는다(GPP 기본값 0.1은 도구 기본값일 뿐). [S7][S17]
- 릴리스 브랜치·태그 이름과 배포 트리거(태그 push / 수동 `workflow_dispatch`) — **팩 결정 필요.**
- 릴리스 노트 파일의 저장 위치와 언어 범위 — **팩 결정 필요.** 형식은 고른 도구를 따른다.

## 출처가 침묵하는 것

- **versionCode를 만드는 방법.** Google 문서는 "더 큰 값"만 요구한다. CI 실행 번호, 날짜, 커밋 수, `major*10000+minor*100+patch` 같은 인코딩 가운데 무엇도 권하지 않는다. [S1]
- **versionName 형식.** 강제 규칙이 없다. [S1]
- **릴리스 브랜치·태그 이름.** 조사한 어떤 공식 문서도 다루지 않는다. 팩이 비워 둔 상태 그대로다.
- **단계적 출시 비율과 관찰 기간.** 몇 %에서 시작해 얼마 뒤 올리라는 수치가 없다. [S17]
- **Play 업로드 도구 추천.** Google은 Publishing API만 제공하고 Gradle 플러그인·CI 액션을 추천하지 않는다. Firebase App Distribution은 공식 Gradle 플러그인이 있지만 Play 업로드에는 공식 Gradle 플러그인이 없다.
- **GitHub Actions용 공식 배포 액션.** Play 업로드와 Firebase App Distribution 모두 Google이 만든 액션이 없다. Google 공식 액션은 인증(`google-github-actions/auth`)까지다. [S25][S30]
- **AGP 9.4 호환 선언.** GPP는 "AGP 9", Firebase 플러그인은 "AGP 9.0.0"까지만 말한다. [S7][S27]
- **Play Developer API에서의 WIF.** Play 쪽 문서는 서비스 계정과 OAuth 클라이언트만 다룬다. [S10]
- **API를 통한 릴리스 노트 글자 수 제한.** 500자는 Play Console 도움말과 GPP README에만 있고 API 레퍼런스에는 없다. [S13][S18]
- **조직 계정의 테스트 요건.** 12명·14일 요건 문서는 개인 계정만 대상으로 적고 조직 계정은 다루지 않는다. [S16]
- **내부 테스트 트랙에서의 debuggable 빌드 허용 여부.** 내부 앱 공유는 허용한다고 적혀 있으나 트랙 문서에는 서술이 없다. [S15][S19]
- **롤백 절차.** versionCode가 내려갈 수 없다는 사실 외에 "문제가 난 릴리스를 어떻게 되돌리나"는 단계적 출시 중단과 "새 릴리스를 올려라"(S17의 Tip)뿐이다.

## 미확인

- **`VariantOutput.versionCode`가 AAB에 반영되는지.** 레퍼런스는 "This only applies to APKs as AARs and Bundles (AABs) do not support multiple outputs."라고만 한다. [S4] `bundleRelease` 산출물의 매니페스트에 주입 값이 들어가는지는 문서로 확정하지 못했다. 규칙으로 쓰기 전에 AGP 9.4.0에서 `bundleRelease` 후 `bundletool dump manifest`로 실증이 필요하다.
- **AGP 9.4.1의 존재.** 9.4 레퍼런스의 `VariantOutput.outputFileName`에 "Added in 9.4.1"이 찍혀 있다. [S4] AGP 9.4.0 릴리스 노트 페이지에는 9.4.1 항목이 없다. [S6] 패치 릴리스가 나왔는지, 레퍼런스 표기가 앞선 것인지 확인하지 못했다. 팩 확정값(9.4.0)은 건드리지 않았다.
- **GPP 4.1.1 + AGP 9.4.0 / Gradle 9.7.1 실제 동작.** 릴리스 노트에 9.4 언급이 없고 빌드 기준이 9.0.0이다. [S7] 저장소 이슈에서 "AGP 9" 제목 검색으로는 9.0 관련 닫힌 이슈 4건(#1181~#1185)만 나왔고 9.4 관련 이슈는 찾지 못했다. 이슈가 없다는 것이 동작 보증은 아니다(README가 이슈를 무시한다고 밝힘). 실빌드 검증 필요.
- **Firebase App Distribution Gradle 플러그인 5.3.0 + AGP 9.4.0.** 5.2.0의 "Fixed compatibility with AGP 9.0.0" 이후 AGP 관련 문장이 없다. [S27] 실빌드 검증 필요. 또한 이 플러그인은 5.2.1부터 Google Services 플러그인 의존을 선언하는데, `com.google.gms.google-services`의 AGP 9.4 호환은 조사하지 않았다.
- **API 트랙 이름 `qa` 대 `internal`.** 공식 가이드(S12)는 내부 테스트 트랙을 `qa`로 적고 도구 문서(S7, S8, S9)는 `internal`로 적는다. API가 두 이름을 모두 받는지, 어느 쪽이 현재 값인지 API를 호출해 확인하지 못했다(인증된 Play 계정 필요).
- **fastlane supply의 `track` 기본값.** 옵션 설명 문장("The default available tracks are: production, beta, alpha, internal")만 확인했고 기본값 열은 읽지 못했다. [S8]
- **Play Console 도움말의 갱신일.** `support.google.com` 페이지는 갱신일을 표시하지 않는다. S2, S15~S20은 2026-09-30에 열어 본 내용이다. 12명·14일 수치는 변경 이력이 있는 값이므로 규칙에 넣을 때 확인일을 함께 적어야 한다.
- **`https://firebase.google.com/docs/app-distribution/limits`** — HTTP 404. 한도는 문제 해결 페이지(S26)와 CI/CD 모범 사례 페이지(S25)에서 확인했다.
- **GitHub 문서(S31)와 fastlane 문서(S8)의 갱신일.** 페이지에 표기가 없다.
- **GitHub 문서(S31)의 예시 액션 핀.** 예시가 `google-github-actions/auth@f1e2d3c4b5a6f7e8d9c0b1a2c3d4e5f6a7b8c9d0`로 돼 있는데 실제 커밋 SHA인지 확인하지 않았다. 팩 예시에는 S30의 `v3` 태그를 쓰는 편이 안전하다.
- **Play Developer API 할당량.** 조사하지 않았다.
- **조사하지 않은 범위.** Play 외 스토어(원스토어, 갤럭시 스토어), 사전 등록, 인앱 업데이트 우선순위(`inAppUpdatePriority`) 운용, ReTrace 매핑 파일·네이티브 디버그 심볼 업로드 자동화.
- **Firebase MCP 서버.** 이 세션에서 연결 시간 초과로 쓰지 못했다. 조사는 공개 문서만으로 했으므로 결과에 영향은 없다.

## 추가 확인과 확정 결정 (2026-09-30, 규칙 작성 단계)

규칙을 쓰면서 아래 두 문서를 직접 받아 문장을 대조했다.

| # | 조직 | 문서명 | URL | 확인 내용 |
|---|---|---|---|---|
| S34 | GitHub | GitHub Actions contexts | https://docs.github.com/en/actions/reference/workflows-and-actions/contexts | `github.run_number` — "A unique number for each run of a particular workflow in a repository. This number begins at 1 for the workflow's first run, and increments with each new run. This number does not change if you re-run the workflow run." |
| S35 | GitHub | Manually running a workflow / Deployments and environments | https://docs.github.com/en/actions/how-tos/manage-workflow-runs/manually-run-a-workflow , https://docs.github.com/en/actions/reference/workflows-and-actions/deployments-and-environments | 수동 실행은 `workflow_dispatch`가 있는 워크플로만 가능하고 쓰기 권한이 필요하다. "If you are on a GitHub Free, GitHub Pro, or GitHub Team plan, required reviewers are only available for public repositories." |

사용자 확정(2026-09-30): 업로드 도구 `r0adkll/upload-google-play`, 테스터 채널 Play 내부 테스트만, versionCode는 CI 실행 번호, **업로드는 사용자가 승인했을 때만**. 비공개 저장소에서는 required reviewers를 쓸 수 없어 승인을 `workflow_dispatch` 수동 실행으로 구현했다(R-31-10).

versionCode는 `onVariants`가 아니라 `defaultConfig.versionCode`에서 Gradle 속성을 읽게 했다(R-31-11). 위 "미확인"의 "AAB에 버전 주입 반영 여부"는 `VariantOutput` 경로에 대한 것이라 이 방식에는 해당하지 않는다. 다만 이 방식도 실빌드로는 아직 확인하지 않았다.

### 변경 (2026-09-30, 같은 날 후속)

사용자가 "배포 프로세스를 안 하더라도 문제 없도록"이라고 해서 실행 위치를 **로컬 스크립트 기본, GitHub 워크플로 선택**으로 바꿨다(`RUN_LOCATION`). 이유는 비공개 저장소의 GitHub 호스팅 러너 사용이 요금제 포함 분량에서 차감되고 결제 수단이 있는 계정은 초과분이 청구되기 때문이다.
- 업로드는 Play Console에 사람이 직접 하는 것이 기본이다. `r0adkll/upload-google-play` 워크플로는 선택 파일로 남았다.
- versionCode는 CI 실행 번호에서 **저장소의 카운터 파일**(`distribution/version-code.txt`)로 바꿨다. 로컬과 워크플로가 같은 값을 읽는다. 위 S34(`github.run_number` 문서)는 더 이상 규칙이 인용하지 않는다.
- 새로 확인한 문서: githooks(pre-push·`core.hooksPath`) — 팩 출처 S162.

