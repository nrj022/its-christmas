package com.itschristmas.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itschristmas.database.dao.AssetDao
import com.itschristmas.database.dao.CardDao
import com.itschristmas.database.dao.CardElementDao
import com.itschristmas.database.entity.AssetEntity
import com.itschristmas.database.entity.CardElementEntity
import com.itschristmas.database.entity.CardEntity

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