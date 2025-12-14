package com.itschristmas.data.di

import com.itschristmas.data.usecaseImpl.GetTextElementsUseCaseImpl
import com.itschristmas.data.usecaseImpl.LoadCardEditorUseCaseImpl
import com.itschristmas.data.usecaseImpl.SaveTextElementsUseCaseImpl
import com.itschristmas.domain.usecase.GetTextElementsUseCase
import com.itschristmas.domain.usecase.LoadCardEditorUseCase
import com.itschristmas.domain.usecase.SaveTextElementsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindLoadCardEditorUseCase(
        loadCardEditorUseCaseImpl: LoadCardEditorUseCaseImpl
    ): LoadCardEditorUseCase

    @Binds
    abstract fun bindGetTextElementsUseCase(
        getTextElementsUseCaseImpl: GetTextElementsUseCaseImpl
    ): GetTextElementsUseCase

    @Binds
    abstract fun bindSaveTextElementsUseCase(
        saveTextElementsUseCaseImpl: SaveTextElementsUseCaseImpl
    ): SaveTextElementsUseCase
}