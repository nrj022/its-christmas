package com.itschristmas.data.usecaseImpl

import android.util.Log
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.TextElement
import com.itschristmas.domain.repository.CardElementRepository
import com.itschristmas.domain.usecase.UpdateTextParams
import com.itschristmas.domain.usecase.UpdateTextResult
import com.itschristmas.domain.usecase.UpdateTextUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "UpdateTextUseCaseImpl"

class UpdateTextUseCaseImpl @Inject constructor(
    private val cardElementRepository: CardElementRepository,
    private val mutex: Mutex
): UpdateTextUseCase {
    override suspend fun invoke(params: UpdateTextParams): Result<UpdateTextResult> {
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
                            elementId = id,
                            textAttributes = text.attributes,
                            posX = text.posX,
                            posY = text.posY
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

                cardElementRepository.deleteCardElementsByIds(toDelete)
                    .onFailure {
                        failedDeleteIds.addAll(toDelete)
                        Log.e(TAG, "deleteCardElementsByIds failed: $it")
                    }

                UpdateTextResult(
                    updatedElements = updated,
                    failedUpdates = failedUpdates,
                    deletedIds = failedDeleteIds
                )
            }}
        }
    }
}