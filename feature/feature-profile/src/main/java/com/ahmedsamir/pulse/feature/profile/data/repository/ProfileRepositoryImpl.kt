package com.ahmedsamir.pulse.feature.profile.data.repository

import com.ahmedsamir.pulse.core.common.NotificationHelper
import com.ahmedsamir.pulse.core.model.NotificationType
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage ,
    private val notificationHelper: NotificationHelper
) : ProfileRepository {

    override fun getProfile(userId: String): Flow<User?> = callbackFlow {
        val listener: ListenerRegistration = firestore
            .collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getCurrentUser(): Resource<User> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
                ?: return Resource.Error("User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user")
        }
    }

    override suspend fun updateProfile(
        displayName: String,
        bio: String,
        imageUri: String?
    ): Resource<User> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            val updates = mutableMapOf<String, Any>(
                "displayName" to displayName,
                "bio" to bio
            )

            if (!imageUri.isNullOrEmpty()) {
                val imageRef = storage.reference
                    .child("profiles/${UUID.randomUUID()}.jpg")
                val uploadTask = imageRef
                    .putFile(android.net.Uri.parse(imageUri))
                    .await()
                val imageUrl = uploadTask.storage.downloadUrl.await().toString()
                updates["profileImageUrl"] = imageUrl
            }

            firestore.collection("users").document(uid).update(updates).await()

            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
                ?: return Resource.Error("User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    override suspend fun followUser(userId: String): Resource<Unit> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            firestore.collection("follows")
                .document("${currentUserId}_${userId}")
                .set(
                    mapOf(
                        "followerId" to currentUserId,
                        "followingId" to userId,
                        "createdAt" to System.currentTimeMillis()
                    )
                ).await()

            firestore.collection("users").document(userId)
                .update("followersCount",
                    com.google.firebase.firestore.FieldValue.increment(1))
                .await()

            firestore.collection("users").document(currentUserId)
                .update("followingCount",
                    com.google.firebase.firestore.FieldValue.increment(1))
                .await()

            // ← Send notification
            val senderDoc = firestore.collection("users")
                .document(currentUserId).get().await()
            val sender = senderDoc.toObject(com.ahmedsamir.pulse.core.model.User::class.java)

            if (sender != null) {
                notificationHelper.sendNotification(
                    recipientId = userId,
                    sender = sender,
                    type = NotificationType.FOLLOW
                )
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to follow user")
        }
    }

    override suspend fun unfollowUser(userId: String): Resource<Unit> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            firestore.collection("follows")
                .document("${currentUserId}_${userId}")
                .delete()
                .await()

            firestore.collection("users").document(userId)
                .update("followersCount",
                    com.google.firebase.firestore.FieldValue.increment(-1))
                .await()

            firestore.collection("users").document(currentUserId)
                .update("followingCount",
                    com.google.firebase.firestore.FieldValue.increment(-1))
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unfollow user")
        }
    }

    override suspend fun isFollowing(userId: String): Boolean {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: return false
            firestore.collection("follows")
                .document("${currentUserId}_${userId}")
                .get()
                .await()
                .exists()
        } catch (e: Exception) {
            false
        }
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
}