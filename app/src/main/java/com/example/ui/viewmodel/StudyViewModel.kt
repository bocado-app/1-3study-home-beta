package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Comment
import com.example.data.model.StudyPost
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyRepository
    private val prefs = application.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)

    val selectedSubject = MutableStateFlow("전체")
    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow("latest") // "latest", "popular", "comments"

    val userNickname = MutableStateFlow(prefs.getString("nickname", "1학년 3반 학생") ?: "1학년 3반 학생")
    val userEmoji = MutableStateFlow(prefs.getString("emoji", "🎓") ?: "🎓")

    private val _activePost = MutableStateFlow<StudyPost?>(null)
    val activePost: StateFlow<StudyPost?> = _activePost.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    private val _showShareDialog = MutableStateFlow(false)
    val showShareDialog: StateFlow<Boolean> = _showShareDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StudyRepository(database.studyDao())
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: StateFlow<List<StudyPost>> = selectedSubject
        .flatMapLatest { subject ->
            repository.getPostsBySubject(subject)
        }
        .flatMapLatest { postList ->
            searchQuery.map { query ->
                val filtered = if (query.isBlank()) {
                    postList
                } else {
                    postList.filter { post ->
                        post.title.contains(query, ignoreCase = true) ||
                                post.content.contains(query, ignoreCase = true) ||
                                post.authorName.contains(query, ignoreCase = true) ||
                                post.keyPoints.contains(query, ignoreCase = true)
                    }
                }
                when (sortOrder.value) {
                    "popular" -> filtered.sortedByDescending { it.likesCount }
                    "comments" -> filtered.sortedByDescending { it.commentCount }
                    else -> filtered.sortedByDescending { it.timestamp }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val activePostComments: StateFlow<List<Comment>> = _activePost
        .flatMapLatest { post ->
            if (post != null) {
                repository.getCommentsForPost(post.id)
            } else {
                MutableStateFlow(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectSubject(subject: String) {
        selectedSubject.value = subject
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSortOrder(order: String) {
        sortOrder.value = order
    }

    fun openCreateDialog(defaultSubject: String = "국어") {
        if (selectedSubject.value != "전체") {
            // Keep selected subject as default
        }
        _showCreateDialog.value = true
    }

    fun closeCreateDialog() {
        _showCreateDialog.value = false
    }

    fun openProfileDialog() {
        _showProfileDialog.value = true
    }

    fun closeProfileDialog() {
        _showProfileDialog.value = false
    }

    fun openShareDialog() {
        _showShareDialog.value = true
    }

    fun closeShareDialog() {
        _showShareDialog.value = false
    }

    fun saveUserProfile(name: String, emoji: String) {
        val cleanName = name.ifBlank { "1학년 3반 학생" }
        userNickname.value = cleanName
        userEmoji.value = emoji
        prefs.edit()
            .putString("nickname", cleanName)
            .putString("emoji", emoji)
            .apply()
        _showProfileDialog.value = false
        showToast("프로필이 업데이트되었습니다: $cleanName ($emoji)")
    }

    fun createPost(subject: String, title: String, content: String, keyPoints: String) {
        if (title.isBlank() || content.isBlank()) {
            showToast("제목과 요약 내용을 입력해주세요.")
            return
        }

        viewModelScope.launch {
            val newPost = StudyPost(
                subject = subject,
                title = title.trim(),
                content = content.trim(),
                keyPoints = keyPoints.trim(),
                authorName = userNickname.value,
                authorEmoji = userEmoji.value,
                timestamp = System.currentTimeMillis()
            )
            repository.insertPost(newPost)
            _showCreateDialog.value = false
            showToast("오늘의 [$subject] 학습 요약이 반 커뮤니티에 저장·공유되었습니다!")
        }
    }

    fun toggleLike(post: StudyPost) {
        viewModelScope.launch {
            repository.toggleLike(post)
            if (_activePost.value?.id == post.id) {
                val updatedLiked = !post.isLiked
                val updatedCount = if (updatedLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                _activePost.value = post.copy(isLiked = updatedLiked, likesCount = updatedCount)
            }
        }
    }

    fun deletePost(post: StudyPost) {
        viewModelScope.launch {
            repository.deletePost(post)
            if (_activePost.value?.id == post.id) {
                _activePost.value = null
            }
            showToast("게시물이 삭제되었습니다.")
        }
    }

    fun updatePost(post: StudyPost, newTitle: String, newContent: String, newKeyPoints: String) {
        viewModelScope.launch {
            val updated = post.copy(
                title = newTitle.trim(),
                content = newContent.trim(),
                keyPoints = newKeyPoints.trim()
            )
            repository.updatePost(updated)
            if (_activePost.value?.id == post.id) {
                _activePost.value = updated
            }
            showToast("게시물이 수정되었습니다.")
        }
    }

    fun openPostDetail(post: StudyPost) {
        _activePost.value = post
    }

    fun closePostDetail() {
        _activePost.value = null
    }

    fun addComment(postId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.addComment(
                postId = postId,
                authorName = userNickname.value,
                authorEmoji = userEmoji.value,
                content = content.trim()
            )
            // Update active post comment count
            _activePost.value?.let { current ->
                _activePost.value = current.copy(commentCount = current.commentCount + 1)
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun showToast(msg: String) {
        _toastMessage.value = msg
    }
}
