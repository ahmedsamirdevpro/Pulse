package com.ahmedsamir.pulse.feature.comments.domain.usecase

import com.ahmedsamir.pulse.core.model.Comment
import com.ahmedsamir.pulse.feature.comments.domain.repository.CommentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentsRepository: CommentsRepository
) {
    operator fun invoke(postId: String): Flow<List<Comment>> =
        commentsRepository.getComments(postId)
}