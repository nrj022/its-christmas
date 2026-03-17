package com.zcard.data.usecaseImpl

import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import javax.inject.Inject
import androidx.core.net.toUri

class GenerateCardUrlUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val assetRepository: AssetRepository
): GenerateCardUrlUseCase {
    private val baseCardUrl = "https://z-card-app.web.app/"

    override suspend fun invoke(
        cardId: Long,
    ): String {
        val card = cardRepository.getCardById(cardId).getOrNull() ?: return ""
        val bg = assetRepository.getAssetById(card.backgroundAssetId).getOrNull() ?: return ""

        val glb = card.glbFileName ?: return ""
        val bgFile = bg.unityKey

        val builder = baseCardUrl.toUri().buildUpon()
            .appendQueryParameter("title", card.title)
            .appendQueryParameter("glb", glb)
            .appendQueryParameter("bg", bgFile)

        return builder.build().toString()
    }
}
