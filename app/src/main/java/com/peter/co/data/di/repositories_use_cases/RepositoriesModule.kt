package com.peter.co.data.di.repositories_use_cases


import com.peter.co.data.network.GithubAPI
import com.peter.co.data.repositories_impl.GithubRepositoryImpl
import com.peter.co.domain.repositories_abs.IGitHubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {
    @Singleton
    @Provides
    fun provideSearchRepository(api: GithubAPI): IGitHubRepository =
        GithubRepositoryImpl(api)
}