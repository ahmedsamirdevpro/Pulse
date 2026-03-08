package com.ahmedsamir.pulse.core.model

import androidx.compose.runtime.Immutable
import com.ahmedsamir.pulse.core.model.NotificationType
@Immutable
data class Notification(
    val id: String = "",
    val recipientId: String = "",
    val senderId: String = "",
    val sender: User = User(),
    val type: NotificationType = NotificationType.LIKE,
    val postId: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = 0L
) {
    fun getTypeFromString(typeStr: String): NotificationType {
        return try {
            NotificationType.valueOf(typeStr)
        } catch (e: Exception) {
            NotificationType.LIKE
        }
    }
}