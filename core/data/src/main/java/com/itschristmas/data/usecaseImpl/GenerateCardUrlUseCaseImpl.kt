package com.itschristmas.data.usecaseImpl

import com.itschristmas.domain.repository.AssetRepository
import com.itschristmas.domain.repository.CardRepository
import com.itschristmas.domain.usecase.GenerateCardUrlUseCase
import java.net.URLEncoder.encode
import javax.inject.Inject

class GenerateCardUrlUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val assetRepository: AssetRepository
): GenerateCardUrlUseCase {
    private val baseCardUrl = "https://its-christmas-1f0ea.web.app"

    override suspend fun invoke(
        cardId: Long,
    ): String {
        val card = cardRepository.getCardById(cardId).getOrNull() ?: return ""
        val bg = assetRepository.getAssetById(card.backgroundAssetId).getOrNull() ?: return ""

        val encodedTitle = encode(card.title, "UTF-8")
        val encodedGlb = encode(card.glbFileName, "UTF-8")
        val encodedGlbToken = encode(card.glbToken, "UTF-8")
        val encodedBg = encode(bg.firebaseFileName, "UTF-8")
        val encodedBgToken = encode(bg.firebaseToken, "UTF-8")

        val url = "$baseCardUrl?title=${encodedTitle}&glb=${encodedGlb}&glbToken=${encodedGlbToken}&bg=${encodedBg}&bgToken=${encodedBgToken}"
        return url
    }
}
