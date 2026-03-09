package com.ahmedsamir.pulse.core.common

import com.ahmedsamir.pulse.core.model.NotificationType
import com.ahmedsamir.pulse.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val firestore: FirebaseFirestore,
    @OneSignalAppId private val oneSignalAppId: String,
    @OneSignalApiKey private val oneSignalApiKey: String
) {
    companion object {
        private const val ONESIGNAL_APP_ID = "e74f5187-f586-4f7b-9a67-f51626066a90"
        private const val ONESIGNAL_REST_API_KEY = "os_v2_app_45hvdb7vqzhxxgth6ulcmbtksdqhymkejchemneayvegikrrrjhg7rz2uiwleqb3ac5axwkckjpmypseulqmidldaozs74jkeul46fi"
    }

    suspend fun sendNotification(
        recipientId: String,
        sender: User,
        type: NotificationType,
        postId: String = "",
        message: String = ""
    ) {
        if (recipientId == sender.id) return

        val notificationMessage = message.ifEmpty {
            when (type) {
                NotificationType.LIKE -> "${sender.displayName} liked your post"
                NotificationType.FOLLOW -> "${sender.displayName} followed you"
                NotificationType.COMMENT -> "${sender.displayName} commented on your post"
                NotificationType.REPOST -> "${sender.displayName} reposted your post"
                NotificationType.MENTION -> "${sender.displayName} mentioned you"
            }
        }

        val notificationTitle = when (type) {
            NotificationType.LIKE -> "New Like ❤️"
            NotificationType.FOLLOW -> "New Follower 👤"
            NotificationType.COMMENT -> "New Comment 💬"
            NotificationType.REPOST -> "New Repost 🔁"
            NotificationType.MENTION -> "New Mention 📢"
        }

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
            "message" to notificationMessage,
            "isRead" to false,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("notifications")
            .document(notificationId)
            .set(notification)
            .await()

        sendOneSignalPush(
            recipientId = recipientId,
            title = notificationTitle,
            body = notificationMessage,
            data = mapOf("type" to type.name, "postId" to postId)
        )
    }

    private suspend fun sendOneSignalPush(
        recipientId: String,
        title: String,
        body: String,
        data: Map<String, String>
    ) = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://onesignal.com/api/v1/notifications")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Basic $ONESIGNAL_REST_API_KEY")
            connection.doOutput = true

            val dataJson = JSONObject()
            data.forEach { (k, v) -> dataJson.put(k, v) }

            val payload = JSONObject()
            payload.put("app_id", ONESIGNAL_APP_ID)
            payload.put("include_aliases", JSONObject().apply {
                put("external_id", JSONArray().apply { put(recipientId) })
            })
            payload.put("target_channel", "push")
            payload.put("headings", JSONObject().apply { put("en", title) })
            payload.put("contents", JSONObject().apply { put("en", body) })
            payload.put("data", dataJson)

            OutputStreamWriter(connection.outputStream).use {
                it.write(payload.toString())
            }

            val responseCode = connection.responseCode
            android.util.Log.d("OneSignal", "Push response: $responseCode")

        } catch (e: Exception) {
            android.util.Log.e("OneSignal", "Failed: ${e.message}")
        }
    }
}