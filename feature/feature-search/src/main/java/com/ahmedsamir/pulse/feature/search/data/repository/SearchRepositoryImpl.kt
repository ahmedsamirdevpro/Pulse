package com.ahmedsamir.pulse.feature.search.data.repository

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.search.domain.repository.SearchRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : SearchRepository {

    override suspend fun searchUsers(query: String): Resource<List<User>> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""
            val queryLower = query.lowercase()

            val byUsername = firestore.collection("users")
                .whereGreaterThanOrEqualTo("username", queryLower)
                .whereLessThanOrEqualTo("username", queryLower + "\uf8ff")
                .limit(20)
                .get()
                .await()

            val byDisplayName = firestore.collection("users")
                .whereGreaterThanOrEqualTo("displayName", query)
                .whereLessThanOrEqualTo("displayName", query + "\uf8ff")
                .limit(20)
                .get()
                .await()

            val usersMap = mutableMapOf<String, User>()

            byUsername.documents.forEach { doc ->
                val user = doc.toObject(User::class.java) ?: return@forEach
                if (user.id != currentUserId) usersMap[user.id] = user
            }

            byDisplayName.documents.forEach { doc ->
                val user = doc.toObject(User::class.java) ?: return@forEach
                if (user.id != currentUserId) usersMap[user.id] = user
            }

            Resource.Success(usersMap.values.toList())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search failed")
        }
    }

    override suspend fun getRecommendedUsers(): Resource<List<User>> {
        return try {
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""

            val snapshot = firestore.collection("users")
                .orderBy("followersCount",
                    com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                val user = doc.toObject(User::class.java) ?: return@mapNotNull null
                if (user.id == currentUserId) null else user
            }

            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get recommendations")
        }
    }
}