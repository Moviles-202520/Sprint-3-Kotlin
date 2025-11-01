package com.example.sprint_2_kotlin.model.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface CommentDao {
    @Insert
    suspend fun insert(comment: PendingComment)
    @Query("SELECT * FROM pending_comments") suspend fun getAll(): List<PendingComment>
    @Delete
    suspend fun delete(comment: PendingComment)
}