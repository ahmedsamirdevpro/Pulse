package com.ahmedsamir.pulse.feature.feed.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmedsamir.pulse.core.common.toFormattedCount
import com.ahmedsamir.pulse.core.common.toFormattedDate
import com.ahmedsamir.pulse.core.model.Post
import com.ahmedsamir.pulse.core.ui.component.UserAvatar
import com.ahmedsamir.pulse.core.ui.theme.PulseGray
import com.ahmedsamir.pulse.core.ui.theme.PulseRed

@Composable
fun PostCard(
    post: Post,
    onLikeClick: (String, Boolean) -> Unit,
    onCommentClick: (String) -> Unit,
    onRepostClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick(post.id) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            UserAvatar(
                imageUrl = post.author.profileImageUrl,
                displayName = post.author.displayName,
                size = 44.dp,
                modifier = Modifier.clickable { onProfileClick(post.author.id) }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = post.author.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "@${post.author.username}",
                        fontSize = 14.sp,
                        color = PulseGray
                    )
                    Text(
                        text = "·",
                        fontSize = 14.sp,
                        color = PulseGray
                    )
                    Text(
                        text = post.createdAt.toFormattedDate(),
                        fontSize = 14.sp,
                        color = PulseGray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post.content,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionButton(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ModeComment,
                                contentDescription = "Comment",
                                tint = PulseGray,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        count = post.commentsCount.toFormattedCount(),
                        onClick = { onCommentClick(post.id) }
                    )

                    ActionButton(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Repost",
                                tint = if (post.isReposted)
                                    MaterialTheme.colorScheme.primary else PulseGray,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        count = post.repostsCount.toFormattedCount(),
                        onClick = { onRepostClick(post.id) }
                    )

                    ActionButton(
                        icon = {
                            Icon(
                                imageVector = if (post.isLiked)
                                    Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (post.isLiked) PulseRed else PulseGray,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        count = post.likesCount.toFormattedCount(),
                        onClick = { onLikeClick(post.id, post.isLiked) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
    }
}

@Composable
private fun ActionButton(
    icon: @Composable () -> Unit,
    count: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        icon()
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = count,
            fontSize = 13.sp,
            color = PulseGray
        )
    }
}