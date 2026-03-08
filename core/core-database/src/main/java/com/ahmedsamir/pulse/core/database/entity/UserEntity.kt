package com.ahmedsamir.pulse.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val bio: String,
    val profileImageUrl: String,
    val coverImageUrl: String,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int,
    val isFollowing: Boolean,
    val isVerified: Boolean,
    val createdAt: Long,
    val cachedAt: Long = System.currentTimeMillis()
)