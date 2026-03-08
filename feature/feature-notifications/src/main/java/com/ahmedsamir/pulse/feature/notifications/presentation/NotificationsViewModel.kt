package com.ahmedsamir.pulse.feature.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedsamir.pulse.core.model.Notification
import com.ahmedsamir.pulse.feature.notifications.domain.repository.NotificationsRepository
import com.ahmedsamir.pulse.feature.notifications.domain.usecase.GetNotificationsUseCase
import com.ahmedsamir.pulse.feature.notifications.domain.usecase.MarkAllReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markAllReadUseCase: MarkAllReadUseCase,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getNotificationsUseCase().collect { notifications ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notifications = notifications,
                        unreadCount = notifications.count { n -> !n.isRead }
                    )
                }
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            markAllReadUseCase()
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationsRepository.markAsRead(notificationId)
        }
    }
}

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0
)