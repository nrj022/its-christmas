package com.itschristmas.card.cardeditor

sealed class CardEditorSideEffect {
    data class ShowToast(val message: String): CardEditorSideEffect()
    data class NavigateToCardShare(val cardUrl: String): CardEditorSideEffect()
}