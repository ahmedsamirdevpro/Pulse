package com.ahmedsamir.pulse.feature.notifications.domain.usecase

import com.ahmedsamir.pulse.core.model.Notification
import com.ahmedsamir.pulse.feature.notifications.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    operator fun invoke(): Flow<List<Notification>> =
        notificationsRepository.getNotifications()
}