package com.ahmedsamir.pulse.feature.feed.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedPagingSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : PagingSource<Any, Post>() {

    override suspend fun load(params: LoadParams<Any>): LoadResult<Any, Post> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""

            var query = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(params.loadSize.toLong())

            if (params.key != null) {
                val lastDoc = firestore.collection("posts")
                    .document(params.key.toString())
                    .get()
                    .await()
                query = query.startAfter(lastDoc)
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

            val nextKey = if (snapshot.documents.isEmpty() ||
                snapshot.documents.size < params.loadSize) null
            else snapshot.documents.last().id

            LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Any, Post>): Any? = null
}