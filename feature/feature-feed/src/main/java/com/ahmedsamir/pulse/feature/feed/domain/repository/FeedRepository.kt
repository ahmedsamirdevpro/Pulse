package com.ahmedsamir.pulse.feature.feed.domain.repository

import androidx.paging.PagingData
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.Resource
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(): Flow<PagingData<Post>>
    fun getUserPosts(userId: String): Flow<PagingData<Post>>
    suspend fun likePost(postId: String, isLiked: Boolean): Resource<Unit>
    suspend fun unlikePost(postId: String): Resource<Unit>
    suspend fun repostPost(postId: String): Resource<Unit>
    suspend fun deletePost(postId: String): Resource<Unit>
    suspend fun getPostById(postId: String): Resource<Post>
    fun observePost(postId: String): Flow<Post?>
}