package {{package}}.feature.{{feature}}.model

/** 도메인 모델. Android 의존 없음. model은 다른 계층을 아무것도 참조하지 않는다 (R-11-01). */
data class {{Feature}}(val id: String = "", val name: String = "")

/**
 * 도메인별 에러 계층. 표준 라이브러리 Result나 Arrow Either 대신 이 sealed 계층을 쓴다(ERROR_TYPE 결정).
 * 구분이 무의미한 "없음"은 nullable로 표현하고, 여기에 케이스를 늘리지 않는다.
 */
sealed interface {{Feature}}Error {
    data object Network : {{Feature}}Error
    data object NotFound : {{Feature}}Error
    data class Unknown(val cause: Throwable) : {{Feature}}Error
}

/** Repository·UseCase의 반환 타입. 실패는 예외가 아니라 값으로 전달한다. */
sealed interface {{Feature}}Result {
    data class Success(val data: {{Feature}}) : {{Feature}}Result
    data class Failure(val error: {{Feature}}Error) : {{Feature}}Result
}
