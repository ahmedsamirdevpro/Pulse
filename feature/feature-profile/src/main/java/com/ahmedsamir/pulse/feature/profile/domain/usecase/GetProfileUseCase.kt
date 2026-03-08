package com.ahmedsamir.pulse.feature.profile.domain.usecase

import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(userId: String): Flow<User?> =
        profileRepository.getProfile(userId)
}