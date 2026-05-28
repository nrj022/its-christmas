package com.zcard.feature.cardeditor.textedit

data class TextEditState(
    val tempTexts: List<TempText> = emptyList(),
    val savedTexts: List<TempText> = emptyList(),
    val showUnsavedChangesDialog: Boolean = false,
    val unityContainerHeightFraction: Float = 0.6f,
    val selectedTextTempId: Long? = null,
) {
    val hasPendingText: Boolean
        get() = savedTexts != tempTexts
    val selectedText: TempText?
        get() = selectedTextTempId?.let { id -> tempTexts.find { it.tempId == id } }
}