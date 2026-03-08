package com.ahmedsamir.pulse.feature.feed.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ahmedsamir.pulse.core.database.PulseDatabase
import com.ahmedsamir.pulse.core.database.entity.PostEntity
import com.ahmedsamir.pulse.core.database.mapper.toEntity
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val database: PulseDatabase
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheCount = database.postDao().getPostsCount()
        return if (cacheCount > 0) InitializeAction.SKIP_INITIAL_REFRESH
        else InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""

            when (loadType) {
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.REFRESH -> database.postDao().clearAllPosts()
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val pageSize = state.config.pageSize.toLong()

            var query = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(pageSize)

            if (loadType == LoadType.APPEND) {
                val lastPost = state.lastItemOrNull()
                if (lastPost != null) {
                    val lastDoc = firestore.collection("posts")
                        .document(lastPost.id)
                        .get()
                        .await()
                    query = query.startAfter(lastDoc)
                }
            }

            val snapshot = query.get().await()

            val posts = snapshot.documents.mapNotNull { doc ->
                val post = doc.toObject(Post::class.java) ?: return@mapNotNull null
                val authorDoc = firestore.collection("users")
                    .document(post.authorId)
                    .get()
                    .await()
                val author = authorDoc.toObject(User::class.java) ?: User()
                val isLiked = firestore.collection("likes")
                    .document("${currentUserId}_${post.id}")
                    .get()
                    .await()
                    .exists()
                post.copy(author = author, isLiked = isLiked)
            }

            database.postDao().insertPosts(posts.map { it.toEntity() })

            MediatorResult.Success(
                endOfPaginationReached = snapshot.documents.size < pageSize
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}