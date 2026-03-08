package com.ahmedsamir.pulse.core.database.mapper

import com.ahmedsamir.pulse.core.database.entity.PostEntity
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.User

fun PostEntity.toDomain(): Post = Post(
    id = id,
    authorId = authorId,
    author = User(
        id = authorId,
        username = authorUsername,
        displayName = authorDisplayName,
        profileImageUrl = authorProfileImageUrl,
        isVerified = authorIsVerified
    ),
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

fun Post.toEntity(): PostEntity = PostEntity(
    id = id,
    authorId = authorId,
    authorUsername = author.username,
    authorDisplayName = author.displayName,
    authorProfileImageUrl = author.profileImageUrl,
    authorIsVerified = author.isVerified,
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