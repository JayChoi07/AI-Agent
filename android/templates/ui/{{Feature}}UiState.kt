package {{package}}.feature.{{feature}}.ui

import {{package}}.feature.{{feature}}.model.{{Feature}}
import {{package}}.feature.{{feature}}.model.{{Feature}}Error

/** 화면 상태. 모든 필드에 기본값. 불변 (R-12-01). 에러는 도메인 타입 그대로 담는다. */
data class {{Feature}}UiState(
    val isLoading: Boolean = false,
    val data: {{Feature}}? = null,
    val error: {{Feature}}Error? = null,
)

/** 사용자 이벤트. UI → ViewModel 단방향 (R-00-01). */
sealed interface {{Feature}}Event {
    data object Retry : {{Feature}}Event
}
