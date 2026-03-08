package com.ahmedsamir.pulse.feature.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.feature.feed.domain.usecase.GetFeedUseCase
import com.ahmedsamir.pulse.feature.feed.domain.usecase.LikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val likePostUseCase: LikePostUseCase
) : ViewModel() {

    private val _likedPostIds = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val _likeCounts = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val _rawFeed = getFeedUseCase()
        .cachedIn(viewModelScope)

    val feedState: kotlinx.coroutines.flow.Flow<PagingData<Post>> =
        _rawFeed.combine(_likedPostIds) { pagingData, likedMap ->
            pagingData.map { post ->
                val isLiked = likedMap[post.id] ?: post.isLiked
                val likesCount = _likeCounts.value[post.id] ?: post.likesCount
                post.copy(isLiked = isLiked, likesCount = likesCount)
            }
        }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun toggleLike(postId: String, currentIsLiked: Boolean, currentLikesCount: Int) {
        val newIsLiked = !currentIsLiked
        val newCount = if (newIsLiked) currentLikesCount + 1 else currentLikesCount - 1

        _likedPostIds.update { it + (postId to newIsLiked) }
        _likeCounts.update { it + (postId to newCount) }

        viewModelScope.launch {
            val result = likePostUseCase(postId, currentIsLiked)
            if (result is com.ahmedsamir.pulse.core.model.Resource.Error) {
                _likedPostIds.update { it + (postId to currentIsLiked) }
                _likeCounts.update { it + (postId to currentLikesCount) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _likedPostIds.value = emptyMap()
            _likeCounts.value = emptyMap()
            _isRefreshing.value = false
        }
    }
}