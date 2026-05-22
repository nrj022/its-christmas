package com.zcard.feature.setting

import androidx.annotation.StringRes

sealed class SettingSideEffect {
    data object NavigateBack: SettingSideEffect()
    data class ToastMessage(@StringRes val msgRes: Int): SettingSideEffect()
    data object OpenCreditsDocs: SettingSideEffect()
}
