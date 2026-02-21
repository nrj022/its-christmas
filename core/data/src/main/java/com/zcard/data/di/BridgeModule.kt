package com.zcard.data.di

import com.zcard.data.bridgeimpl.UnityBridgeImpl
import com.zcard.domain.bridge.UnityBridge
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