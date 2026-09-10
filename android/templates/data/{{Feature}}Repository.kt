package {{package}}.feature.{{feature}}.data

import {{package}}.feature.{{feature}}.model.{{Feature}}Result

/** Repository는 인터페이스와 구현 모두 data 계층에 둔다. 다른 계층은 이 인터페이스에만 의존한다 (R-11-02). */
interface {{Feature}}Repository {
    suspend fun get{{Feature}}(): {{Feature}}Result
}
