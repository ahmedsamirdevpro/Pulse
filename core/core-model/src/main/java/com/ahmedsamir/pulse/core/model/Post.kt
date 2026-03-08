package com.ahmedsamir.pulse.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Post(
    val id: String = "",
    val authorId: String = "",
    val author: User = User(),
    val content: String = "",
    val imageUrl: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val isLiked: Boolean = false,
    val isReposted: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)