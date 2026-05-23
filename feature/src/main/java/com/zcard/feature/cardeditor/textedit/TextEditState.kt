package com.zcard.feature.cardeditor.textedit

import com.zcard.feature.cardeditor.model.TempTextElement
import com.zcard.domain.model.TextElement

data class TextEditState(
    val tempTextList: List<TempTextElement> = emptyList(),
    val texts: List<TextElement> = emptyList(),
    val selectedTextTempId: Long? = null,
    val showUnsavedChangesDialog: Boolean = false,
    val hasPendingText: Boolean = false
) {
    val selectedText: TempTextElement?
        get() = selectedTextTempId?.let { id -> tempTextList.find { it.tempId == id } }
}