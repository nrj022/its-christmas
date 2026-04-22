package com.zcard.feature

import com.zcard.domain.model.UnityMessage

sealed class MainIntent {
    data class OnUnityMessage(val message: UnityMessage) : MainIntent()
    data object OnEditorCreated : MainIntent()
    data class OnEditorDestroyed(val cardId: Long) : MainIntent()
    data class OnUnityContainerHeightChanged(val fraction: Float) : MainIntent()
}