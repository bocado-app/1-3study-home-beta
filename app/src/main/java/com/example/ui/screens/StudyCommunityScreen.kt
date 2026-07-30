package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudyPost
import com.example.ui.components.ClassHeader
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.PostCard
import com.example.ui.components.PostDetailDialog
import com.example.ui.components.ShareLinkDialog
import com.example.ui.components.SubjectChip
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.BackgroundEditorial
import com.example.ui.theme.EditorialDarkPurple
import com.example.ui.theme.EditorialFabPurple
import com.example.ui.theme.EditorialLightPurple
import com.example.ui.theme.EditorialPurple
import com.example.ui.theme.getSubjectColor
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun StudyCommunityScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val userNickname by viewModel.userNickname.collectAsStateWithLifecycle()
    val userEmoji by viewModel.userEmoji.collectAsStateWithLifecycle()

    val activePost by viewModel.activePost.collectAsStateWithLifecycle()
    val activeComments by viewModel.activePostComments.collectAsStateWithLifecycle()

    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()
    val showProfileDialog by viewModel.showProfileDialog.collectAsStateWithLifecycle()
    val showShareDialog by viewModel.showShareDialog.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val subjects = listOf("전체", "국어", "수학", "사회", "과학", "영어", "한국사")

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundEditorial,
        contentWindowInsets = WindowInsets.systemBars,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openCreateDialog(selectedSubject) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "학습 요약 작성",
                        tint = if (selectedSubject == "전체") EditorialDarkPurple else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = "오늘의 요약 작성",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSubject == "전체") EditorialDarkPurple else Color.White
                        )
                    )
                },
                containerColor = if (selectedSubject == "전체") EditorialFabPurple else getSubjectColor(selectedSubject),
                modifier = Modifier.testTag("create_post_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Class Header
            ClassHeader(
                totalPosts = posts.size,
                userNickname = userNickname,
                userEmoji = userEmoji,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onOpenProfile = { viewModel.openProfileDialog() },
                onOpenShare = { viewModel.openShareDialog() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Subject Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subject_filter_row"),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { subject ->
                    val count = if (subject == "전체") {
                        posts.size
                    } else {
                        posts.count { it.subject == subject }
                    }
                    SubjectChip(
                        subject = subject,
                        isSelected = subject == selectedSubject,
                        count = count,
                        onClick = { viewModel.selectSubject(subject) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Feed Header Info Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedSubject == "전체") "📌 전체 과목 스터디 피드" else "📌 $selectedSubject 요약 모음",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                )

                Text(
                    text = "${posts.size}개 등록됨",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF49454F),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // User Name Quick Info Banner & Sort Filter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EditorialLightPurple.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.openProfileDialog() }
                        .testTag("name_setting_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = userEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = userNickname,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = EditorialDarkPurple
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "✏️",
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sort Chips (최신순 / 인기순 / 댓글순)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val sortOptions = listOf(
                        "latest" to "🕒 최신",
                        "popular" to "🔥 인기",
                        "comments" to "💬 댓글"
                    )
                    sortOptions.forEach { (key, label) ->
                        val isSelected = sortOrder == key
                        Surface(
                            color = if (isSelected) EditorialDarkPurple else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { viewModel.setSortOrder(key) }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF424242)
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Posts List Feed
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "📚", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "'$searchQuery' 검색 결과가 없습니다." else "등록된 학습 요약이 없습니다.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151)
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "오늘 배운 내용을 첫 번째로 공유해서 반 친구들을 도와주세요!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) viewModel.setSearchQuery("")
                                else viewModel.openCreateDialog(selectedSubject)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPurple)
                        ) {
                            Text(if (searchQuery.isNotBlank()) "검색어 초기화" else "오늘의 요약 작성하기")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("post_feed_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = posts,
                        key = { post -> post.id }
                    ) { post ->
                        PostCard(
                            post = post,
                            onClick = { viewModel.openPostDetail(post) },
                            onLikeClick = { viewModel.toggleLike(post) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs Layer
    if (showCreateDialog) {
        CreatePostDialog(
            initialSubject = selectedSubject,
            userNickname = userNickname,
            userEmoji = userEmoji,
            onDismiss = { viewModel.closeCreateDialog() },
            onSubmit = { subject, title, content, keyPoints ->
                viewModel.createPost(subject, title, content, keyPoints)
            }
        )
    }

    activePost?.let { post ->
        PostDetailDialog(
            post = post,
            comments = activeComments,
            userNickname = userNickname,
            userEmoji = userEmoji,
            onDismiss = { viewModel.closePostDetail() },
            onLikeClick = { viewModel.toggleLike(post) },
            onAddComment = { commentText -> viewModel.addComment(post.id, commentText) },
            onDeletePost = { targetPost -> viewModel.deletePost(targetPost) },
            onUpdatePost = { targetPost, newTitle, newContent, newKeyPoints ->
                viewModel.updatePost(targetPost, newTitle, newContent, newKeyPoints)
            }
        )
    }

    if (showProfileDialog) {
        UserProfileDialog(
            currentName = userNickname,
            currentEmoji = userEmoji,
            onDismiss = { viewModel.closeProfileDialog() },
            onSave = { name, emoji -> viewModel.saveUserProfile(name, emoji) }
        )
    }

    if (showShareDialog) {
        ShareLinkDialog(
            onDismiss = { viewModel.closeShareDialog() }
        )
    }
}
