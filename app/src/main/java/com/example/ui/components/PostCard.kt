package com.example.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyPost
import com.example.ui.theme.getSubjectBgColor
import com.example.ui.theme.getSubjectColor
import com.example.ui.theme.getSubjectIcon

@Composable
fun PostCard(
    post: StudyPost,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectColor = getSubjectColor(post.subject)
    val subjectBgColor = getSubjectBgColor(post.subject)
    val icon = getSubjectIcon(post.subject)

    val relativeTime = DateUtils.getRelativeTimeSpanString(
        post.timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row: Subject Badge & Time Ago
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Subject Badge Tag
                Surface(
                    color = subjectBgColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = icon, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.subject,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = subjectColor
                            )
                        )
                    }
                }

                // Author & Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F2F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = post.authorEmoji, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                    )
                    Text(
                        text = " • $relativeTime",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF49454F),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Post Content snippet
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF1D1B20),
                    lineHeight = 20.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Key Points Highlight Box (if available)
            if (post.keyPoints.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = subjectBgColor.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 핵심 포인트 요약",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = subjectColor
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = post.keyPoints,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF1D1B20),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Actions: Likes & Comments Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Like button & count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onLikeClick() }
                    ) {
                        IconButton(
                            onClick = { onLikeClick() },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("like_btn_${post.id}")
                        ) {
                            Icon(
                                imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "좋아요",
                                tint = if (post.isLiked) Color(0xFFE53935) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (post.isLiked) Color(0xFFE53935) else Color.Gray
                            )
                        )
                    }

                    // Comment icon & count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "댓글",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${post.commentCount}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        )
                    }
                }

                // "자세히 보기" text button indicator
                Text(
                    text = "자세히 보기 >",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = subjectColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
