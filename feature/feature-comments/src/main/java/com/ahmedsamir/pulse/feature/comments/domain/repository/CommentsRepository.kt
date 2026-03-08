package com.ahmedsamir.pulse.feature.comments.domain.repository

import com.ahmedsamir.pulse.core.model.Comment
import com.ahmedsamir.pulse.core.model.Resource
import kotlinx.coroutines.flow.Flow

interface CommentsRepository {
    fun getComments(postId: String): Flow<List<Comment>>
    suspend fun addComment(postId: String, content: String): Resource<Comment>
    suspend fun deleteComment(postId: String, commentId: String): Resource<Unit>
    suspend fun likeComment(commentId: String): Resource<Unit>
    suspend fun unlikeComment(commentId: String): Resource<Unit>
}