package com.ahmedsamir.pulse.feature.feed.domain.usecase

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.feed.domain.repository.FeedRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(postId: String, isLiked: Boolean): Resource<Unit> {
        return if (isLiked) feedRepository.unlikePost(postId)
        else feedRepository.likePost(postId)
    }
}