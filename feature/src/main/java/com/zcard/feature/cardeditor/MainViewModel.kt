package com.zcard.feature.cardeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.model.UnityMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {

    private val _mainSideEffect = MutableSharedFlow<MainSideEffect>()
    val mainSideEffect: SharedFlow<MainSideEffect> = _mainSideEffect

    fun emitSideEffect(sideEffect: MainSideEffect) {
        viewModelScope.launch {
            _mainSideEffect.emit(sideEffect)
        }
    }

    fun emitUnityMessage(message: UnityMessage) {
        viewModelScope.launch {
            _mainSideEffect.emit(MainSideEffect.ReceivedUnityMessage(message))
        }
    }

    fun emitUnityContainerHeightFraction(fraction: Float) {
        viewModelScope.launch {
            _mainSideEffect.emit(MainSideEffect.UnityContainerHeightFraction(fraction))
        }
    }
}