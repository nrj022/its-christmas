package com.zcard.feature.home

import com.zcard.domain.model.UnityMessage

sealed class HomeIntent {
    data class OnUnityMessage(val message: UnityMessage) : HomeIntent()
    data object CreateCardAndNavigate : HomeIntent()
}