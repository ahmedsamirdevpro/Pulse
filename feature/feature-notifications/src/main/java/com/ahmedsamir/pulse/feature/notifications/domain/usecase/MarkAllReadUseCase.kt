package com.ahmedsamir.pulse.feature.notifications.domain.usecase

import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.notifications.domain.repository.NotificationsRepository
import javax.inject.Inject

class MarkAllReadUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    suspend operator fun invoke(): Resource<Unit> =
        notificationsRepository.markAllAsRead()
}