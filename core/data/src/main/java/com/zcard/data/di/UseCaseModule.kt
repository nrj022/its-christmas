package com.zcard.data.di

import com.zcard.data.usecaseImpl.GenerateCardUrlUseCaseImpl
import com.zcard.data.usecaseImpl.GetCardUseCaseImpl
import com.zcard.data.usecaseImpl.GetTextElementsUseCaseImpl
import com.zcard.data.usecaseImpl.OpenCardEditorUseCaseImpl
import com.zcard.data.usecaseImpl.SaveTextElementsUseCaseImpl
import com.zcard.data.usecaseImpl.UploadCardModelUseCaseImpl
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import com.zcard.domain.usecase.GetCardUseCase
import com.zcard.domain.usecase.GetTextElementsUseCase
import com.zcard.domain.usecase.OpenCardEditorUseCase
import com.zcard.domain.usecase.SaveTextElementsUseCase
import com.zcard.domain.usecase.UploadCardModelUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGenerateCardUrlUseCase(
        generateCardUrlUseCaseImpl: GenerateCardUrlUseCaseImpl
    ): GenerateCardUrlUseCase

    @Binds
    @Binds
    abstract fun bindGetCardUseCase(
        getCardUseCaseImpl: GetCardUseCaseImpl
    ): GetCardUseCase

    @Binds
    abstract fun bindGetTextElementsUseCase(
        getTextElementsUseCaseImpl: GetTextElementsUseCaseImpl
    ): GetTextElementsUseCase

    @Binds
    abstract fun bindSaveTextElementsUseCase(
        saveTextElementsUseCaseImpl: SaveTextElementsUseCaseImpl
    ): SaveTextElementsUseCase

    @Binds
    abstract fun bindUploadCardModelUseCase(
        uploadCardModelUseCaseImpl: UploadCardModelUseCaseImpl
    ): UploadCardModelUseCase
}