package {{package}}.feature.{{feature}}

import {{package}}.feature.{{feature}}.data.{{Feature}}Dto
import {{package}}.feature.{{feature}}.data.{{Feature}}RemoteDataSource
import java.io.IOException

/**
 * fake 우선, mock은 외부 경계만 (R-30-02). `dto = null` 이면 네트워크 실패를 흉내 낸다.
 * Repository 테스트는 이 fake를 주입해 조립한다 (R-30-10).
 */
class Fake{{Feature}}RemoteDataSource : {{Feature}}RemoteDataSource {
    var dto: {{Feature}}Dto? = {{Feature}}Dto(id = "1", name = "remote")

    override suspend fun fetch(): {{Feature}}Dto = dto ?: throw IOException("네트워크 실패")
}
