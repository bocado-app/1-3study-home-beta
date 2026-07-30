package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorEmoji: String = "✍️",
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
