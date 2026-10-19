package {{package}}.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 콜드 스타트 시간(`StartupTimingMetric`: TTID)을 프로파일 없음·있음으로 비교한다 (R-41-01, R-41-02).
 * 이 템플릿은 `reportFullyDrawn` 을 부르지 않으므로 TTFD 는 나오지 않는다.
 * `Require` 는 프로파일이 앱에 없으면 실패한다 — [FrameTimingBenchmark] 의 설명과 같다.
 * 콜드 스타트 시간은 측정 블록에서 앱을 띄워야 재어진다(`StartupMode.COLD`).
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() = startup(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfile() = startup(
        CompilationMode.Partial(BaselineProfileMode.Require),
    )

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        startupMode = StartupMode.COLD,
        iterations = ITERATIONS,
    ) {
        startApp(TARGET_PACKAGE)
        waitForFirstScreen()
    }

    private companion object {
        /** 반복 10회 이상은 제안값이다(R-41-02). */
        const val ITERATIONS = 10
    }
}
