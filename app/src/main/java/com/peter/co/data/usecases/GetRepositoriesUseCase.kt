package com.peter.co.data.usecases

import com.peter.co.domain.repositories_abs.IGitHubRepository
import javax.inject.Inject

class GetRepositoriesUseCase @Inject constructor(private val repository: IGitHubRepository) {
    operator fun invoke(username : String,page : Int) = repository.getRepos(username,page)
}