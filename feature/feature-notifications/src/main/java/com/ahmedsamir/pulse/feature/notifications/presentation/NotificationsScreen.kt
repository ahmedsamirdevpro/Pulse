package com.ahmedsamir.pulse.feature.notifications.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahmedsamir.pulse.core.common.toFormattedDate
import com.ahmedsamir.pulse.core.model.Notification
import com.ahmedsamir.pulse.core.model.NotificationType
import com.ahmedsamir.pulse.core.ui.component.UserAvatar
import com.ahmedsamir.pulse.core.ui.theme.PulseGray
import com.ahmedsamir.pulse.core.ui.theme.PulseGreen
import com.ahmedsamir.pulse.core.ui.theme.PulseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onProfileClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    if (uiState.unreadCount > 0) {
                        TextButton(onClick = viewModel::markAllRead) {
                            Text(
                                text = "Mark all read",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.notifications.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications yet",
                        color = PulseGray
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(uiState.notifications, key = { it.id }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onProfileClick = onProfileClick,
                            onPostClick = onPostClick
                        )
                        Divider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onProfileClick: (String) -> Unit,
    onPostClick: (String) -> Unit
) {
    val bgColor = if (!notification.isRead)
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    else MaterialTheme.colorScheme.background

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable {
                if (notification.postId.isNotEmpty()) onPostClick(notification.postId)
                else onProfileClick(notification.senderId)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box {
            UserAvatar(
                imageUrl = notification.sender.profileImageUrl,
                displayName = notification.sender.displayName,
                size = 44.dp,
                modifier = Modifier.clickable { onProfileClick(notification.senderId) }
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .background(
                        color = when (notification.type) {
                            NotificationType.LIKE -> PulseRed
                            NotificationType.FOLLOW -> MaterialTheme.colorScheme.primary
                            NotificationType.COMMENT -> MaterialTheme.colorScheme.primary
                            NotificationType.REPOST -> PulseGreen
                            NotificationType.MENTION -> MaterialTheme.colorScheme.primary
                        },
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.LIKE -> Icons.Default.Favorite
                        NotificationType.FOLLOW -> Icons.Default.PersonAdd
                        NotificationType.COMMENT -> Icons.Default.ModeComment
                        NotificationType.REPOST -> Icons.Default.Repeat
                        NotificationType.MENTION -> Icons.Default.ModeComment
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.message.ifEmpty {
                    when (notification.type) {
                        NotificationType.LIKE -> "${notification.sender.displayName} liked your post"
                        NotificationType.COMMENT -> "${notification.sender.displayName} commented on your post"
                        NotificationType.FOLLOW -> "${notification.sender.displayName} followed you"
                        NotificationType.REPOST -> "${notification.sender.displayName} reposted your post"
                        NotificationType.MENTION -> "${notification.sender.displayName} mentioned you"
                    }
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = notification.createdAt.toFormattedDate(),
                fontSize = 12.sp,
                color = PulseGray
            )
        }
    }
}