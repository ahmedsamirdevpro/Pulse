package com.ahmedsamir.pulse.core.network.dto

import com.ahmedsamir.pulse.core.model.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("username") val username: String = "",
    @SerializedName("display_name") val displayName: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("bio") val bio: String = "",
    @SerializedName("profile_image_url") val profileImageUrl: String = "",
    @SerializedName("cover_image_url") val coverImageUrl: String = "",
    @SerializedName("followers_count") val followersCount: Int = 0,
    @SerializedName("following_count") val followingCount: Int = 0,
    @SerializedName("posts_count") val postsCount: Int = 0,
    @SerializedName("is_following") val isFollowing: Boolean = false,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("created_at") val createdAt: Long = 0L
) {
    fun toDomain(): User = User(
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
}