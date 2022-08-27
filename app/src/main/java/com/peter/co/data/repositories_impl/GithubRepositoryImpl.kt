package com.peter.co.data.repositories_impl

import android.util.Log
import com.peter.co.data.network.GithubAPI
import com.peter.co.data.network.PER_PAGE
import com.peter.co.domain.models.Repository
import com.peter.co.domain.repositories_abs.IGitHubRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.reflect.Array
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(private val api: GithubAPI) : IGitHubRepository {
    override fun getRepos(username: String, page: Int): Flow<List<Repository>> =
        flow {
            val list = ArrayList<Repository>()
            try {
                val response =
                    api.searchByQueryAsync(username = username, per_page = PER_PAGE, page = page)
                        .await()
                Log.i("github", "repo" + response.size.toString())
                response.forEach { list.add(Repository(id = it.id, name = it.name)) }
                emit(list)
            } catch (e: Exception) {
                Log.e("ErrorGetRepos", e.message.toString())
            }
            emit(list)
        }.flowOn(IO)


}