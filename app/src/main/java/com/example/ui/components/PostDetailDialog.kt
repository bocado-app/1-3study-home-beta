package com.example.ui.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Comment
import com.example.data.model.StudyPost
import com.example.ui.theme.getSubjectBgColor
import com.example.ui.theme.getSubjectColor
import com.example.ui.theme.getSubjectIcon

@Composable
fun PostDetailDialog(
    post: StudyPost,
    comments: List<Comment>,
    userNickname: String,
    userEmoji: String,
    onDismiss: () -> Unit,
    onLikeClick: () -> Unit,
    onAddComment: (String) -> Unit,
    onDeletePost: ((StudyPost) -> Unit)? = null,
    onUpdatePost: ((StudyPost, String, String, String) -> Unit)? = null
) {
    val subjectColor = getSubjectColor(post.subject)
    val subjectBgColor = getSubjectBgColor(post.subject)
    val icon = getSubjectIcon(post.subject)

    var commentText by remember { mutableStateOf("") }
    var isKeyboardOpen by remember { mutableStateOf(false) }
    var isQuizMode by remember { mutableStateOf(false) }
    var showQuizAnswer by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(post.title) }
    var editedContent by remember { mutableStateOf(post.content) }
    var editedKeyPoints by remember { mutableStateOf(post.keyPoints) }

    val canEditOrDelete = userNickname == "1311박태민" || userNickname == post.authorName

    val formattedTime = DateUtils.getRelativeTimeSpanString(
        post.timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (isKeyboardOpen) Alignment.TopCenter else Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(if (isKeyboardOpen) 0.52f else 0.88f)
                    .padding(top = if (isKeyboardOpen) 16.dp else 12.dp, bottom = 12.dp)
                    .testTag("post_detail_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Subject Chip
                    Surface(
                        color = subjectBgColor,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = post.subject,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = subjectColor
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Flashcard Quiz Toggle Button
                        Surface(
                            color = if (isQuizMode) Color(0xFFFFF3E0) else Color(0xFFF3EDF7),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    isQuizMode = !isQuizMode
                                    showQuizAnswer = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = if (isQuizMode) Color(0xFFE65100) else Color(0xFF49454F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isQuizMode) "학습모드 ON" else "🧠 암기암송 Quiz",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isQuizMode) Color(0xFFE65100) else Color(0xFF49454F)
                                    )
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = Color(0xFF616161)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Author Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(subjectBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = post.authorEmoji, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                            )
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF757575))
                            )
                        }
                    }

                    // Like Button & Admin Action Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (canEditOrDelete) {
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { isEditing = !isEditing }
                            ) {
                                Text(
                                    text = if (isEditing) "취소" else "✏️ 수정",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                color = Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        onDeletePost?.invoke(post)
                                        onDismiss()
                                    }
                            ) {
                                Text(
                                    text = "🗑️ 삭제",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC62828)
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Surface(
                            color = if (post.isLiked) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onLikeClick() }
                                .testTag("post_like_button_detail")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "좋아요",
                                    tint = if (post.isLiked) Color.Red else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${post.likesCount}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (post.isLiked) Color.Red else Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    // Post Edit Mode
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "✏️ 게시물 수정 모드 (1311박태민 권한)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        )

                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("제목") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = editedContent,
                            onValueChange = { editedContent = it },
                            label = { Text("요약 내용") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = editedKeyPoints,
                            onValueChange = { editedKeyPoints = it },
                            label = { Text("핵심 포인트 요약") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    onUpdatePost?.invoke(post, editedTitle, editedContent, editedKeyPoints)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("수정 완료 저장", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (isQuizMode) {
                    // Flashcard / Quiz Mode Layout
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "💡 빈칸 채우기 암기 테스트",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57F17)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "주요 핵심 포인트를 떠올려보세요!",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF5D4037))
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (showQuizAnswer) {
                                Text(
                                    text = post.content,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF1D1B20),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            } else {
                                Text(
                                    text = "❓ [정답 보기를 눌러 내용을 확인하세요]",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF8D6E63),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showQuizAnswer = !showQuizAnswer },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (showQuizAnswer) "정답 가리기" else "정답 확인하기",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // Standard Reading View
                    Surface(
                        color = Color(0xFFF8F9FA),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF212121),
                                    lineHeight = 22.sp
                                )
                            )

                            if (post.keyPoints.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFE0E0E0))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = subjectColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "핵심 요약 포인트",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = subjectColor
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    color = subjectBgColor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = post.keyPoints,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF1D1B20),
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comments Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💬 댓글 목록 (${comments.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Comment List
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    if (comments.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "아직 댓글이 없습니다. 첫번째 응원을 남겨보세요!",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(comments) { comment ->
                                Surface(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(text = comment.authorEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = comment.authorName,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1D1B20)
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = comment.content,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF1D1B20)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.height(6.dp))

                // New Comment Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("comment_input_field"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            placeholder = {
                                Text(
                                    text = "응원 한마디나 질문 남기기...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF757575))
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1D1B20),
                                unfocusedTextColor = Color(0xFF1D1B20),
                                cursorColor = Color(0xFF1D1B20),
                                focusedBorderColor = subjectColor,
                                unfocusedBorderColor = Color(0xFFCAC4D0)
                            )
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { isKeyboardOpen = true }
                        )
                    }

                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onAddComment(commentText)
                                commentText = ""
                            }
                        },
                        modifier = Modifier.testTag("submit_comment_button"),
                        enabled = commentText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = subjectColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "댓글 등록",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        OnScreenVirtualKeyboard(
            currentText = commentText,
            onTextChange = { commentText = it },
            isVisible = isKeyboardOpen,
            onClose = { isKeyboardOpen = false },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
}
