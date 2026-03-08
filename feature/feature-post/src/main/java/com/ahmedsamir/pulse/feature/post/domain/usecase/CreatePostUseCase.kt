package com.ahmedsamir.pulse.feature.post.domain.usecase

import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.post.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(content: String, imageUri: String? = null): Resource<Post> {
        if (content.isBlank()) return Resource.Error("Post cannot be empty")
        if (content.length > 280) return Resource.Error("Post cannot exceed 280 characters")
        return postRepository.createPost(content.trim(), imageUri)
    }
}