package com.ahmedsamir.pulse.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.search.domain.usecase.GetRecommendedUsersUseCase
import com.ahmedsamir.pulse.feature.search.domain.usecase.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getRecommendedUsersUseCase: GetRecommendedUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadRecommended()
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                    } else {
                        search(query)
                    }
                }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(query = query) }
    }

    private fun loadRecommended() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRecommended = true) }
            when (val result = getRecommendedUsersUseCase()) {
                is Resource.Success -> _uiState.update {
                    it.copy(isLoadingRecommended = false, recommendedUsers = result.data)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoadingRecommended = false)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            when (val result = searchUsersUseCase(query)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isSearching = false, searchResults = result.data)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isSearching = false, searchResults = emptyList())
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearQuery() {
        _searchQuery.value = ""
        _uiState.update { it.copy(query = "", searchResults = emptyList(), isSearching = false) }
    }
}

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val isLoadingRecommended: Boolean = false,
    val searchResults: List<User> = emptyList(),
    val recommendedUsers: List<User> = emptyList()
)