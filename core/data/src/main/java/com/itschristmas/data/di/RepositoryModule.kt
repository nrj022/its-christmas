package com.itschristmas.data.di

import com.itschristmas.data.repositoryImpl.AssetRepositoryImpl
import com.itschristmas.data.repositoryImpl.CardElementRepositoryImpl
import com.itschristmas.data.repositoryImpl.CardRepositoryImpl
import com.itschristmas.domain.repository.AssetRepository
import com.itschristmas.domain.repository.CardElementRepository
import com.itschristmas.domain.repository.CardRepository
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