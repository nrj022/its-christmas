package com.zcard.feature.setting

data class SettingState(
    val showFeedbackDialog: Boolean = false,
    val feedbackText: String = "",
) {
    val isSubmitEnabled: Boolean
        get() = feedbackText.isNotBlank()
}