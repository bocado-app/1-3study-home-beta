package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Comment
import com.example.data.model.StudyPost
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<StudyPost>>

    @Query("SELECT * FROM study_posts WHERE subject = :subject ORDER BY timestamp DESC")
    fun getPostsBySubject(subject: String): Flow<List<StudyPost>>

    @Query("SELECT * FROM study_posts WHERE id = :postId")
    fun getPostById(postId: Int): Flow<StudyPost?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: StudyPost): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<StudyPost>)

    @Update
    suspend fun updatePost(post: StudyPost)

    @Delete
    suspend fun deletePost(post: StudyPost)

    @Query("DELETE FROM study_posts")
    suspend fun deleteAllPosts()

    @Query("DELETE FROM study_posts WHERE authorName IN ('김국어', '박수학', '이영어', '최과학')")
    suspend fun deleteSeedPosts()

    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()

    @Query("SELECT COUNT(*) FROM study_posts")
    suspend fun getPostCount(): Int

    // Comments
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long

    @Query("UPDATE study_posts SET commentCount = commentCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: Int)
}
