package com.ahmedsamir.pulse.feature.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.feed.domain.repository.FeedRepository
import com.ahmedsamir.pulse.feature.feed.domain.usecase.LikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val likePostUseCase: LikePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            feedRepository.observePost(postId).collect { post ->
                if (post != null) {
                    _uiState.update { it.copy(isLoading = false, post = post) }
                } else {
                    when (val result = feedRepository.getPostById(postId)) {
                        is Resource.Success -> _uiState.update {
                            it.copy(isLoading = false, post = result.data)
                        }
                        is Resource.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                        is Resource.Loading -> Unit
                    }
                }
            }
        }
    }

    fun toggleLike(postId: String, isLiked: Boolean) {
        viewModelScope.launch {
            likePostUseCase(postId, isLiked)
        }
    }
}

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String? = null
)