package com.zcard.data.di

import com.zcard.data.repositoryImpl.AssetRepositoryImpl
import com.zcard.data.repositoryImpl.CardElementRepositoryImpl
import com.zcard.data.repositoryImpl.CardRepositoryImpl
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.repository.CardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCardRepository(
        cardRepositoryImpl: CardRepositoryImpl
    ): CardRepository

    @Binds
    abstract fun bindAssetRepository(
        assetRepositoryImpl: AssetRepositoryImpl
    ): AssetRepository

    @Binds
    abstract fun bindCardElementRepository(
        cardElementRepositoryImpl: CardElementRepositoryImpl
    ): CardElementRepository
}