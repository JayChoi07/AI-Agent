package {{package}}.core.designsystem.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * 모션 토큰의 **유일한 정의처** (R-28-13, R-28-14). 화면과 feature 는 `tween`·`spring` 에 숫자를 직접
 * 쓰지 않고 이 객체의 함수만 부른다 — Konsist 테스트가 `:core:designsystem` 밖의 숫자 리터럴을 막는다.
 *
 * 값의 출처: 곡선과 시간은 Material 모션 표(곡선 3개, 시간 Short3·Short4·Medium2)이고, spring 은 Material
 * spatial spring 3단(fast·default·slow)이다. 어느 값을 쓸지는 사용자 결정이다(91-decisions MOTION_TOKENS).
 * Material 표는 Views 테마 속성이라 Compose 에는 값만 옮겼다. 실기기에서 조정하지 않은 값이다.
 */
object Motion {
    /** 눌림 피드백·작은 요소의 색 변화. Material Short3. */
    const val FAST_MS = 150

    /** 오버레이·요소의 퇴장. 등장(300)보다 짧게 한다 (R-28-12). Material Short4. */
    const val EXIT_MS = 200

    /** 화면 전환·오버레이 등장의 표준 시간. Material Medium2. */
    const val STANDARD_MS = 300

    /** 이 시간 안에 끝나는 로딩은 표시하지 않는다 (R-28-19). 표준 시간과 같은 값이지만 따로 둔다. */
    const val LOADING_DELAY_MS = 300

    /** 화면 안에서 시작하고 끝나는 움직임. */
    val standardEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** 화면에 들어오는 움직임(감속). */
    val enterEasing = CubicBezierEasing(0f, 0f, 0f, 1f)

    /** 화면에서 나가는 움직임(가속). */
    val exitEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)

    /** 화면 안의 일반 움직임(표준 시간 + 표준 곡선). */
    fun <T> standard(): FiniteAnimationSpec<T> = tween(STANDARD_MS, easing = standardEasing)

    /** 들어오는 움직임(표준 시간 + 감속). */
    fun <T> enter(): FiniteAnimationSpec<T> = tween(STANDARD_MS, easing = enterEasing)

    /** 나가는 움직임(짧은 시간 + 가속). */
    fun <T> exit(): FiniteAnimationSpec<T> = tween(EXIT_MS, easing = exitEasing)

    /** 눌림·색 변화처럼 작고 빠른 움직임. */
    fun <T> fast(): FiniteAnimationSpec<T> = tween(FAST_MS, easing = standardEasing)

    /** 손에 이어지는 작은 요소의 움직임(스위치·버튼). Material fast spatial. */
    fun <T> quickSpring(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.9f, stiffness = 1400f)

    /** 손에 이어지는 중간 크기 움직임(시트 스냅·드래그 복귀). Material default spatial. */
    fun <T> basicSpring(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.9f, stiffness = 700f)

    /** 손에 이어지는 전체 화면 움직임. Material slow spatial. */
    fun <T> slowSpring(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.9f, stiffness = 300f)
}
