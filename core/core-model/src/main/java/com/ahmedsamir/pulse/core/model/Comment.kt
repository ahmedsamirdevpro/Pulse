package com.ahmedsamir.pulse.core.model

data class Comment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val author: User = User(),
    val content: String = "",
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)