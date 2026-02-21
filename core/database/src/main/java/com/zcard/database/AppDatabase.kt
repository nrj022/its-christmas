package com.zcard.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zcard.database.dao.AssetDao
import com.zcard.database.dao.CardDao
import com.zcard.database.dao.CardElementDao
import com.zcard.database.entity.AssetEntity
import com.zcard.database.entity.CardElementEntity
import com.zcard.database.entity.CardEntity

@Database(
    entities = [
        CardEntity::class,
        CardElementEntity::class,
        AssetEntity::class
    ],
    version = 1
)

abstract class AppDatabase: RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun cardElementDao(): CardElementDao
    abstract fun assetDao(): AssetDao
}