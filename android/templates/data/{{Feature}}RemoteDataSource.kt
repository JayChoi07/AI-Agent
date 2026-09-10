package {{package}}.feature.{{feature}}.data

import {{package}}.feature.{{feature}}.model.{{Feature}}
import javax.inject.Inject

/** 네트워크 DTO. 도메인 모델과 분리하고, 매핑은 이 파일에서 한다. */
data class {{Feature}}Dto(val id: String, val name: String)

fun {{Feature}}Dto.toDomain() = {{Feature}}(id = id, name = name)

/**
 * Repository 테스트가 fake로 갈아끼울 수 있도록 인터페이스로 둔다 (R-30-10).
 * 구현이 하나뿐이어도 이 경계는 테스트 대역을 위해 필요하다 (R-00-05 예외).
 */
interface {{Feature}}RemoteDataSource {
    /** Retrofit 서비스 호출로 교체한다. 예외는 그대로 던지고 Repository가 잡는다. */
    suspend fun fetch(): {{Feature}}Dto
}

/** 스텁 본문에는 정지 지점이 없다. Retrofit으로 교체하면 실제 suspend가 되므로 시그니처를 유지한다. */
@Suppress("RedundantSuspendModifier")
class Default{{Feature}}RemoteDataSource @Inject constructor() : {{Feature}}RemoteDataSource {
    override suspend fun fetch(): {{Feature}}Dto = {{Feature}}Dto(id = "1", name = "remote")
}
