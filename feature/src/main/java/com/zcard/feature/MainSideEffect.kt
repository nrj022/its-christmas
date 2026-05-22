package com.zcard.feature

sealed class MainSideEffect {
    data object ResumeUnity : MainSideEffect()
    data object PauseUnity : MainSideEffect()
    data class UnityContainerHeightFraction(val fraction: Float) : MainSideEffect()
    data object NavigateToHome : MainSideEffect()
}