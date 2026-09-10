package {{package}}.feature.{{feature}}

import app.cash.turbine.test
import {{package}}.core.testing.MainDispatcherRule
import {{package}}.feature.{{feature}}.domain.Get{{Feature}}UseCase
import {{package}}.feature.{{feature}}.model.{{Feature}}
import {{package}}.feature.{{feature}}.model.{{Feature}}Error
import {{package}}.feature.{{feature}}.model.{{Feature}}Result
import {{package}}.feature.{{feature}}.ui.{{Feature}}Event
import {{package}}.feature.{{feature}}.ui.{{Feature}}ViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/** 로드는 `init`이 아니라 `initialize()`가 시작하므로 (R-12-07) 테스트가 직접 부른다. */
class {{Feature}}ViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repository = Fake{{Feature}}Repository()

    private fun viewModel() = {{Feature}}ViewModel(Get{{Feature}}UseCase(repository))

    @Test
    fun `성공 시 data가 채워지고 로딩이 끝난다`() = runTest {
        val viewModel = viewModel()
        viewModel.initialize()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals({{Feature}}(id = "1", name = "fake"), state.data)
            assertEquals(false, state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `실패 시 error가 채워진다`() = runTest {
        repository.result = {{Feature}}Result.Failure({{Feature}}Error.NotFound)
        val viewModel = viewModel()
        viewModel.initialize()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals({{Feature}}Error.NotFound, state.error)
            assertNull(state.data)
        }
    }

    @Test
    fun `Retry 이벤트는 다시 로드한다`() = runTest {
        val viewModel = viewModel()
        viewModel.initialize()
        viewModel.onEvent({{Feature}}Event.Retry)
        assertEquals(2, repository.callCount)
    }
}
