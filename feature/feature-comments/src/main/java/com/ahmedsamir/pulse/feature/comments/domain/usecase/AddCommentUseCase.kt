package com.ahmedsamir.pulse.feature.comments.domain.usecase

import com.ahmedsamir.pulse.core.model.Comment
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.comments.domain.repository.CommentsRepository
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    suspend operator fun invoke(postId: String, content: String): Resource<Comment> {
        if (content.isBlank()) return Resource.Error("Comment cannot be empty")
        if (content.length > 280) return Resource.Error("Comment too long")
        return commentsRepository.addComment(postId, content.trim())
    }
}