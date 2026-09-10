package {{package}}.feature.{{feature}}.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import {{package}}.core.designsystem.theme.AppTheme
import {{package}}.feature.{{feature}}.model.{{Feature}}Error

/**
 * 순수 UI. 상태와 콜백만 받는다 (R-17-01). Modifier는 첫 선택 파라미터.
 * `onBack`은 뼈대에서 아직 쓰이지 않는다. 실제 화면에서는 TopAppBar의 navigationIcon에 연결한다.
 */
@Suppress("UnusedParameter")
@Composable
fun {{Feature}}Screen(
    uiState: {{Feature}}UiState,
    onEvent: ({{Feature}}Event) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val error = uiState.error
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            error != null ->
                Button(onClick = { onEvent({{Feature}}Event.Retry) }) {
                    Text(error.toMessage())
                }
            else -> Text(uiState.data?.name.orEmpty())
        }
    }
}

/**
 * 에러 → 사용자 문구 매핑은 UI 계층 책임이다 (R-23). 도메인·데이터 계층은 문자열을 만들지 않는다.
 * 실제 프로젝트에서는 리터럴 대신 stringResource(R.string.…) 값을 반환하도록 바꾼다.
 */
private fun {{Feature}}Error.toMessage(): String = when (this) {
    {{Feature}}Error.Network -> "네트워크 연결을 확인해 주세요"
    {{Feature}}Error.NotFound -> "요청한 정보를 찾을 수 없습니다"
    is {{Feature}}Error.Unknown -> "잠시 후 다시 시도해 주세요"
}

@Preview(name = "Default")
@Composable
private fun {{Feature}}ScreenPreview() {
    AppTheme { {{Feature}}Screen(uiState = {{Feature}}UiState(), onEvent = {}, onBack = {}) }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun {{Feature}}ScreenDarkPreview() {
    AppTheme { {{Feature}}Screen(uiState = {{Feature}}UiState(), onEvent = {}, onBack = {}) }
}

@Preview(name = "LargeFont", fontScale = 1.5f)
@Composable
private fun {{Feature}}ScreenLargeFontPreview() {
    AppTheme { {{Feature}}Screen(uiState = {{Feature}}UiState(), onEvent = {}, onBack = {}) }
}
