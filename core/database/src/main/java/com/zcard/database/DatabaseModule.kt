package com.zcard.database

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.zcard.database.dao.AssetDao
import com.zcard.database.dao.CardDao
import com.zcard.database.dao.CardElementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val ZCARD_PREFS = "zcard_prefs"
    private const val DB_NAME = "app_database.db"

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(ZCARD_PREFS, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        assetDaoProvider: Provider<AssetDao>,
        prefs: SharedPreferences
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        )
        .enableMultiInstanceInvalidation()
        .addCallback(AppDatabaseCallback(assetDaoProvider, prefs))
        .build()
    }

    @Provides
    fun provideCardDao(appDatabase: AppDatabase): CardDao = appDatabase.cardDao()

    @Provides
    fun provideAssetDao(appDatabase: AppDatabase): AssetDao = appDatabase.assetDao()

    @Provides
    fun provideCardElementDao(appDatabase: AppDatabase): CardElementDao = appDatabase.cardElementDao()
}