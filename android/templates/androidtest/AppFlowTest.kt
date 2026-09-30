package {{package}}

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

/**
 * `:app` 의 `src/androidTest` 로 간다. 루트 Activity 를 띄워 사용자처럼 이동하는 대표 플로우만 둔다
 * (R-30-12, R-30-13). 데이터 계층은 `@TestInstallIn` 모듈로 fake 에 바꿔 끼운다 (R-30-15).
 */
@HiltAndroidTest
class AppFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launch_showsFirstScreen() {
        // 기다려야 하면 Thread.sleep 이 아니라 composeRule.waitUntil* 를 쓴다 (R-30-16).
        composeRule.onNodeWithText(FIRST_SCREEN_TEXT).assertIsDisplayed()
    }

    private companion object {
        /** 첫 화면에 항상 보이는 문구로 바꾼다. */
        const val FIRST_SCREEN_TEXT = "{{Feature}}"
    }
}
