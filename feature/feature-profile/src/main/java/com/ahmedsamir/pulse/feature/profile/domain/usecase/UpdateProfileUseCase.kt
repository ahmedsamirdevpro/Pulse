package com.ahmedsamir.pulse.feature.profile.domain.usecase

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        displayName: String,
        bio: String,
        imageUri: String? = null
    ): Resource<User> {
        if (displayName.isBlank()) return Resource.Error("Display name cannot be empty")
        if (displayName.length > 50) return Resource.Error("Display name too long")
        if (bio.length > 160) return Resource.Error("Bio cannot exceed 160 characters")
        return profileRepository.updateProfile(displayName.trim(), bio.trim(), imageUri)
    }
}