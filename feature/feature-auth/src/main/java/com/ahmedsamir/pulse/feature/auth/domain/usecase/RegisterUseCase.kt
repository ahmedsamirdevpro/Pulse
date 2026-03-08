package com.ahmedsamir.pulse.feature.auth.domain.usecase

import com.ahmedsamir.pulse.core.common.isValidEmail
import com.ahmedsamir.pulse.core.common.isValidPassword
import com.ahmedsamir.pulse.core.common.isValidUsername
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): Resource<User> {
        if (!email.isValidEmail()) return Resource.Error("Invalid email address")
        if (!password.isValidPassword()) return Resource.Error("Password must be at least 8 characters")
        if (!username.isValidUsername()) return Resource.Error("Username must be at least 3 characters (letters, numbers, _ only)")
        if (displayName.isBlank()) return Resource.Error("Display name cannot be empty")
        return authRepository.register(email.trim(), password, username.trim(), displayName.trim())
    }
}