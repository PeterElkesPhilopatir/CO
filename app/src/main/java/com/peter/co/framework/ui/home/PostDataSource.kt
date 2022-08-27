package com.peter.co.framework.ui.home

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.peter.co.data.usecases.GetRepositoriesUseCase
import com.peter.co.domain.models.Repository
import com.peter.co.domain.repositories_abs.IGitHubRepository
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class PostDataSource (private val repository: IGitHubRepository) :
    PagingSource<Int, Repository>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val responseData = mutableListOf<Repository>()

           repository.getRepos(username = "peterelkesphilopatir", page =  currentLoadingPageKey).collectLatest {
               Log.i("github", "psd" + it.size.toString())
               responseData.addAll(it)
            }

            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            return LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = currentLoadingPageKey.plus(1)
            )
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
            return LoadResult.Error(e)
        }
    }

    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state?.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}