package {{package}}

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * `:app` 의 `src/androidTest` 로 간다. 계측 테스트를 Hilt 테스트 애플리케이션 위에서 돌린다 (R-30-14).
 * `:app/build.gradle.kts` 의 `testInstrumentationRunner` 에 이 클래스의 전체 이름을 적는다.
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
