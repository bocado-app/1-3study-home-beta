package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_posts")
data class StudyPost(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subject: String, // 국어, 수학, 사회, 과학, 영어, 한국사
    val title: String,
    val content: String,
    val keyPoints: String = "",
    val authorName: String,
    val authorEmoji: String = "🎓",
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val commentCount: Int = 0
)
