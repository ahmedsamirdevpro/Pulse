package com.ahmedsamir.pulse.feature.comments.data.repository

import com.ahmedsamir.pulse.core.model.Comment
import com.ahmedsamir.pulse.core.model.NotificationType
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.comments.domain.repository.CommentsRepository
import com.ahmedsamir.pulse.core.common.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class CommentsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val notificationHelper: NotificationHelper
) : CommentsRepository {

    override fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener: ListenerRegistration = firestore
            .collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val comments = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Comment::class.java)
                } ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addComment(postId: String, content: String): Resource<Comment> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            val userDoc = firestore.collection("users").document(userId).get().await()
            val author = userDoc.toObject(User::class.java) ?: User(id = userId)

            val commentId = UUID.randomUUID().toString()
            val comment = Comment(
                id = commentId,
                postId = postId,
                authorId = userId,
                author = author,
                content = content,
                createdAt = System.currentTimeMillis()
            )

            firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .document(commentId)
                .set(comment)
                .await()

            firestore.collection("posts").document(postId)
                .update(
                    "commentsCount",
                    com.google.firebase.firestore.FieldValue.increment(1)
                ).await()

            val postDoc = firestore.collection("posts").document(postId).get().await()
            val postAuthorId = postDoc.getString("authorId") ?: ""
            if (postAuthorId.isNotEmpty()) {
                notificationHelper.sendNotification(
                    recipientId = postAuthorId,
                    sender = author,
                    type = NotificationType.COMMENT,
                    postId = postId
                )
            }

            Resource.Success(comment)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add comment")
        }
    }

    override suspend fun deleteComment(postId: String, commentId: String): Resource<Unit> {
        return try {
            firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .document(commentId)
                .delete()
                .await()

            firestore.collection("posts").document(postId)
                .update(
                    "commentsCount",
                    com.google.firebase.firestore.FieldValue.increment(-1)
                ).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete comment")
        }
    }

    override suspend fun likeComment(commentId: String): Resource<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")
            firestore.collection("comment_likes")
                .document("${userId}_${commentId}")
                .set(mapOf("userId" to userId, "commentId" to commentId))
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to like comment")
        }
    }

    override suspend fun unlikeComment(commentId: String): Resource<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")
            firestore.collection("comment_likes")
                .document("${userId}_${commentId}")
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unlike comment")
        }
    }
}