package com.peter.co.domain.repositories_abs

import com.peter.co.domain.models.Repository
import com.peter.co.domain.models.User
import kotlinx.coroutines.flow.Flow

interface IGitHubRepository {
    fun getRepos(username: String, page: Int): Flow<List<Repository>>
}