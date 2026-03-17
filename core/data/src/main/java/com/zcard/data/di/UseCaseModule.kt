package com.zcard.data.di

import com.zcard.data.usecaseImpl.GenerateCardUrlUseCaseImpl
import com.zcard.data.usecaseImpl.GetTextElementsUseCaseImpl
import com.zcard.data.usecaseImpl.InitCardEditorUseCaseImpl
import com.zcard.data.usecaseImpl.LoadCardEditorUseCaseImpl
import com.zcard.data.usecaseImpl.SaveTextElementsUseCaseImpl
import com.zcard.data.usecaseImpl.UploadCardModelUseCaseImpl
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import com.zcard.domain.usecase.GetTextElementsUseCase
import com.zcard.domain.usecase.InitCardEditorUseCase
import com.zcard.domain.usecase.LoadCardEditorUseCase
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
    abstract fun bindLoadCardEditorUseCase(
        loadCardEditorUseCaseImpl: LoadCardEditorUseCaseImpl
    ): LoadCardEditorUseCase

    @Binds
    abstract fun bindInitCardEditorUseCase(
        initCardEditorUseCaseImpl: InitCardEditorUseCaseImpl
    ): InitCardEditorUseCase

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