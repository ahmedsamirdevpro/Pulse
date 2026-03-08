package com.ahmedsamir.pulse.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ahmedsamir.pulse.core.database.entity.PendingLikeEntity

@Dao
interface PendingLikeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingLike(like: PendingLikeEntity)

    @Query("SELECT * FROM pending_likes ORDER BY createdAt ASC")
    suspend fun getPendingLikes(): List<PendingLikeEntity>

    @Query("DELETE FROM pending_likes WHERE id = :id")
    suspend fun deletePendingLike(id: String)

    @Query("DELETE FROM pending_likes")
    suspend fun clearAllPendingLikes()
}