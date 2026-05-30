package com.zcard.feature.cardshare

sealed class CardShareIntent {
    data object NavigateBack: CardShareIntent()
    data object NavigateToHome: CardShareIntent()
    data class ShareCardLink(val cardUrl: String): CardShareIntent()
    data object CheckNetwork: CardShareIntent()
    data object StopLoading: CardShareIntent()
}