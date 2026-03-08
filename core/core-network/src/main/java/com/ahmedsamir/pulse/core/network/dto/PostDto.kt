package com.ahmedsamir.pulse.core.network.dto

import com.ahmedsamir.pulse.core.model.Post
import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("author_id") val authorId: String = "",
    @SerializedName("author") val author: UserDto = UserDto(),
    @SerializedName("content") val content: String = "",
    @SerializedName("image_url") val imageUrl: String = "",
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("comments_count") val commentsCount: Int = 0,
    @SerializedName("reposts_count") val repostsCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("is_reposted") val isReposted: Boolean = false,
    @SerializedName("is_bookmarked") val isBookmarked: Boolean = false,
    @SerializedName("created_at") val createdAt: Long = 0L,
    @SerializedName("updated_at") val updatedAt: Long = 0L
) {
    fun toDomain(): Post = Post(
        id = id,
        authorId = authorId,
        author = author.toDomain(),
        content = content,
        imageUrl = imageUrl,
        likesCount = likesCount,
        commentsCount = commentsCount,
        repostsCount = repostsCount,
        isLiked = isLiked,
        isReposted = isReposted,
        isBookmarked = isBookmarked,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}