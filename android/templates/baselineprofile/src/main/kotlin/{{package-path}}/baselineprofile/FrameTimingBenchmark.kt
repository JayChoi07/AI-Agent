package {{package}}.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 화면 전환의 프레임 시간(`FrameTimingMetric`)을 컴파일 모드 둘로 잰다 (R-41-01, R-41-02) —
 * 프로파일 없음([CompilationMode.None])과 있음([CompilationMode.Partial] + [BaselineProfileMode.Require]).
 *
 * 매 반복이 프로세스를 새로 띄운 직후의 **첫 진입**을 잰다. `StartupMode.COLD` 는 setup 과 measure
 * 사이에 프로세스를 죽이므로 쓰지 않고, `startupMode = null` 에 setup 이 `killProcess()` 로
 * 프로세스를 새로 띄워 첫 화면까지 연다. 측정 블록은 이동 동작만 한다(공식: Write a Macrobenchmark).
 * 이 구성은 기기에서 돌려 보지 않았다(미확인).
 *
 * `Require` 는 프로파일이 앱에 없으면 실패한다 — 먼저 `:app:generateBaselineProfile` 로 프로파일을 만든
 * 뒤 잰다. 실행은 `./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest` 이다 — 프로젝트가
 * 쓰는 이름이고 기기에서 돌려 보지 않았다(미확인). 태스크 이름은 `./gradlew :baselineprofile:tasks --all` 로 본다.
 */
@RunWith(AndroidJUnit4::class)
class FrameTimingBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun secondScreenPushPopCompilationNone() =
        measure(CompilationMode.None()) { openSecondAndBack() }

    @Test
    fun secondScreenPushPopCompilationBaselineProfile() =
        measure(CompilationMode.Partial(BaselineProfileMode.Require)) { openSecondAndBack() }

    @Test
    fun otherTabCompilationNone() = measure(CompilationMode.None()) { tabRoundTrip() }

    @Test
    fun otherTabCompilationBaselineProfile() =
        measure(CompilationMode.Partial(BaselineProfileMode.Require)) { tabRoundTrip() }

    private fun measure(compilationMode: CompilationMode, action: MacrobenchmarkScope.() -> Unit) =
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            startupMode = null,
            iterations = ITERATIONS,
            setupBlock = {
                killProcess()
                startApp(TARGET_PACKAGE)
                waitForFirstScreen()
            },
            measureBlock = action,
        )

    private fun MacrobenchmarkScope.tabRoundTrip() {
        tapTab(Labels.TAB_OTHER)
        tapTab(Labels.TAB_FIRST)
    }

    private companion object {
        /** 반복 10회 이상은 제안값이다(R-41-02). */
        const val ITERATIONS = 10
    }
}
