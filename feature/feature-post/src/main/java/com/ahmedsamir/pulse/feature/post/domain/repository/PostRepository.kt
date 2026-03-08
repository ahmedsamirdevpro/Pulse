package com.ahmedsamir.pulse.feature.post.domain.repository

import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.Resource

interface PostRepository {
    suspend fun createPost(content: String, imageUri: String?): Resource<Post>
    suspend fun deletePost(postId: String): Resource<Unit>
    suspend fun getPostById(postId: String): Resource<Post>
}