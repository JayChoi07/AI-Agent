package {{package}}.feature.{{feature}}

import {{package}}.feature.{{feature}}.data.Default{{Feature}}Repository
import {{package}}.feature.{{feature}}.data.{{Feature}}Dto
import {{package}}.feature.{{feature}}.model.{{Feature}}
import {{package}}.feature.{{feature}}.model.{{Feature}}Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Repository는 fake DataSource 2종으로 조립한다 (R-30-10). 검증 대상은 캐시 우선순위와 에러 변환이다. */
@OptIn(ExperimentalCoroutinesApi::class)
class Default{{Feature}}RepositoryTest {
    private val remote = Fake{{Feature}}RemoteDataSource()
    private val local = Fake{{Feature}}LocalDataSource()
    private val repository = Default{{Feature}}Repository(remote, local, UnconfinedTestDispatcher())

    @Test
    fun `원격 성공이면 Success이고 캐시에 저장한다`() = runTest {
        val result = repository.get{{Feature}}()
        assertEquals({{Feature}}Result.Success({{Feature}}(id = "1", name = "remote")), result)
        assertEquals({{Feature}}Dto(id = "1", name = "remote"), local.cache)
    }

    @Test
    fun `원격 실패면 캐시로 폴백한다`() = runTest {
        remote.dto = null
        local.cache = {{Feature}}Dto(id = "1", name = "cached")
        val result = repository.get{{Feature}}()
        assertEquals({{Feature}}Result.Success({{Feature}}(id = "1", name = "cached")), result)
    }

    @Test
    fun `원격과 캐시가 연달아 실패해도 예외가 새지 않는다`() = runTest {
        remote.dto = null
        local.failOnLoad = true
        assertTrue(repository.get{{Feature}}() is {{Feature}}Result.Failure)
    }
}
