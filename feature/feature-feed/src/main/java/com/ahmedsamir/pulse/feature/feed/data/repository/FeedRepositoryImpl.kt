package com.ahmedsamir.pulse.feature.feed.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ahmedsamir.pulse.core.common.Constants
import com.ahmedsamir.pulse.core.common.NotificationHelper
import com.ahmedsamir.pulse.core.database.PulseDatabase
import com.ahmedsamir.pulse.core.database.mapper.toDomain
import com.ahmedsamir.pulse.core.database.mapper.toEntity
import com.ahmedsamir.pulse.core.model.NotificationType
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.feed.data.mediator.FeedRemoteMediator
import com.ahmedsamir.pulse.feature.feed.data.pagingsource.FeedPagingSource
import com.ahmedsamir.pulse.feature.feed.domain.repository.FeedRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val database: PulseDatabase,
    private val feedRemoteMediator: FeedRemoteMediator,
    private val notificationHelper: NotificationHelper
) : FeedRepository {

    override fun getFeed(): Flow<PagingData<Post>> {
        @OptIn(androidx.paging.ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = Constants.POSTS_PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            remoteMediator = feedRemoteMediator,
            pagingSourceFactory = {
                database.postDao().getPostsPaged()
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getUserPosts(userId: String): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.POSTS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                database.postDao().getUserPostsPaged(userId)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun likePost(postId: String, isLiked: Boolean): Resource<Unit> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            val likeId = "${currentUserId}_${postId}"

            if (isLiked) {
                // Unlike
                firestore.collection("likes").document(likeId).delete().await()
                firestore.collection("posts").document(postId)
                    .update("likesCount",
                        com.google.firebase.firestore.FieldValue.increment(-1))
                    .await()
            } else {
                // Like
                firestore.collection("likes").document(likeId).set(
                    mapOf(
                        "userId" to currentUserId,
                        "postId" to postId,
                        "createdAt" to System.currentTimeMillis()
                    )
                ).await()

                firestore.collection("posts").document(postId)
                    .update("likesCount",
                        com.google.firebase.firestore.FieldValue.increment(1))
                    .await()

                // ← Send notification
                val postDoc = firestore.collection("posts").document(postId).get().await()
                val postAuthorId = postDoc.getString("authorId") ?: ""

                val senderDoc = firestore.collection("users")
                    .document(currentUserId).get().await()
                val sender = senderDoc.toObject(com.ahmedsamir.pulse.core.model.User::class.java)

                if (sender != null && postAuthorId.isNotEmpty()) {
                    notificationHelper.sendNotification(
                        recipientId = postAuthorId,
                        sender = sender,
                        type = NotificationType.LIKE,
                        postId = postId
                    )
                }
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to like post")
        }
    }

    override suspend fun unlikePost(postId: String): Resource<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: return Resource.Error("Not logged in")
            val likeId = "${userId}_${postId}"

            firestore.collection("likes").document(likeId).delete().await()

            firestore.collection("posts").document(postId)
                .update("likesCount", com.google.firebase.firestore.FieldValue.increment(-1))
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unlike post")
        }
    }

    override suspend fun repostPost(postId: String): Resource<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: return Resource.Error("Not logged in")

            firestore.collection("reposts").document("${userId}_${postId}").set(
                mapOf("userId" to userId, "postId" to postId, "createdAt" to System.currentTimeMillis())
            ).await()

            firestore.collection("posts").document(postId)
                .update("repostsCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to repost")
        }
    }

    override suspend fun deletePost(postId: String): Resource<Unit> {
        return try {
            firestore.collection("posts").document(postId).delete().await()
            database.postDao().deletePost(postId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete post")
        }
    }

    override suspend fun getPostById(postId: String): Resource<Post> {
        return try {
            val doc = firestore.collection("posts").document(postId).get().await()
            val post = doc.toObject(Post::class.java) ?: return Resource.Error("Post not found")
            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get post")
        }
    }

    override fun observePost(postId: String): Flow<Post?> {
        return database.postDao().getPostById(postId).map { it?.toDomain() }
    }
}