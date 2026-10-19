package {{package}}.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 릴리스 `{{package}}` 의 Baseline Profile 을 만든다 (R-40-03, R-41-17).
 * 실행은 기기를 꽂고 `./gradlew :app:generateBaselineProfile` 이다.
 * 결과는 `:app` 의 `baselineProfile { saveInSrc }` 설정에 따라
 * `app/src/main/generated/baselineProfiles/` 에 저장된다.
 *
 * 시작 경로([startup])와 나머지 여정([journeys])을 따로 둔다. 시작 경로만 Startup Profile
 * (dex 배치 최적화)에 넣고 나머지는 넣지 않는 것이 공식 권고다(`includeInStartupProfile`).
 * 프로파일 대상은 시작뿐 아니라 화면 이동·스크롤이다(R-41-17). 여정은 `Journeys.kt` 의 조각으로 짠다.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    /** 콜드 스타트 → 첫 화면이 그려질 때까지. */
    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = TARGET_PACKAGE,
        includeInStartupProfile = true,
    ) {
        startApp(TARGET_PACKAGE)
        waitForFirstScreen()
    }

    /** 첫 화면 스크롤 → 두 번째 화면 푸시·팝 → 다른 탭 스크롤 → 시작 탭으로 복귀. */
    @Test
    fun journeys() = baselineProfileRule.collect(packageName = TARGET_PACKAGE) {
        startApp(TARGET_PACKAGE)
        waitForFirstScreen()
        scrollContent()

        openSecondAndBack()

        tapTab(Labels.TAB_OTHER)
        scrollContent()
        tapTab(Labels.TAB_FIRST)
    }
}
