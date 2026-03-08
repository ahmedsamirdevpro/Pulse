package com.ahmedsamir.pulse.core.network.dto

import com.ahmedsamir.pulse.core.model.Comment
import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("post_id") val postId: String = "",
    @SerializedName("author_id") val authorId: String = "",
    @SerializedName("author") val author: UserDto = UserDto(),
    @SerializedName("content") val content: String = "",
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("created_at") val createdAt: Long = 0L
) {
    fun toDomain(): Comment = Comment(
        id = id,
        postId = postId,
        authorId = authorId,
        author = author.toDomain(),
        content = content,
        likesCount = likesCount,
        isLiked = isLiked,
        createdAt = createdAt
    )
}