package {{package}}.core.designsystem.motion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * 햅틱 전역 스위치 (R-28-20). 기본은 꺼짐이고, 나중에 설정 화면이나 실험 플래그가 앱 루트에서 한 곳만
 * 켠다. 디자인 시스템 컴포넌트는 이 값이 true 일 때만 `LocalHapticFeedback` 을 부른다.
 */
val LocalHapticsEnabled = staticCompositionLocalOf { false }

private const val PRESS_DISTANCE_DP = 2
private const val PRESS_MIN_BASIS_DP = 24
private const val PRESS_WIDTH_DIVISOR = 4

/**
 * 눌린 동안 요소가 정해진 **거리**(2dp)만큼 줄어든다 — 배율이 아니라 거리라서 큰 요소와 작은 요소의 눌린
 * 느낌이 같다 (R-28-18). 기준 길이는 `max(높이, 폭 ÷ 4, 24dp)`이고 배율은 `(기준 − 2) ÷ 기준`이다.
 * 레이아웃 단계가 아니라 그리기 단계에서만 바뀌므로 요소가 차지하는 자리는 변하지 않는다 (R-28-16).
 * 눌림 색 변화는 이 확장이 아니라 컴포넌트가 `MaterialTheme.colorScheme` 역할로 따로 준다.
 */
@Composable
fun Modifier.pressScale(interactionSource: InteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val progress = animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = Motion.fast(),
        label = "pressScale",
    )
    return graphicsLayer {
        val basis = maxOf(
            size.height,
            size.width / PRESS_WIDTH_DIVISOR,
            PRESS_MIN_BASIS_DP.dp.toPx(),
        )
        val scale = 1f - progress.value * PRESS_DISTANCE_DP.dp.toPx() / basis
        scaleX = scale
        scaleY = scale
    }
}
