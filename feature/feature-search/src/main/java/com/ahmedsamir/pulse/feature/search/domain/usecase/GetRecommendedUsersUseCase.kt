package com.ahmedsamir.pulse.feature.search.domain.usecase

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.search.domain.repository.SearchRepository
import javax.inject.Inject

class GetRecommendedUsersUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(): Resource<List<User>> =
        searchRepository.getRecommendedUsers()
}