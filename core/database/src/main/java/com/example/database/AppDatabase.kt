package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.AssetDao
import com.example.database.dao.CardDao
import com.example.database.dao.CardElementDao
import com.example.database.entity.AssetEntity
import com.example.database.entity.CardElementEntity
import com.example.database.entity.CardEntity

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