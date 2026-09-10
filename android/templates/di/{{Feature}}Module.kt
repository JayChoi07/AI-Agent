package {{package}}.feature.{{feature}}.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import {{package}}.feature.{{feature}}.data.Default{{Feature}}LocalDataSource
import {{package}}.feature.{{feature}}.data.Default{{Feature}}RemoteDataSource
import {{package}}.feature.{{feature}}.data.Default{{Feature}}Repository
import {{package}}.feature.{{feature}}.data.{{Feature}}LocalDataSource
import {{package}}.feature.{{feature}}.data.{{Feature}}RemoteDataSource
import {{package}}.feature.{{feature}}.data.{{Feature}}Repository

/** @Binds 만 있는 모듈은 abstract class 가 아니라 interface 로 둔다(구체 멤버가 없다). */
@Module
@InstallIn(SingletonComponent::class)
interface {{Feature}}Module {
    @Binds
    fun bind{{Feature}}Repository(impl: Default{{Feature}}Repository): {{Feature}}Repository

    @Binds
    fun bind{{Feature}}RemoteDataSource(
        impl: Default{{Feature}}RemoteDataSource,
    ): {{Feature}}RemoteDataSource

    @Binds
    fun bind{{Feature}}LocalDataSource(
        impl: Default{{Feature}}LocalDataSource,
    ): {{Feature}}LocalDataSource
}
