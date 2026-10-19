package {{package}}.core.designsystem.motion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
 * 푸시·팝은 Android Activity 기본 전환 모양을 따른다 — 새 화면이 오른쪽에서 폭 전체로 들어와
 * 이전 화면을 불투명하게 덮고, 이전 화면은 왼쪽으로 [PARALLAX_DIVISOR] 분의 1만큼 밀린다. 뒤로 가면 위 화면이 오른쪽으로 빠지고
 * 아래 화면이 왼쪽에서 돌아온다. 페이드는 쓰지 않는다. 시스템 설정 앱의 전환을 녹화해 프레임으로 비교해 정한 모양이다(2026-10-15).
 * 밀리는 거리(1/4)와 시간(300ms)은 그 녹화를 눈으로 맞춘 값이다. 푸시·팝의 들어옴·나감은 같은 곡선·시간을 써서 두 화면 사이에
 * 틈이 벌어지지 않게 한다.
 */
object NavTransitions {
    private const val PARALLAX_DIVISOR = 4

    // fade through 의 105/195ms 분할은 Material 예측형 뒤로가기의 35% 경계를 같은 비율로 가져온 값이다
    // (Material 의 fade through 문서는 분할을 적지 않는다).
    private const val FADE_THROUGH_OUT_MS = Motion.STANDARD_MS * 35 / 100
    private const val FADE_THROUGH_IN_MS = Motion.STANDARD_MS - FADE_THROUGH_OUT_MS

    private const val TOP = 1f
    private const val BOTTOM = -1f

    private val fadeOutSpec = tween<Float>(FADE_THROUGH_OUT_MS, easing = Motion.exitEasing)
    private val fadeInSpec = tween<Float>(
        durationMillis = FADE_THROUGH_IN_MS,
        delayMillis = FADE_THROUGH_OUT_MS,
        easing = Motion.enterEasing,
    )

    /**
     * 앞으로: 새 화면이 오른쪽에서 폭 전체로 들어와 덮고, 이전 화면은 왼쪽으로 조금 밀린다. 새 화면이 위에 그려진다.
     * 화면마다 불투명한 배경이 있어야 덮는 모양이 된다(`:app` 의 엔트리 데코레이터가 깐다).
     */
    val push: ContentTransform = (
        slideInHorizontally(Motion.enter()) { it } togetherWith
            slideOutHorizontally(Motion.enter()) { -it / PARALLAX_DIVISOR }
        ).apply { targetContentZIndex = TOP }

    /** 뒤로: 위 화면이 오른쪽으로 빠지고 아래 화면이 왼쪽에서 돌아온다. 나가는 화면이 위에 그려진다. */
    val pop: ContentTransform = (
        slideInHorizontally(Motion.enter()) { -it / PARALLAX_DIVISOR } togetherWith
            slideOutHorizontally(Motion.enter()) { it }
        ).apply { targetContentZIndex = BOTTOM }

    /**
     * 최상위 목적지(하단 탭) 사이 이동: 나가는 화면은 그대로 두고 들어오는 화면이 그 위에서 페이드인한다.
     * 하단 바가 각 탭 화면 안에 그려지므로 [fadeThrough] 처럼 나가는 화면을 먼저 지우면 바가 잠깐 사라졌다 나타난다 — 두 화면의 바가
     * 거의 같아서 위에서 덮어 가며 나타나면 바는 흔들리지 않는다. 나가는 쪽의 `fadeOut(targetAlpha = 1f)` 는 투명도를 바꾸지 않고
     * 전환 시간 동안 화면을 붙들어 두는 용도다.
     */
    // 아직 안 쓴다(미검증): 2026-10-15 탭 전환을 `:app` 의 탭 컨테이너(탭 화면을 살려 두고 알파로 페이드인)로 옮겨 이 전환은 쓰이지 않는다.
    val tabSwitch: ContentTransform = (
        fadeIn(Motion.enter()) togetherWith fadeOut(Motion.enter(), targetAlpha = 1f)
        ).apply { targetContentZIndex = TOP }

    /** 스택 교체(백스택을 비우고 시작 화면으로): 나가는 화면이 먼저 사라진 뒤 들어오는 화면이 나타난다. */
    val fadeThrough: ContentTransform = fadeIn(fadeInSpec) togetherWith fadeOut(fadeOutSpec)

    /** 모달 전체 화면이 닫힐 때: 아래로 내려가며 밑의 화면이 드러난다. */
    val modalPop: ContentTransform =
        EnterTransition.None togetherWith slideOutVertically(Motion.exit()) { it }

    /**
     * 예측형 뒤로가기: 뒤로 키·제스처 모두 [pop] 과 같은 모양이다. 이 앱은 예측형 뒤로가기가 켜져 있어 뒤로 키도 이 전환을 쓰므로,
     * 팩의 공식 수치(R-28-03: 90%로 줄고 110%에서 들어옴)를 쓰면 "위 화면이 오른쪽으로 빠지는" 모양이 나오지 않는다.
     */
    val predictivePop: ContentTransform = pop
}

/**
 * 모달 전체 화면이 열릴 때: 아래에서 올라오고 아래 화면은 전환이 끝날 때까지 그대로 둔다. 공식 레시피의
 * `ExitTransition.KeepUntilTransitionsFinished` 는 companion 멤버는 internal 이고 이 스코프의 확장 속성만
 * 공개라서, 스코프 밖의 상수로는 만들 수 없다.
 */
fun AnimatedContentTransitionScope<*>.modalPush(): ContentTransform =
    slideInVertically(Motion.enter()) { it } togetherWith
        ExitTransition.KeepUntilTransitionsFinished
