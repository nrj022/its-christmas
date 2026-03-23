package com.zcard.data.usecaseImpl

import android.util.Log
import com.zcard.domain.enum.ElementType
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.TextElement
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.usecase.SaveTextElementsParams
import com.zcard.domain.usecase.SaveTextElementsResult
import com.zcard.domain.usecase.SaveTextElementsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SaveTextElementsUseCaseImpl"

class SaveTextElementsUseCaseImpl @Inject constructor(
    private val cardElementRepository: CardElementRepository,
    private val mutex: Mutex
): SaveTextElementsUseCase {
    override suspend fun invoke(params: SaveTextElementsParams): Result<SaveTextElementsResult> {
        return runCatching { mutex.withLock {
            withContext(Dispatchers.IO) {
                val (nonEmptyTextList, emptyTextList) = params.updates
                    .partition { it.attributes.content.isNotBlank() }

                val updated = mutableListOf<TextElement>()
                val failedUpdates = mutableListOf<TextElement>()
                val failedDeleteIds = mutableSetOf<Long>()

                nonEmptyTextList.forEach { text ->
                    text.elementId?.let { id ->
                        cardElementRepository.updateTextElement(
                            cardId = params.cardId,
                            elementId = id,
                            textAttributes = text.attributes,
                            posX = text.posX,
                            posY = text.posY,
                            posZ = text.posZ
                        ).onSuccess {
                            updated += text
                        }.onFailure {
                            failedUpdates += text
                            Log.e(TAG, "updateTextElement failed: $it")
                        }
                    } ?: run {
                        cardElementRepository.insertCardElement(
                            CardElement(
                                elementId = 0,
                                cardId = params.cardId,
                                elementType = ElementType.TEXT,
                                posX = text.posX,
                                posY = text.posY,
                                posZ = text.posZ,
                                textAttributes = text.attributes
                            )
                        ).onSuccess { id ->
                            updated += text.copy(elementId = id)
                        }.onFailure {
                            failedUpdates += text
                            Log.e(TAG, "updateTextElement failed: $it")
                        }
                    }
                }

                val toDelete = params.deletedIds.toMutableSet().apply {
                    addAll(emptyTextList.mapNotNull { it.elementId })
                }.toList()

                cardElementRepository.deleteCardElementsByIds(params.cardId, toDelete)
                    .onFailure {
                        failedDeleteIds.addAll(toDelete)
                        Log.e(TAG, "deleteCardElementsByIds failed: $it")
                    }

                SaveTextElementsResult(
                    updatedElements = updated,
                    failedUpdates = failedUpdates,
                    deletedIds = failedDeleteIds
                )
            }}
        }
    }
}