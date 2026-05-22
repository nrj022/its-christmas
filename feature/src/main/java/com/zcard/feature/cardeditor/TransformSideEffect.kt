package com.zcard.feature.cardeditor

import androidx.annotation.StringRes

sealed class TransformSideEffect {
    data class ToastMessage(@StringRes val msgRes: Int): TransformSideEffect()
    data object Finish: TransformSideEffect()
}