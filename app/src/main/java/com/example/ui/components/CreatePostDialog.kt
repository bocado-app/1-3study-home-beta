package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EditorialPurple
import com.example.ui.theme.getSubjectBgColor
import com.example.ui.theme.getSubjectColor
import com.example.ui.theme.getSubjectIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostDialog(
    initialSubject: String,
    userNickname: String,
    userEmoji: String,
    onDismiss: () -> Unit,
    onSubmit: (subject: String, title: String, content: String, keyPoints: String) -> Unit
) {
    val subjects = listOf("국어", "수학", "사회", "과학", "영어", "한국사")
    var selectedSubject by remember {
        mutableStateOf(if (initialSubject in subjects) initialSubject else "국어")
    }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var keyPoints by remember { mutableStateOf("") }

    var activeTarget by remember { mutableStateOf("title") }
    var isKeyboardOpen by remember { mutableStateOf(false) }

    val activeSubjectColor = getSubjectColor(selectedSubject)
    val activeSubjectBg = getSubjectBgColor(selectedSubject)

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
                    .testTag("create_post_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✏️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "오늘의 학습 요약 작성",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D1B20)
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = Color(0xFF616161)
                        )
                    }
                }

                Text(
                    text = "작성하신 공부 요약은 실시간으로 반 게시판에 공유됩니다.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF616161))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subject Selection Label & Chips
                Text(
                    text = "1. 과목 선택",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subjects.forEach { subj ->
                        val isSel = subj == selectedSubject
                        val color = getSubjectColor(subj)
                        val icon = getSubjectIcon(subj)

                        Surface(
                            color = if (isSel) color else Color(0xFFF3EDF7),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { selectedSubject = subj }
                                .testTag("subject_select_$subj")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = subj,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else Color(0xFF1D1B20)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var activeTarget by remember { mutableStateOf("title") } // "title", "content", "keyPoints"
                var isKeyboardOpen by remember { mutableStateOf(true) }

                // Title Input
                Text(
                    text = "2. 학습 주제/제목",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_title_input"),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        placeholder = {
                            Text(
                                text = "예: 단원 정리 - 삼국시대 한강 유역 차지 과정",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF757575))
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            cursorColor = Color(0xFF1D1B20),
                            focusedBorderColor = activeSubjectColor,
                            unfocusedBorderColor = Color(0xFFCAC4D0)
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                activeTarget = "title"
                                isKeyboardOpen = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content Input
                Text(
                    text = "3. 상세 학습 요약 내용",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("post_content_input"),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        placeholder = {
                            Text(
                                text = "오늘 배운 주요 개념, 중요 공식, 암기 포인트나 틀리기 쉬운 문제를 작성하세요...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF757575))
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            cursorColor = Color(0xFF1D1B20),
                            focusedBorderColor = activeSubjectColor,
                            unfocusedBorderColor = Color(0xFFCAC4D0)
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                activeTarget = "content"
                                isKeyboardOpen = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Key Points Input + AI Generation Assistant Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "4. 핵심 포인트 요약 (선택)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20)
                        )
                    )

                    // AI Key Points Auto Generator Button
                    Surface(
                        color = activeSubjectBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable {
                                if (content.isNotBlank()) {
                                    val lines = content.lines().filter { it.isNotBlank() }
                                    keyPoints = if (lines.isNotEmpty()) {
                                        lines.take(3).joinToString("\n") { "• " + it.take(25) }
                                    } else {
                                        "• 핵심 개념 1단계 암기\n• 자주 나오는 기출 문제 패턴 체크\n• 실수하기 쉬운 포인트 주의"
                                    }
                                } else {
                                    keyPoints = "• $selectedSubject 필수 교과 핵심 개념 정리\n• 시험 단골 출제 키워드 파악\n• 오답 노트 예시 확인"
                                }
                            }
                            .border(1.dp, activeSubjectColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = activeSubjectColor,
                                modifier = Modifier.height(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✨ AI 핵심요약 자동생성",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = activeSubjectColor
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = keyPoints,
                        onValueChange = { keyPoints = it },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("post_keypoints_input"),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        placeholder = {
                            Text(
                                text = "• 한눈에 보는 핵심 요약 1\n• 한눈에 보는 핵심 요약 2",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF757575))
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1D1B20),
                            unfocusedTextColor = Color(0xFF1D1B20),
                            cursorColor = Color(0xFF1D1B20),
                            focusedBorderColor = activeSubjectColor,
                            unfocusedBorderColor = Color(0xFFCAC4D0)
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                activeTarget = "keyPoints"
                                isKeyboardOpen = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Author Info Bar
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = userEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "작성자: ",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF616161))
                        )
                        Text(
                            text = userNickname,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("취소", color = Color(0xFF616161))
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSubmit(selectedSubject, title, content, keyPoints)
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("submit_post_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activeSubjectColor
                        )
                    ) {
                        Text(
                            text = "게시판에 등록",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        OnScreenVirtualKeyboard(
            currentText = when (activeTarget) {
                "content" -> content
                "keyPoints" -> keyPoints
                else -> title
            },
            onTextChange = { updated ->
                when (activeTarget) {
                    "content" -> content = updated
                    "keyPoints" -> keyPoints = updated
                    else -> title = updated
                }
            },
            isVisible = isKeyboardOpen,
            onClose = { isKeyboardOpen = false },
            headerContent = {
                Surface(
                    color = Color(0xFF282836),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "입력 대상: ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        val targets = listOf("title" to "📌 제목", "content" to "📄 내용", "keyPoints" to "💡 핵심요약")
                        targets.forEach { (key, label) ->
                            val isSelected = activeTarget == key
                            Surface(
                                color = if (isSelected) activeSubjectColor else Color(0xFF424258),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { activeTarget = key }
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
}
