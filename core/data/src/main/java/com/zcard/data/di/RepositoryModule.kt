package com.zcard.data.di

import com.zcard.data.repositoryImpl.AssetRepositoryImpl
import com.zcard.data.repositoryImpl.AuthRepositoryImpl
import com.zcard.data.repositoryImpl.CardElementRepositoryImpl
import com.zcard.data.repositoryImpl.CardRepositoryImpl
import com.zcard.data.repositoryImpl.FeedbackRepositoryImpl
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.AuthRepository
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.repository.FeedbackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: CardRepositoryImpl
    ): CardRepository

    @Binds
    @Singleton
    abstract fun bindAssetRepository(
        assetRepositoryImpl: AssetRepositoryImpl
    ): AssetRepository

    @Binds
    @Singleton
    abstract fun bindCardElementRepository(
        cardElementRepositoryImpl: CardElementRepositoryImpl
    ): CardElementRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(
        feedbackRepositoryImpl: FeedbackRepositoryImpl
    ): FeedbackRepository
}