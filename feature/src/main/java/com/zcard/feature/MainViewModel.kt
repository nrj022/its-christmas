package com.zcard.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.bridge.UnityEventType
import com.zcard.domain.bridge.UnityMessage
import com.zcard.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UnityLifecycle {
    data object Paused : UnityLifecycle()
    data object Running : UnityLifecycle()
    data class Resetting(val id: Int) : UnityLifecycle()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val unityBridge: UnityBridge,
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private var unityLifecycle: UnityLifecycle = UnityLifecycle.Paused
    private var resetIdCounter = 0

    val isTutorialCompleted: Boolean
        get() = onboardingRepository.isTutorialCompleted()

    private val _mainSideEffect = MutableSharedFlow<MainSideEffect>()
    val mainSideEffect: SharedFlow<MainSideEffect> = _mainSideEffect

    private val _unityMessage = MutableSharedFlow<UnityMessage>()
    val unityMessage: SharedFlow<UnityMessage> = _unityMessage

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.OnUnityMessage -> handleUnityMessage(intent.message)
            is MainIntent.OnEditorCreated -> handleEditorCreated()
            is MainIntent.OnEditorDestroyed -> handleEditorDestroyed(intent.cardId)
            is MainIntent.OnUnityContainerHeightChanged -> handleUnityContainerHeightFraction(intent.fraction)
            is MainIntent.CompleteTutorial -> handleCompleteTutorial()
        }
    }

    private fun handleCompleteTutorial() {
        onboardingRepository.setTutorialCompleted()
        emitSideEffect(MainSideEffect.NavigateToHome)
    }

    private fun handleUnityMessage(message: UnityMessage) {
        if (message.type == UnityEventType.SCENE_RESET) {
            val idCounter = message.data.toIntOrNull() ?: -1
            handleUnityResetComplete(idCounter)
        } else {
            viewModelScope.launch { _unityMessage.emit(message) }
        }
    }

    fun handleUnityContainerHeightFraction(fraction: Float) {
        emitSideEffect(MainSideEffect.UnityContainerHeightFraction(fraction))
    }

    private fun handleEditorCreated() {
        unityLifecycle = UnityLifecycle.Running
        emitSideEffect(MainSideEffect.ResumeUnity)
    }

    private fun handleEditorDestroyed(cardId: Long) {
        val id = ++resetIdCounter
        unityLifecycle = UnityLifecycle.Resetting(id)
        unityBridge.resetScene(cardId, id)
    }

    private fun handleUnityResetComplete(id: Int) {
        val state = unityLifecycle
        if (state is UnityLifecycle.Resetting && state.id == id) {
            unityLifecycle = UnityLifecycle.Paused
            emitSideEffect(MainSideEffect.PauseUnity)
        }
    }

    private fun emitSideEffect(sideEffect: MainSideEffect) {
        viewModelScope.launch { _mainSideEffect.emit(sideEffect) }
    }
}