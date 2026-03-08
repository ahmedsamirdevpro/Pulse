package com.ahmedsamir.pulse.feature.search.domain.repository

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User

interface SearchRepository {
    suspend fun searchUsers(query: String): Resource<List<User>>
    suspend fun getRecommendedUsers(): Resource<List<User>>
}