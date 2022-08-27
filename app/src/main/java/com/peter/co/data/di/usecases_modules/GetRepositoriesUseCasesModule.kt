package com.peter.co.data.di.usecases_modules

import com.peter.co.data.usecases.GetRepositoriesUseCase
import com.peter.co.domain.repositories_abs.IGitHubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GetRepositoriesUseCasesModule {
    @Singleton
    @Provides
    fun provideGetRepositoryUseCase(repository: IGitHubRepository): GetRepositoriesUseCase =
        GetRepositoriesUseCase(repository)
}
