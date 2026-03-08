package com.ahmedsamir.pulse.feature.auth.domain.repository

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    fun isLoggedIn(): Boolean
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(email: String, password: String, username: String, displayName: String): Resource<User>
    suspend fun logout(): Resource<Unit>
    suspend fun getCurrentUser(): Resource<User>
}