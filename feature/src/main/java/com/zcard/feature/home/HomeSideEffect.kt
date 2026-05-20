package com.zcard.feature.home

sealed class HomeSideEffect {
    data object NavigateToSetting: HomeSideEffect()
    data class NavigateToCardEditor(val cardId: Long): HomeSideEffect()
    data class ToastMessage(val message: String): HomeSideEffect()
    data class ShareLink(val link: String): HomeSideEffect()
}
