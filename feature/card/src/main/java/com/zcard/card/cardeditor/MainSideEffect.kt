package com.zcard.card.cardeditor

import com.zcard.domain.model.UnityMessage

sealed class MainSideEffect {
    data object ResumeUnity : MainSideEffect()
    data object PauseUnity : MainSideEffect()
    data class UnityContainerHeightFraction(val fraction: Float) : MainSideEffect()
    data class ReceivedUnityMessage(val message: UnityMessage) : MainSideEffect()
}