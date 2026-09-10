package {{package}}.feature.{{feature}}.domain

import {{package}}.feature.{{feature}}.data.{{Feature}}Repository
import {{package}}.feature.{{feature}}.model.{{Feature}}Result
import javax.inject.Inject

/**
 * 뼈대 템플릿이다. 지금처럼 Repository로 단순 위임만 한다면 UseCase를 만들지 말고
 * ViewModel이 Repository를 직접 호출한다 (R-16-02).
 * 승격 조건은 ViewModel 2개 이상이 공유하거나 Repository 2개 이상을 조합할 때다 (R-16-07).
 */
class Get{{Feature}}UseCase @Inject constructor(
    private val repository: {{Feature}}Repository,
) {
    /** public 함수는 invoke 하나 (R-16-01). */
    suspend operator fun invoke(): {{Feature}}Result = repository.get{{Feature}}()
}
