package com.zcard.feature.home

import com.zcard.domain.bridge.UnityMessage

sealed class HomeIntent {
    data class OnUnityMessage(val message: UnityMessage) : HomeIntent()
    data object CreateCard : HomeIntent()
    data object NavigateToSetting : HomeIntent()
    data class NavigateToCardEditor(val cardId: Long) : HomeIntent()
    data class OpenBottomSheet(val cardId: Long) : HomeIntent()
    data object CloseBottomSheet : HomeIntent()
    data class ShareLink(val cardId: Long) : HomeIntent()
    data object ConfirmDelete : HomeIntent()
    data object CancelDelete : HomeIntent()
    data class DeleteCard(val cardId: Long) : HomeIntent()
}