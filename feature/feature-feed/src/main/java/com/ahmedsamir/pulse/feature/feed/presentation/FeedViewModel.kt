package com.ahmedsamir.pulse.feature.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.feature.feed.domain.usecase.GetFeedUseCase
import com.ahmedsamir.pulse.feature.feed.domain.usecase.LikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val likePostUseCase: LikePostUseCase
) : ViewModel() {

    private val _feedState = MutableStateFlow<PagingData<Post>>(PagingData.empty())
    val feedState: StateFlow<PagingData<Post>> = _feedState.asStateFlow()

    init {
        loadFeed()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            getFeedUseCase()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _feedState.value = pagingData
                }
        }
    }

    fun toggleLike(postId: String, isLiked: Boolean) {
        viewModelScope.launch {
            likePostUseCase(postId, isLiked)
        }
    }
}