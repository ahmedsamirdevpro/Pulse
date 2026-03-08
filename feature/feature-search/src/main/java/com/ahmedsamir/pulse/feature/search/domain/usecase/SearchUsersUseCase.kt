package com.ahmedsamir.pulse.feature.search.domain.usecase

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.search.domain.repository.SearchRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Resource<List<User>> {
        if (query.isBlank()) return Resource.Success(emptyList())
        if (query.length < 2) return Resource.Success(emptyList())
        return searchRepository.searchUsers(query.trim())
    }
}