package com.zcard.feature.cardeditor

import androidx.annotation.StringRes

sealed class CardEditorSideEffect {
    data class ToastMessage(@StringRes val msgRes: Int): CardEditorSideEffect()
    data object Finish: CardEditorSideEffect()
    data class NavigateToCardShare(val cardUrl: String): CardEditorSideEffect()
    data class NavigateToTransform(val elementId: Long): CardEditorSideEffect()
    data object NavigateToTextEdit: CardEditorSideEffect()
    data class CopyCardLink(val cardUrl: String): CardEditorSideEffect()
}