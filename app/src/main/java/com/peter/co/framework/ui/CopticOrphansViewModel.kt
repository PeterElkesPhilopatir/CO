package com.peter.co.framework.ui

import android.content.pm.PackageManager
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.peter.co.data.network.PER_PAGE
import com.peter.co.domain.models.User
import com.peter.co.domain.repositories_abs.IGitHubRepository
import com.peter.co.framework.ui.home.PostDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class CopticOrphansViewModel @Inject constructor(private val repository: IGitHubRepository) :
    ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun setUser(user: User) {
        _user.value = user
    }

    val listData = Pager(PagingConfig(PER_PAGE)) {
        PostDataSource(repository)
    }.flow.cachedIn(CoroutineScope(Job() + Dispatchers.Main))

}