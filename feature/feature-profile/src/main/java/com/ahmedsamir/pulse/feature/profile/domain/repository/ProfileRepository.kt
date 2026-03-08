package com.ahmedsamir.pulse.feature.profile.domain.repository

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(userId: String): Flow<User?>
    suspend fun getCurrentUser(): Resource<User>
    suspend fun updateProfile(displayName: String, bio: String, imageUri: String?): Resource<User>
    suspend fun followUser(userId: String): Resource<Unit>
    suspend fun unfollowUser(userId: String): Resource<Unit>
    suspend fun isFollowing(userId: String): Boolean
    fun getCurrentUserId(): String?
}