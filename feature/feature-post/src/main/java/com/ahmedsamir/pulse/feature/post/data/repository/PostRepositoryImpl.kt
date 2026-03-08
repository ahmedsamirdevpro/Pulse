package com.ahmedsamir.pulse.feature.post.data.repository

import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.post.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage
) : PostRepository {

    override suspend fun createPost(content: String, imageUri: String?): Resource<Post> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Resource.Error("Not logged in")

            val userDoc = firestore.collection("users").document(userId).get().await()
            val author = userDoc.toObject(User::class.java) ?: User(id = userId)

            var imageUrl = ""
            if (!imageUri.isNullOrEmpty()) {
                val imageRef = storage.reference
                    .child("posts/${UUID.randomUUID()}.jpg")
                val uploadTask = imageRef.putFile(android.net.Uri.parse(imageUri)).await()
                imageUrl = uploadTask.storage.downloadUrl.await().toString()
            }

            val postId = UUID.randomUUID().toString()
            val post = Post(
                id = postId,
                authorId = userId,
                author = author,
                content = content,
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            firestore.collection("posts").document(postId).set(post).await()

            firestore.collection("users").document(userId)
                .update("postsCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create post")
        }
    }

    override suspend fun deletePost(postId: String): Resource<Unit> {
        return try {
            firestore.collection("posts").document(postId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete post")
        }
    }

    override suspend fun getPostById(postId: String): Resource<Post> {
        return try {
            val doc = firestore.collection("posts").document(postId).get().await()
            val post = doc.toObject(Post::class.java)
                ?: return Resource.Error("Post not found")
            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get post")
        }
    }
}