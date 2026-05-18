package com.zcard.feature.home

import com.zcard.domain.model.CardPreview

data class HomeState(
    val cardPreviews: List<CardPreview> = emptyList(),
    val selectedCardId: Long? = null,
    val deleteTargetCard: CardPreview? = null,
) {
    val selectedCard: CardPreview? = cardPreviews.find { it.cardId == selectedCardId }
}