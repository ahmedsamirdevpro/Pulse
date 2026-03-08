package com.ahmedsamir.pulse.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ahmedsamir.pulse.core.database.PulseDatabase
import com.ahmedsamir.pulse.core.database.mapper.toEntity
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.util.UUID

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: PulseDatabase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            coroutineScope {
                val syncPendingPosts = async { syncPendingPosts() }
                val syncPendingLikes = async { syncPendingLikes() }
                val cacheFeed = async { cacheFeed() }
                awaitAll(syncPendingPosts, syncPendingLikes, cacheFeed)
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }

    private suspend fun syncPendingPosts() {
        val pendingPosts = database.pendingPostDao().getPendingPostsOnce()
        if (pendingPosts.isEmpty()) return

        val userId = firebaseAuth.currentUser?.uid ?: return
        val userDoc = firestore.collection("users").document(userId).get().await()
        val author = userDoc.toObject(User::class.java) ?: User(id = userId)

        pendingPosts.forEach { pendingPost ->
            try {
                database.pendingPostDao().updatePendingPost(
                    pendingPost.copy(isSyncing = true)
                )

                var imageUrl = ""
                if (pendingPost.imageUri.isNotEmpty()) {
                    val imageRef = storage.reference
                        .child("posts/${UUID.randomUUID()}.jpg")
                    val uploadTask = imageRef
                        .putFile(android.net.Uri.parse(pendingPost.imageUri))
                        .await()
                    imageUrl = uploadTask.storage.downloadUrl.await().toString()
                }

                val postId = UUID.randomUUID().toString()
                val post = Post(
                    id = postId,
                    authorId = userId,
                    author = author,
                    content = pendingPost.content,
                    imageUrl = imageUrl,
                    createdAt = pendingPost.createdAt,
                    updatedAt = System.currentTimeMillis()
                )

                firestore.collection("posts").document(postId).set(post).await()

                firestore.collection("users").document(userId)
                    .update(
                        "postsCount",
                        com.google.firebase.firestore.FieldValue.increment(1)
                    ).await()

                database.pendingPostDao().deletePendingPost(pendingPost.localId)

            } catch (e: Exception) {
                database.pendingPostDao().updatePendingPost(
                    pendingPost.copy(
                        isSyncing = false,
                        retryCount = pendingPost.retryCount + 1
                    )
                )
            }
        }
    }

    private suspend fun syncPendingLikes() {
        val pendingLikes = database.pendingLikeDao().getPendingLikes()
        if (pendingLikes.isEmpty()) return

        pendingLikes.forEach { pendingLike ->
            try {
                val likeId = "${pendingLike.userId}_${pendingLike.postId}"

                if (pendingLike.isLike) {
                    firestore.collection("likes").document(likeId).set(
                        mapOf(
                            "userId" to pendingLike.userId,
                            "postId" to pendingLike.postId,
                            "createdAt" to pendingLike.createdAt
                        )
                    ).await()

                    firestore.collection("posts").document(pendingLike.postId)
                        .update(
                            "likesCount",
                            com.google.firebase.firestore.FieldValue.increment(1)
                        ).await()
                } else {
                    firestore.collection("likes").document(likeId).delete().await()

                    firestore.collection("posts").document(pendingLike.postId)
                        .update(
                            "likesCount",
                            com.google.firebase.firestore.FieldValue.increment(-1)
                        ).await()
                }

                database.pendingLikeDao().deletePendingLike(pendingLike.id)

            } catch (e: Exception) {
                // keep pending — will retry next sync
            }
        }
    }

    private suspend fun cacheFeed() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        val snapshot = firestore.collection("posts")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()

        val posts = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Post::class.java)
        }

        if (posts.isNotEmpty()) {
            database.postDao().clearAllPosts()
            database.postDao().insertPosts(posts.map { it.toEntity() })
        }
    }
}