package com.example.database

import android.content.Context
import androidx.room.Room
import com.example.database.dao.AssetDao
import com.example.database.dao.CardDao
import com.example.database.dao.CardElementDao
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

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        assetDaoProvider: Provider<AssetDao>,
        cardDaoProvider: Provider<CardDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        )
        .fallbackToDestructiveMigration()   // DB 스키마 변경 시 기존 데이터 삭제 후 다시 생성 - TODO 마이그레이션 추가
        .addCallback(AppDatabaseCallback(assetDaoProvider, cardDaoProvider))    // TODO: 초기 데이터 설정 방식 sqLite로 변경
        .build()
    }

    @Provides
    fun provideCardDao(appDatabase: AppDatabase): CardDao = appDatabase.cardDao()

    @Provides
    fun provideAssetDao(appDatabase: AppDatabase): AssetDao = appDatabase.assetDao()

    @Provides
    fun provideCardElementDao(appDatabase: AppDatabase): CardElementDao = appDatabase.cardElementDao()
}