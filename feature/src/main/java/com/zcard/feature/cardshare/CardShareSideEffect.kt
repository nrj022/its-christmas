package com.zcard.feature.cardshare

sealed class CardShareSideEffect {
    data object NavigateBack: CardShareSideEffect()
    data object NavigateToHome: CardShareSideEffect()
    data class ShareCardLink(val cardUrl: String): CardShareSideEffect()
}
