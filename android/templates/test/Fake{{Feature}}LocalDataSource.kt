package {{package}}.feature.{{feature}}

import {{package}}.feature.{{feature}}.data.{{Feature}}Dto
import {{package}}.feature.{{feature}}.data.{{Feature}}LocalDataSource

/**
 * 캐시 fake (R-30-02, R-30-10). `cache = null` 이면 캐시 없음,
 * `failOnLoad = true` 면 캐시 조회 자체가 실패한다(네트워크·캐시 연속 실패 경로 검증용).
 */
class Fake{{Feature}}LocalDataSource : {{Feature}}LocalDataSource {
    var cache: {{Feature}}Dto? = null
    var failOnLoad: Boolean = false

    override suspend fun save(dto: {{Feature}}Dto) {
        cache = dto
    }

    override suspend fun load(): {{Feature}}Dto? {
        if (failOnLoad) error("캐시 조회 실패")
        return cache
    }
}
