package com.ahmedsamir.pulse.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pending_posts")
data class PendingPostEntity(
    @PrimaryKey val localId: String = UUID.randomUUID().toString(),
    val authorId: String,
    val content: String,
    val imageUri: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val isSyncing: Boolean = false
)