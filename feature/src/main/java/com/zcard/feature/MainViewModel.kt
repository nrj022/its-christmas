package com.zcard.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.model.UnityEventType
import com.zcard.domain.model.UnityMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UnityLifecycle {
    data object Paused: UnityLifecycle()
    data object Running: UnityLifecycle()
    data class Resetting(val id: Int): UnityLifecycle()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val unityBridge: UnityBridge
) : ViewModel() {

    private var unityLifecycle: UnityLifecycle = UnityLifecycle.Paused
    private var resetIdCounter = 0

    private val _mainSideEffect = MutableSharedFlow<MainSideEffect>()
    val mainSideEffect: SharedFlow<MainSideEffect> = _mainSideEffect

    private fun emitSideEffect(sideEffect: MainSideEffect) {
        viewModelScope.launch {
            _mainSideEffect.emit(sideEffect)
        }
    }

    fun emitUnityMessage(message: UnityMessage) {
        if(message.type == UnityEventType.SCENE_RESET) {
            val idCounter = message.data.toIntOrNull() ?: -1
            onUnityResetComplete(idCounter)
        } else {
            emitSideEffect(MainSideEffect.ReceivedUnityMessage(message))
        }
    }

    fun emitUnityContainerHeightFraction(fraction: Float) {
        emitSideEffect(MainSideEffect.UnityContainerHeightFraction(fraction))
    }

    fun onEditorCreated() {
        unityLifecycle = UnityLifecycle.Running
        emitSideEffect(MainSideEffect.ResumeUnity)
    }

    fun onEditorDestroyed(cardId: Long) {
        val id = ++resetIdCounter
        unityLifecycle = UnityLifecycle.Resetting(id)
        unityBridge.resetScene(cardId, id)
    }

    fun onUnityResetComplete(id: Int) {
        val state = unityLifecycle
        if(state is UnityLifecycle.Resetting && state.id == id) {
            unityLifecycle = UnityLifecycle.Paused
            emitSideEffect(MainSideEffect.PauseUnity)
        }
    }
}