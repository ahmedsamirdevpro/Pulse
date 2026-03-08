package com.ahmedsamir.pulse.core.common

import com.ahmedsamir.pulse.core.model.NotificationType
import com.ahmedsamir.pulse.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun sendNotification(
        recipientId: String,
        sender: User,
        type: NotificationType,
        postId: String = "",
        message: String = ""
    ) {
        if (recipientId == sender.id) {
            android.util.Log.d("NotificationHelper", "SKIPPED: same user")
            return
        }

        android.util.Log.d("NotificationHelper", "Sending notification: type=$type, to=$recipientId, from=${sender.id}")

        try {
            val notificationId = UUID.randomUUID().toString()
            val notification = mapOf(
                "id" to notificationId,
                "recipientId" to recipientId,
                "senderId" to sender.id,
                "sender" to mapOf(
                    "id" to sender.id,
                    "displayName" to sender.displayName,
                    "username" to sender.username,
                    "profileImageUrl" to sender.profileImageUrl
                ),
                "type" to type.name,
                "postId" to postId,
                "message" to message.ifEmpty {
                    when (type) {
                        NotificationType.LIKE -> "${sender.displayName} liked your post"
                        NotificationType.FOLLOW -> "${sender.displayName} followed you"
                        NotificationType.COMMENT -> "${sender.displayName} commented on your post"
                        NotificationType.REPOST -> "${sender.displayName} reposted your post"
                        NotificationType.MENTION -> "${sender.displayName} mentioned you"
                    }
                },
                "isRead" to false,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("notifications")
                .document(notificationId)
                .set(notification)
                .await()

            android.util.Log.d("NotificationHelper", "✅ Notification sent successfully: $notificationId")

        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "❌ Failed to send notification: ${e.message}")
        }
    }
}