package com.example.data.repository

import com.example.data.db.StudyDao
import com.example.data.model.Comment
import com.example.data.model.StudyPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudyRepository(private val studyDao: StudyDao) {

    val allPosts: Flow<List<StudyPost>> = studyDao.getAllPosts()

    fun getPostsBySubject(subject: String): Flow<List<StudyPost>> {
        return if (subject == "전체" || subject.isEmpty()) {
            studyDao.getAllPosts()
        } else {
            studyDao.getPostsBySubject(subject)
        }
    }

    fun getPostById(postId: Int): Flow<StudyPost?> = studyDao.getPostById(postId)

    fun getCommentsForPost(postId: Int): Flow<List<Comment>> = studyDao.getCommentsForPost(postId)

    suspend fun insertPost(post: StudyPost): Long = withContext(Dispatchers.IO) {
        studyDao.insertPost(post)
    }

    suspend fun toggleLike(post: StudyPost) = withContext(Dispatchers.IO) {
        val updatedLiked = !post.isLiked
        val updatedCount = if (updatedLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        studyDao.updatePost(post.copy(isLiked = updatedLiked, likesCount = updatedCount))
    }

    suspend fun addComment(postId: Int, authorName: String, authorEmoji: String, content: String) = withContext(Dispatchers.IO) {
        val comment = Comment(
            postId = postId,
            authorName = authorName,
            authorEmoji = authorEmoji,
            content = content
        )
        studyDao.insertComment(comment)
        studyDao.incrementCommentCount(postId)
    }

    suspend fun deletePost(post: StudyPost) = withContext(Dispatchers.IO) {
        studyDao.deletePost(post)
    }

    suspend fun updatePost(post: StudyPost) = withContext(Dispatchers.IO) {
        studyDao.updatePost(post)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        studyDao.deleteAllComments()
        studyDao.deleteAllPosts()
    }

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        // Clean up any previously generated fake posts so only real user posts are saved
        studyDao.deleteSeedPosts()
    }
}
