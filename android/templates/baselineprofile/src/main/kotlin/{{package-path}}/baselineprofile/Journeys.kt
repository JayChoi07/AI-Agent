package {{package}}.baselineprofile

import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.boundsInScreen
import androidx.test.uiautomator.textAsString

/**
 * 프로파일 생성기와 벤치마크가 함께 쓰는 사용자 여정(CUJ) 조각 (R-41-17).
 * 여정을 한 곳에 두면 생성기와 벤치마크가 같은 동작을 돈다.
 *
 * 이 모듈은 앱 코드를 컴파일하지 않으므로 R 을 못 쓴다. 요소는 화면에 보이는 글자
 * (`text`나 `contentDescription`)로 찾는다. **[Labels] 의 글자는 앱마다 다르다** —
 * 값은 전부 자리 표시이므로 앱의 실제 글자로 바꾼다. 글자가 바뀌면 [Labels] 만 고친다.
 * 대상은 릴리스 변이이고, 화면에 더미·고정 데이터가 보이는 상태에서 도는 것을 전제한다.
 *
 * 여기 있는 동작(첫 화면 대기·두 번째 화면 푸시·팝·탭 전환·스크롤)은 R-41-02 의 대상 동작 중
 * 앱 골격이 공통으로 가진 것이다. 앱에 맞는 여정(목록 항목 열기, 긴 작업의 진행 화면 등)은
 * 이 파일에 같은 모양의 함수로 더하고, 없는 화면(하단 탭 등)의 함수는 지운다.
 */
internal const val TARGET_PACKAGE = "{{package}}"

internal object Labels {
    /** 첫 화면에만 보이는 글자. 이것이 보이면 첫 화면이 그려졌다는 신호다. */
    const val FIRST_SCREEN = "(첫 화면에만 보이는 글자)"

    /** 첫 화면에서 두 번째 화면을 여는 요소의 글자. 푸시·팝 여정이 누른다. */
    const val OPEN_SECOND = "(두 번째 화면을 여는 글자)"

    /** 두 번째 화면이 떴다는 신호(제목 등). */
    const val SECOND_TITLE = "(두 번째 화면 제목)"

    /** 하단 탭 라벨. 시작 탭과 다른 탭 하나. 하단 탭이 없는 앱은 탭 여정을 지운다. */
    const val TAB_FIRST = "(시작 탭 라벨)"
    const val TAB_OTHER = "(다른 탭 라벨)"
}

/** 하단 탭 막대는 화면 아래쪽 이 비율보다 아래에 있다. 상단 제목과 같은 글자를 가려내는 데 쓴다. */
private const val TAB_BAR_TOP_FRACTION = 0.8f

/** 탭 화면의 제목은 화면 위쪽 이 비율보다 위에 있다. */
private const val TITLE_BOTTOM_FRACTION = 0.25f

/** 목록을 스크롤하는 손가락의 시작·끝 높이(화면 높이 비율)와 swipe 단계 수. 단계가 적을수록 플링에 가깝다. */
private const val SWIPE_FROM_FRACTION = 0.75f
private const val SWIPE_TO_FRACTION = 0.30f
private const val SWIPE_STEPS_SLOW = 30
private const val SWIPE_STEPS_FLING = 8

/** 전환(300ms)과 새 화면의 첫 프레임이 끝날 시간. */
private const val SETTLE_MS = 700L

/** 스크롤 한 번 뒤에 쉬는 시간. */
private const val SCROLL_SETTLE_MS = 350L

/** 있을 수도 없을 수도 있는 요소를 기다리는 시간. 필수 요소는 라이브러리 기본 제한 시간을 쓴다. */
private const val OPTIONAL_WAIT_MS = 3_000L

private fun AccessibilityNodeInfo.hasLabel(label: String): Boolean =
    textAsString() == label || contentDescription?.toString() == label

private fun settle(ms: Long = SETTLE_MS) = SystemClock.sleep(ms)

/** 첫 화면이 그려질 때까지 기다린다. 앱을 막 띄운 직후와 첫 화면으로 돌아온 직후에 부른다. */
internal fun UiAutomatorTestScope.waitForFirstScreen() {
    onElement { hasLabel(Labels.FIRST_SCREEN) }
    settle()
}

/** 첫 화면 → [Labels.OPEN_SECOND] → 두 번째 화면 → 뒤로. 화면 푸시·팝 한 번이다. */
internal fun UiAutomatorTestScope.openSecondAndBack() {
    onElement { hasLabel(Labels.OPEN_SECOND) }.click()
    onElement { hasLabel(Labels.SECOND_TITLE) }
    settle()
    pressBack()
    waitForFirstScreen()
}

/**
 * 하단 탭을 누르고 그 화면의 제목이 위쪽에 보일 때까지 기다린다.
 * 시작 탭은 첫 화면이 그려졌다는 신호([waitForFirstScreen])를 기다린다.
 */
internal fun UiAutomatorTestScope.tapTab(label: String) {
    val tabBarTop = device.displayHeight * TAB_BAR_TOP_FRACTION
    onElement { hasLabel(label) && boundsInScreen().centerY() > tabBarTop }.click()
    if (label == Labels.TAB_FIRST) {
        waitForFirstScreen()
    } else {
        val titleBottom = device.displayHeight * TITLE_BOTTOM_FRACTION
        onElementOrNull(OPTIONAL_WAIT_MS) {
            hasLabel(label) &&
                boundsInScreen().centerY() < titleBottom
        }
        settle()
    }
}

/** 현재 화면의 목록을 느리게 [times] 번 내렸다가 빠른 플링으로 같은 횟수만큼 올려 제자리로 돌려놓는다. */
internal fun UiAutomatorTestScope.scrollContent(times: Int = 2) {
    val x = device.displayWidth / 2
    val from = (device.displayHeight * SWIPE_FROM_FRACTION).toInt()
    val to = (device.displayHeight * SWIPE_TO_FRACTION).toInt()
    repeat(times) {
        device.swipe(x, from, x, to, SWIPE_STEPS_SLOW)
        settle(SCROLL_SETTLE_MS)
    }
    repeat(times) {
        device.swipe(x, to, x, from, SWIPE_STEPS_FLING)
        settle(SCROLL_SETTLE_MS)
    }
}
