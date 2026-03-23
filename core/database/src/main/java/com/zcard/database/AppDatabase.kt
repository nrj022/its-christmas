package com.zcard.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
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
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = AppDatabase.GlbTokenDeleteSpec::class)
    ]
)

abstract class AppDatabase: RoomDatabase() {

    @DeleteColumn(tableName = "cards", columnName = "glbToken")
    class GlbTokenDeleteSpec : AutoMigrationSpec

    abstract fun cardDao(): CardDao
    abstract fun cardElementDao(): CardElementDao
    abstract fun assetDao(): AssetDao
}