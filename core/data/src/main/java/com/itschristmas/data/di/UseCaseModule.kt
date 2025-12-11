package com.itschristmas.data.di

import com.itschristmas.data.usecaseImpl.UpdateTextUseCaseImpl
import com.itschristmas.domain.usecase.UpdateTextUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindUpdateTextUseCase(
        updateTextUseCaseImpl: UpdateTextUseCaseImpl
    ): UpdateTextUseCase
}