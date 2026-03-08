package com.ahmedsamir.pulse.sync

import com.ahmedsamir.pulse.core.common.NetworkMonitor
import com.ahmedsamir.pulse.core.database.PulseDatabase
import com.ahmedsamir.pulse.core.database.entity.PendingLikeEntity
import com.ahmedsamir.pulse.core.database.entity.PendingPostEntity
import com.ahmedsamir.pulse.core.model.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflinePostRepository @Inject constructor(
    private val database: PulseDatabase,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager,
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun createPostOfflineFirst(content: String, imageUri: String?): Resource<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            val pendingPost = PendingPostEntity(
                authorId = userId,
                content = content,
                imageUri = imageUri ?: ""
            )

            database.pendingPostDao().insertPendingPost(pendingPost)

            val isOnline = networkMonitor.isOnline.first()
            if (isOnline) syncManager.syncNow()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save post")
        }
    }

    suspend fun likePostOfflineFirst(postId: String, isLike: Boolean): Resource<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            val pendingLike = PendingLikeEntity(
                id = UUID.randomUUID().toString(),
                postId = postId,
                userId = userId,
                isLike = isLike
            )

            database.pendingLikeDao().insertPendingLike(pendingLike)

            database.postDao().updatePostLike(
                postId = postId,
                isLiked = isLike,
                likesCount = if (isLike) 1 else -1
            )

            val isOnline = networkMonitor.isOnline.first()
            if (isOnline) syncManager.syncNow()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to process like")
        }
    }

    fun getPendingPostsCount() = database.pendingPostDao().getPendingCount()
}