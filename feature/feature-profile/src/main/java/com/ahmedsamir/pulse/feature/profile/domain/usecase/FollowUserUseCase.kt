package com.ahmedsamir.pulse.feature.profile.domain.usecase

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class FollowUserUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: String, isFollowing: Boolean): Resource<Unit> {
        return if (isFollowing) profileRepository.unfollowUser(userId)
        else profileRepository.followUser(userId)
    }
}