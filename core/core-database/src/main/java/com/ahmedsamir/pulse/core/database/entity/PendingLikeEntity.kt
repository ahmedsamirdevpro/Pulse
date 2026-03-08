package com.ahmedsamir.pulse.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_likes")
data class PendingLikeEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val isLike: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)