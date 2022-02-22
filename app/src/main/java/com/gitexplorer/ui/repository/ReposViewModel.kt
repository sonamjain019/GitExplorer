package com.gitexplorer.ui.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gitexplorer.data.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject internal constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)
    var previousQueryString: String = ""

    val repos = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchRepos(query: String) {
        currentQuery.value = query
    }

    companion object {
        const val DEFAULT_QUERY = "kotlin in:name"
    }
}