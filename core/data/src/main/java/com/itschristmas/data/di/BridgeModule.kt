package com.itschristmas.data.di

import com.itschristmas.data.bridgeimpl.UnityBridgeImpl
import com.itschristmas.domain.bridge.UnityBridge
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BridgeModule {

    @Binds
    abstract fun bindUnityBridge(
        unityBridgeImpl: UnityBridgeImpl
    ): UnityBridge
}