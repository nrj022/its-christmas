package com.zcard.feature.cardeditor.textedit

import com.zcard.feature.R

data class TextEditState(
    val tempTexts: List<TempText> = emptyList(),
    val savedTexts: List<TempText> = emptyList(),
    val selectedTab: TextEditorTab = TextEditorTab.STYLE,
    val showUnsavedChangesDialog: Boolean = false,
    val unityContainerHeightFraction: Float = 0.6f,
    val selectedTextTempId: Long? = null,
) {
    val hasPendingText: Boolean
        get() = savedTexts != tempTexts
    val selectedText: TempText?
        get() = selectedTextTempId?.let { id -> tempTexts.find { it.tempId == id } }

    enum class TextEditorTab(val resId: Int) { STYLE(R.string.text_edit_title_tab_style), FONT(R.string.text_edit_title_tab_font), POSITION(R.string.text_edit_title_tab_position) }
}