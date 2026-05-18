package com.zcard.data.usecaseImpl

import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import javax.inject.Inject
import androidx.core.net.toUri
import com.zcard.domain.exception.CardNotExportedException

class GenerateCardUrlUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val assetRepository: AssetRepository
): GenerateCardUrlUseCase {
    private val baseCardUrl = "https://z-card-app.web.app/"

    override suspend fun invoke(
        cardId: Long,
    ): Result<String> {
        val card = cardRepository.getCardById(cardId).getOrElse { return Result.failure(it) }
        val bg = assetRepository.getAssetById(card.backgroundAssetId).getOrElse { return Result.failure(it) }

        val glb = card.glbFileName ?: return Result.failure(CardNotExportedException(cardId))
        val bgFile = bg.unityKey

        val builder = baseCardUrl.toUri().buildUpon()
            .appendQueryParameter("title", card.title)
            .appendQueryParameter("glb", glb)
            .appendQueryParameter("bg", bgFile)

        return Result.success(builder.build().toString())
    }
}
