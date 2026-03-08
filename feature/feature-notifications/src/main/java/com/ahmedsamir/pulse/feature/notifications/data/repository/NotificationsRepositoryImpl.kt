package com.ahmedsamir.pulse.feature.notifications.data.repository

import com.ahmedsamir.pulse.core.model.Notification
import com.ahmedsamir.pulse.core.model.NotificationType
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.notifications.domain.repository.NotificationsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : NotificationsRepository {

    override fun getNotifications(): Flow<List<Notification>> = callbackFlow {
        val userId = firebaseAuth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore
            .collection("notifications")
            .whereEqualTo("recipientId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("NotificationsRepo", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null

                        val senderMap = data["sender"] as? Map<*, *>
                        val sender = User(
                            id = senderMap?.get("id") as? String ?: "",
                            displayName = senderMap?.get("displayName") as? String ?: "",
                            username = senderMap?.get("username") as? String ?: "",
                            profileImageUrl = senderMap?.get("profileImageUrl") as? String ?: ""
                        )

                        val typeStr = data["type"] as? String ?: "LIKE"
                        val type = try {
                            NotificationType.valueOf(typeStr)
                        } catch (e: Exception) {
                            NotificationType.LIKE
                        }

                        Notification(
                            id = data["id"] as? String ?: doc.id,
                            recipientId = data["recipientId"] as? String ?: "",
                            senderId = data["senderId"] as? String ?: "",
                            sender = sender,
                            type = type,
                            postId = data["postId"] as? String ?: "",
                            message = data["message"] as? String ?: "",
                            isRead = data["isRead"] as? Boolean ?: false,
                            createdAt = data["createdAt"] as? Long ?: 0L
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("NotificationsRepo", "Parse error: ${e.message}")
                        null
                    }
                } ?: emptyList()

                android.util.Log.d("NotificationsRepo", "Loaded ${notifications.size} notifications")
                trySend(notifications)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun markAsRead(notificationId: String): Resource<Unit> {
        return try {
            firestore.collection("notifications")
                .document(notificationId)
                .update("isRead", true)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark as read")
        }
    }

    override suspend fun markAllAsRead(): Resource<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            val unread = firestore.collection("notifications")
                .whereEqualTo("recipientId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()
            unread.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark all as read")
        }
    }

    override fun getUnreadCount(): Flow<Int> = getNotifications().map { notifications ->
        notifications.count { !it.isRead }
    }
}