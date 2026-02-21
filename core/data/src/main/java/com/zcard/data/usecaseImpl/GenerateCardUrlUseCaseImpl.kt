package com.zcard.data.usecaseImpl

import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import java.net.URLEncoder.encode
import javax.inject.Inject

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
        val glbToken = card.glbToken ?: return ""
        val bgFile = bg.firebaseFileName ?: return ""
        val bgToken = bg.firebaseToken ?: return ""

        return run {
            val encodedTitle = encode(card.title, "UTF-8")
            val encodedGlb = encode(glb, "UTF-8")
            val encodedGlbToken = encode(glbToken, "UTF-8")
            val encodedBg = encode(bgFile, "UTF-8")
            val encodedBgToken = encode(bgToken, "UTF-8")

            "$baseCardUrl?title=$encodedTitle&glb=$encodedGlb&glbToken=$encodedGlbToken&bg=$encodedBg&bgToken=$encodedBgToken"
        }
    }
}
