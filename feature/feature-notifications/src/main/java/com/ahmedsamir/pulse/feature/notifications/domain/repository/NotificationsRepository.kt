package com.ahmedsamir.pulse.feature.notifications.domain.repository

import com.ahmedsamir.pulse.core.model.Notification
import com.ahmedsamir.pulse.core.model.Resource
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    fun getNotifications(): Flow<List<Notification>>
    suspend fun markAsRead(notificationId: String): Resource<Unit>
    suspend fun markAllAsRead(): Resource<Unit>
    fun getUnreadCount(): Flow<Int>
}