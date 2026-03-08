package com.ahmedsamir.pulse.feature.feed.domain.usecase

import androidx.paging.PagingData
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.feature.feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(): Flow<PagingData<Post>> = feedRepository.getFeed()
}