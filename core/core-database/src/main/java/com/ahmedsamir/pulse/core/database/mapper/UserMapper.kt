package com.ahmedsamir.pulse.core.database.mapper

import com.ahmedsamir.pulse.core.database.entity.UserEntity
import com.ahmedsamir.pulse.core.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    username = username,
    displayName = displayName,
    email = email,
    bio = bio,
    profileImageUrl = profileImageUrl,
    coverImageUrl = coverImageUrl,
    followersCount = followersCount,
    followingCount = followingCount,
    postsCount = postsCount,
    isFollowing = isFollowing,
    isVerified = isVerified,
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    displayName = displayName,
    email = email,
    bio = bio,
    profileImageUrl = profileImageUrl,
    coverImageUrl = coverImageUrl,
    followersCount = followersCount,
    followingCount = followingCount,
    postsCount = postsCount,
    isFollowing = isFollowing,
    isVerified = isVerified,
    createdAt = createdAt
)