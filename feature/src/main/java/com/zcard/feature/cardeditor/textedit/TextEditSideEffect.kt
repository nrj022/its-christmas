package com.zcard.feature.cardeditor.textedit

import androidx.annotation.StringRes

sealed class TextEditSideEffect {
    data class ToastMessage(@StringRes val msgRes: Int): TextEditSideEffect()
    data object Finish: TextEditSideEffect()
}