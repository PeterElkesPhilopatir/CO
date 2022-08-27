package com.peter.co.data.network

import com.peter.co.data.network.response.RepositoryResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubAPI {
    @GET("users/{user}/repos")
    fun searchByQueryAsync(@Path("user") username: String,@Query("per_page") per_page : Int,@Query("page") page : Int): Deferred<RepositoryResponse>
}