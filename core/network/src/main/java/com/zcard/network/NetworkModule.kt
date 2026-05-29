package com.zcard.network

import com.zcard.domain.network.NetworkManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun provideNetworkManager(
        networkManagerImpl: NetworkManagerImpl
    ): NetworkManager
}