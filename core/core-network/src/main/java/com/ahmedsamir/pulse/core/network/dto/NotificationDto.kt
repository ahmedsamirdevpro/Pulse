package com.ahmedsamir.pulse.core.network.dto

import com.ahmedsamir.pulse.core.model.Notification
import com.ahmedsamir.pulse.core.model.NotificationType
import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("recipient_id") val recipientId: String = "",
    @SerializedName("sender_id") val senderId: String = "",
    @SerializedName("sender") val sender: UserDto = UserDto(),
    @SerializedName("type") val type: String = "",
    @SerializedName("post_id") val postId: String = "",
    @SerializedName("message") val message: String = "",
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("created_at") val createdAt: Long = 0L
) {
    fun toDomain(): Notification = Notification(
        id = id,
        recipientId = recipientId,
        senderId = senderId,
        sender = sender.toDomain(),
        type = when (type) {
            "like" -> NotificationType.LIKE
            "comment" -> NotificationType.COMMENT
            "follow" -> NotificationType.FOLLOW
            "repost" -> NotificationType.REPOST
            "mention" -> NotificationType.MENTION
            else -> NotificationType.LIKE
        },
        postId = postId,
        message = message,
        isRead = isRead,
        createdAt = createdAt
    )
}