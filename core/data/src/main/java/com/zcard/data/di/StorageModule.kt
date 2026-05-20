package com.zcard.data.di

import com.zcard.data.storageimpl.CardFileStorageImpl
import com.zcard.domain.storage.CardFileStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindCardFileStorage(
        cardFileStorageImpl: CardFileStorageImpl
    ): CardFileStorage
}