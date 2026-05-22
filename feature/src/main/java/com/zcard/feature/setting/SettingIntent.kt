package com.zcard.feature.setting

sealed class SettingIntent {
    data object NavigateBack: SettingIntent()
    data object OpenCreditsDocs: SettingIntent()
    data object ShowFeedbackDialog : SettingIntent()
    data class OnFeedbackTextChanged(val text: String) : SettingIntent()
    data class SubmitFeedback(val text: String) : SettingIntent()
    data object CancelFeedback : SettingIntent()
}