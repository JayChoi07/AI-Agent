package {{package}}.feature.{{feature}}.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import {{package}}.feature.{{feature}}.domain.Get{{Feature}}UseCase
import {{package}}.feature.{{feature}}.model.{{Feature}}Result
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 기본 골격은 MVVM-UDF다. MVI가 필요하면 ui/mvi 변형으로 교체한다 (R-12-02 판단 매트릭스).
 * 최초 로드는 `init`이 아니라 UI가 부르는 멱등 `initialize()`가 시작한다 (R-12-07).
 * {{Feature}}Route의 `LaunchedEffect(viewModel) { viewModel.initialize() }`가 호출한다.
 */
@HiltViewModel
class {{Feature}}ViewModel @Inject constructor(
    private val get{{Feature}}: Get{{Feature}}UseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow({{Feature}}UiState())
    val uiState: StateFlow<{{Feature}}UiState> = _uiState.asStateFlow()

    private var initialized = false

    /** 여러 번 불려도 최초 1회만 로드한다 (R-12-07). */
    fun initialize() {
        if (initialized) return
        initialized = true
        load()
    }

    fun onEvent(event: {{Feature}}Event) {
        when (event) {
            {{Feature}}Event.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = get{{Feature}}()) {
                is {{Feature}}Result.Success ->
                    _uiState.update { it.copy(isLoading = false, data = result.data) }

                is {{Feature}}Result.Failure ->
                    _uiState.update { it.copy(isLoading = false, error = result.error) }
            }
        }
    }
}
