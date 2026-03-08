package com.ahmedsamir.pulse.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ahmedsamir.pulse.core.database.entity.PendingPostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingPost(post: PendingPostEntity)

    @Query("SELECT * FROM pending_posts ORDER BY createdAt ASC")
    fun getPendingPosts(): Flow<List<PendingPostEntity>>

    @Query("SELECT * FROM pending_posts ORDER BY createdAt ASC")
    suspend fun getPendingPostsOnce(): List<PendingPostEntity>

    @Update
    suspend fun updatePendingPost(post: PendingPostEntity)

    @Query("DELETE FROM pending_posts WHERE localId = :localId")
    suspend fun deletePendingPost(localId: String)

    @Query("SELECT COUNT(*) FROM pending_posts")
    fun getPendingCount(): Flow<Int>
}