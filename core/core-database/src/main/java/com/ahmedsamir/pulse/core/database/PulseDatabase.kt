package com.ahmedsamir.pulse.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ahmedsamir.pulse.core.database.dao.PendingLikeDao
import com.ahmedsamir.pulse.core.database.dao.PendingPostDao
import com.ahmedsamir.pulse.core.database.dao.PostDao
import com.ahmedsamir.pulse.core.database.dao.UserDao
import com.ahmedsamir.pulse.core.database.entity.PendingLikeEntity
import com.ahmedsamir.pulse.core.database.entity.PendingPostEntity
import com.ahmedsamir.pulse.core.database.entity.PostEntity
import com.ahmedsamir.pulse.core.database.entity.UserEntity

@Database(
    entities = [
        PostEntity::class,
        UserEntity::class,
        PendingPostEntity::class,
        PendingLikeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PulseDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun pendingPostDao(): PendingPostDao
    abstract fun pendingLikeDao(): PendingLikeDao
}