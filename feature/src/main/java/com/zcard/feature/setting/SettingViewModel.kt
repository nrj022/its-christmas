package com.zcard.feature.setting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "SettingViewModel"

@HiltViewModel
class SettingViewModel @Inject constructor(
): ViewModel() {

    private val _settingState = MutableStateFlow(SettingState())
    val settingState: StateFlow<SettingState> = _settingState

    private val _settingSideEffect = Channel<SettingSideEffect>(Channel.BUFFERED)
    val settingSideEffect = _settingSideEffect.receiveAsFlow()

    fun onIntent(intent: SettingIntent) {
        when(intent) {
            is SettingIntent.NavigateBack -> handleNavigateBack()
            is SettingIntent.OpenCreditsDocs -> handleOpenCreditsDocs()
            is SettingIntent.ShowFeedbackDialog -> handleShowFeedbackDialog()
            is SettingIntent.OnFeedbackTextChanged -> handleOnFeedbackTextChanged(intent.text)
            is SettingIntent.SubmitFeedback -> handleSubmitFeedback(intent.text)
            is SettingIntent.CancelFeedback -> handleCancelFeedback()
        }
    }

    private fun handleNavigateBack() {
        _settingSideEffect.trySend(SettingSideEffect.NavigateBack)
    }

    private fun handleOpenCreditsDocs() {
        _settingSideEffect.trySend(SettingSideEffect.OpenCreditsDocs)
    }

    private fun handleShowFeedbackDialog() {
        _settingState.update { it.copy(showFeedbackDialog = true) }
    }

    private fun handleOnFeedbackTextChanged(text: String) {
        _settingState.update { it.copy(feedbackText = text) }
    }

    private fun handleSubmitFeedback(text: String) {
        // TODO: 피드백 업로드
        _settingState.update { it.copy(showFeedbackDialog = false) }
        _settingSideEffect.trySend(SettingSideEffect.ToastMessage("Feedback submitted"))
    }

    private fun handleCancelFeedback() {
        _settingState.update { it.copy(showFeedbackDialog = false) }
    }
}