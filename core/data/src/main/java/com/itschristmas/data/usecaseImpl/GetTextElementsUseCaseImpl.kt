package com.itschristmas.data.usecaseImpl

import android.util.Log
import com.itschristmas.domain.model.TextElement
import com.itschristmas.domain.repository.CardElementRepository
import com.itschristmas.domain.usecase.GetTextElementsUseCase
import javax.inject.Inject

private const val TAG = "GetTextElementsUseCaseImpl"

class GetTextElementsUseCaseImpl @Inject constructor(
    private val cardElementRepository: CardElementRepository,
): GetTextElementsUseCase {

    override suspend fun invoke(cardId: Long): Result<List<TextElement>> {
        return runCatching {
            cardElementRepository.getTextElementsByCardId(cardId).getOrThrow()
                .mapNotNull { element ->
                    element.textAttributes?.let { attr ->
                        TextElement(
                            elementId = element.elementId,
                            attributes = attr,
                            posX = element.posX,
                            posY = element.posY,
                            posZ = element.posZ
                        )
                    }
                }
        }.onFailure {
            Log.e(TAG, "invoke failed: $it")
        }
    }
}