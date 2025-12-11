package com.itschristmas.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.sync.Mutex
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConcurrencyModule {

    @Provides
    @Singleton
    fun provideMutex(): Mutex = Mutex()
}