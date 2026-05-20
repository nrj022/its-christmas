package com.zcard.feature.home

import androidx.annotation.StringRes

sealed class HomeSideEffect {
    data object NavigateToSetting: HomeSideEffect()
    data class NavigateToCardEditor(val cardId: Long): HomeSideEffect()
    data class ToastMessage(@StringRes val msgRes: Int): HomeSideEffect()
    data class ShareLink(val link: String): HomeSideEffect()
}
