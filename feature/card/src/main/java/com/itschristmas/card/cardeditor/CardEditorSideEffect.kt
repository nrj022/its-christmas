package com.itschristmas.card.cardeditor

sealed class CardEditorSideEffect {
    data class NavigateToCardShare(val cardUrl: String): CardEditorSideEffect()
}