package com.zcard.feature.home

sealed class HomeSideEffect {
    data class NavigateToCardEditor(val cardId: Long): HomeSideEffect()
}