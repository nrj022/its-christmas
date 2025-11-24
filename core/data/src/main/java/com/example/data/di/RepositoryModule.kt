package com.example.data.di

import com.example.data.repositoryImpl.AssetRepositoryImpl
import com.example.data.repositoryImpl.CardElementRepositoryImpl
import com.example.data.repositoryImpl.CardRepositoryImpl
import com.example.domain.repository.AssetRepository
import com.example.domain.repository.CardElementRepository
import com.example.domain.repository.CardRepository
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