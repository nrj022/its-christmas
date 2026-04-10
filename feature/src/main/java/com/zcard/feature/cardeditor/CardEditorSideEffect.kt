package com.zcard.feature.cardeditor

sealed class CardEditorSideEffect {
    data class ShowToast(val message: String): CardEditorSideEffect()
    data object Finish: CardEditorSideEffect()
    data class NavigateToCardShare(val cardUrl: String): CardEditorSideEffect()
    data class CopyCardLink(val cardUrl: String): CardEditorSideEffect()
}