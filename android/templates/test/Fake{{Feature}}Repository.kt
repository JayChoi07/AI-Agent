package {{package}}.feature.{{feature}}

import {{package}}.feature.{{feature}}.data.{{Feature}}Repository
import {{package}}.feature.{{feature}}.model.{{Feature}}
import {{package}}.feature.{{feature}}.model.{{Feature}}Result

/** fake 우선, mock은 외부 경계만 (R-30-02). */
class Fake{{Feature}}Repository : {{Feature}}Repository {
    var result: {{Feature}}Result = {{Feature}}Result.Success({{Feature}}(id = "1", name = "fake"))

    var callCount: Int = 0
        private set

    override suspend fun get{{Feature}}(): {{Feature}}Result {
        callCount++
        return result
    }
}
