package {{package}}.core.designsystem.motion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

/**
 * 화면 이동 전환 모음. `:app` 의 `NavDisplay` 한 곳에서만 꽂는다 (R-28-01~03).
 *
 * ```
 * NavDisplay(
 *     transitionSpec = { NavTransitions.push },
 *     popTransitionSpec = { NavTransitions.pop },
 *     predictivePopTransitionSpec = { NavTransitions.predictivePop },
 *     ...
 * )
 * ```
 * 모달 화면과 최상위 목적지(탭)는 그 엔트리의 metadata 전환 키로 덮어쓴다 (R-28-02). 모달이 열릴 때의
 * [modalPush] 는 `AnimatedContentTransitionScope` 안에서만 쓸 수 있는 공식 확장 속성에 기대므로 객체 멤버가
 * 아니라 확장 함수다 — metadata 람다 안에서 `{ modalPush() }` 로 부른다.
 *
 * 슬라이드 거리(화면 폭의 1/10)는 공식 문서가 정하지 않은 첫 값이라 실기기에서 조정해야 한다.
 */
object NavTransitions {
    private const val SLIDE_DIVISOR = 10

    // 예측형 뒤로가기 공식 수치 — 나가는 쪽 100→90%, 들어오는 쪽 110→100%, fade-through 경계 35%.
    // fade through 의 105/195ms 분할은 이 35% 경계를 같은 비율로 가져온 값이다(Material 의 fade through
    // 문서는 분할을 적지 않는다).
    private const val PREDICTIVE_EXIT_SCALE = 0.9f
    private const val PREDICTIVE_ENTER_SCALE = 1.1f
    private const val FADE_THROUGH_OUT_MS = Motion.STANDARD_MS * 35 / 100
    private const val FADE_THROUGH_IN_MS = Motion.STANDARD_MS - FADE_THROUGH_OUT_MS

    private val predictiveEasing = CubicBezierEasing(0.1f, 0.1f, 0f, 1f)
    private val fadeOutSpec = tween<Float>(FADE_THROUGH_OUT_MS, easing = Motion.exitEasing)
    private val fadeInSpec = tween<Float>(
        durationMillis = FADE_THROUGH_IN_MS,
        delayMillis = FADE_THROUGH_OUT_MS,
        easing = Motion.enterEasing,
    )
    private val predictiveSpec = tween<Float>(Motion.STANDARD_MS, easing = predictiveEasing)
    private val predictiveFadeOutSpec = tween<Float>(FADE_THROUGH_OUT_MS, easing = predictiveEasing)
    private val predictiveFadeInSpec = tween<Float>(
        durationMillis = FADE_THROUGH_IN_MS,
        delayMillis = FADE_THROUGH_OUT_MS,
        easing = predictiveEasing,
    )

    /** 앞으로: 오른쪽에서 들어오며 페이드, 나가는 화면은 왼쪽으로 밀리며 페이드 (shared axis X). */
    val push: ContentTransform = horizontal(direction = 1)

    /** 뒤로: [push] 의 반대 방향. */
    val pop: ContentTransform = horizontal(direction = -1)

    /** 최상위 목적지 간·스택 교체: 나가는 화면이 먼저 사라진 뒤 들어오는 화면이 나타난다. */
    val fadeThrough: ContentTransform = fadeIn(fadeInSpec) togetherWith fadeOut(fadeOutSpec)

    /** 모달 전체 화면이 닫힐 때: 아래로 내려가며 밑의 화면이 드러난다. */
    val modalPop: ContentTransform =
        EnterTransition.None togetherWith slideOutVertically(Motion.exit()) { it }

    /** 예측형 뒤로가기(전체 화면): 나가는 화면은 90%로 줄며 사라지고, 들어오는 화면은 110%에서 줄며 나타난다. */
    val predictivePop: ContentTransform = predictive()

    private fun predictive(): ContentTransform {
        val enter = scaleIn(predictiveSpec, PREDICTIVE_ENTER_SCALE) + fadeIn(predictiveFadeInSpec)
        val exit = scaleOut(predictiveSpec, PREDICTIVE_EXIT_SCALE) + fadeOut(predictiveFadeOutSpec)
        return enter togetherWith exit
    }

    private fun horizontal(direction: Int): ContentTransform {
        val enter = slideInHorizontally(Motion.enter()) { direction * it / SLIDE_DIVISOR } +
            fadeIn(Motion.enter())
        val exit = slideOutHorizontally(Motion.exit()) { -direction * it / SLIDE_DIVISOR } +
            fadeOut(Motion.exit())
        return enter togetherWith exit
    }
}

/**
 * 모달 전체 화면이 열릴 때: 아래에서 올라오고 아래 화면은 전환이 끝날 때까지 그대로 둔다. 공식 레시피의
 * `ExitTransition.KeepUntilTransitionsFinished` 는 companion 멤버는 internal 이고 이 스코프의 확장 속성만
 * 공개라서, 스코프 밖의 상수로는 만들 수 없다.
 */
fun AnimatedContentTransitionScope<*>.modalPush(): ContentTransform =
    slideInVertically(Motion.enter()) { it } togetherWith
        ExitTransition.KeepUntilTransitionsFinished
