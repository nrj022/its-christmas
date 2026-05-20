package com.zcard.feature.setting

sealed class SettingSideEffect {
    data object NavigateBack: SettingSideEffect()
    data class ToastMessage(val message: String): SettingSideEffect()
    data object OpenCreditsDocs: SettingSideEffect()
}
