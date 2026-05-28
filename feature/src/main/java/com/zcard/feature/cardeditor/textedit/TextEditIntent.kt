package com.zcard.feature.cardeditor.textedit

import com.zcard.feature.cardeditor.model.Direction
import com.zcard.domain.model.TextColor
import com.zcard.domain.model.TextFontFamily
import com.zcard.domain.model.TextAlignment

sealed class TextEditIntent {
    data class Init(val cardId: Long): TextEditIntent()
    data class ImeVisible(val visible: Boolean): TextEditIntent()
    data class ChangeTab(val tab: TextEditState.TextEditorTab): TextEditIntent()
    data object ResetCamera: TextEditIntent()
    data object MissingTextSelection: TextEditIntent()
    data object AddText: TextEditIntent()
    data class DeleteText(val tempId: Long): TextEditIntent()
    data class SelectText(val tempId: Long): TextEditIntent()
    data class ChangeTextContent(val newText: String): TextEditIntent()
    data class SelectAlignment(val newAlignment: TextAlignment): TextEditIntent()
    data class SelectColor(val newColor: TextColor): TextEditIntent()
    data class ChangeFontSize(val newSize: Float): TextEditIntent()
    data class SelectFont(val newFont: TextFontFamily): TextEditIntent()
    data class MoveText(val direction: Direction): TextEditIntent()
    data object Exit: TextEditIntent()
    data object DismissDialog: TextEditIntent()
    data object SaveChanges: TextEditIntent()
    data object SaveAndExit: TextEditIntent()
    data object DiscardAndExit: TextEditIntent()
}