package com.ahmedsamir.pulse.core.model

data class Notification(
    val id: String = "",
    val recipientId: String = "",
    val senderId: String = "",
    val sender: User = User(),
    val type: NotificationType = NotificationType.LIKE,
    val postId: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    REPOST,
    MENTION
}