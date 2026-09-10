package {{package}}.feature.{{feature}}.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import {{package}}.feature.{{feature}}.domain.Get{{Feature}}UseCase
import {{package}}.feature.{{feature}}.model.{{Feature}}
import {{package}}.feature.{{feature}}.model.{{Feature}}Error
import {{package}}.feature.{{feature}}.model.{{Feature}}Result
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI(순수 구현) 변형. 기본은 ui/{{Feature}}ViewModel.kt이고, R-12-02 매트릭스가 MVI를 가리킬 때만 이 파일로 바꾼다.
 * 라이브러리 없이 sealed Intent + private reduce + Channel SideEffect 세 요소를 이 파일에 함께 둔다 (R-12-11).
 * 패키지·클래스 이름이 기본형과 같으므로 둘 중 하나만 복사한다. 함께 고칠 곳 4군데:
 * 1) {{Feature}}Route.kt: `onEvent = viewModel::onEvent` → `onIntent = viewModel::dispatch`
 * 2) {{Feature}}Screen.kt: 파라미터 `onEvent: ({{Feature}}Event) -> Unit` → `onIntent: ({{Feature}}Intent) -> Unit`
 * 3) {{Feature}}ViewModelTest.kt: `onEvent({{Feature}}Event.Retry)` → `dispatch({{Feature}}Intent.Retry)`
 * 4) {{Feature}}ScreenshotTest.kt: `onEvent = {}` → `onIntent = {}` (2군데)
 * 일회성 이벤트(스낵바·화면 이동)가 없다면 SideEffect Channel은 지운다.
 * 최초 로드는 `init`이 아니라 Route가 부르는 멱등 `initialize()`가 시작한다 (R-12-07).
 */
sealed interface {{Feature}}Intent {
    data object Load : {{Feature}}Intent
    data object Retry : {{Feature}}Intent
}

/** 일회성 이벤트. 메시지 문자열이 아니라 에러 타입을 넘겨 문구 매핑은 UI가 한다 (R-23). */
sealed interface {{Feature}}SideEffect {
    data class ShowError(val error: {{Feature}}Error) : {{Feature}}SideEffect
}

@HiltViewModel
class {{Feature}}ViewModel @Inject constructor(
    private val get{{Feature}}: Get{{Feature}}UseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow({{Feature}}UiState())
    val uiState: StateFlow<{{Feature}}UiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<{{Feature}}SideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<{{Feature}}SideEffect> = _sideEffect.receiveAsFlow()

    private var initialized = false

    /** 여러 번 불려도 최초 1회만 Load Intent를 흘린다 (R-12-07). */
    fun initialize() {
        if (initialized) return
        initialized = true
        dispatch({{Feature}}Intent.Load)
    }

    fun dispatch(intent: {{Feature}}Intent) {
        when (intent) {
            {{Feature}}Intent.Load, {{Feature}}Intent.Retry -> load()
        }
    }

    private fun load() = viewModelScope.launch {
        _uiState.update { reduceLoading(it) }
        when (val result = get{{Feature}}()) {
            is {{Feature}}Result.Success -> _uiState.update { reduceLoaded(it, result.data) }

            is {{Feature}}Result.Failure -> {
                _uiState.update { reduceError(it, result.error) }
                _sideEffect.send({{Feature}}SideEffect.ShowError(result.error))
            }
        }
    }

    private fun reduceLoading(state: {{Feature}}UiState) = state.copy(isLoading = true, error = null)

    private fun reduceLoaded(state: {{Feature}}UiState, data: {{Feature}}) =
        state.copy(isLoading = false, data = data)

    private fun reduceError(state: {{Feature}}UiState, error: {{Feature}}Error) =
        state.copy(isLoading = false, error = error)
}
