package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.model.EditorDialogState
import com.itschristmas.card.cardeditor.model.PanelState
import com.itschristmas.card.cardeditor.model.TempTextElementState
import com.itschristmas.card.cardeditor.model.TempTransformState
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.Asset

data class CardEditorState(
    val panelState: PanelState = PanelState.ASSET_BROWSER,
    val tempTransformState: TempTransformState? = null,
    val tempTextList: List<TempTextElementState> = emptyList(),
    val editorDialogState: EditorDialogState = EditorDialogState.NONE,
    val cardTitle: String = "New Card",
    val objects: List<Asset> = emptyList(),
    val backgrounds: List<Asset> = emptyList(),
    val myObjects: List<CardElementWithAssetKeys> = emptyList(),
    val selectedBackground: Long? = null,
    val selectedMyObject: CardElementWithAssetKeys? = null,
    val selectedTextTempId: Long? = null,
) {
    val isAssetBrowserPanelActive: Boolean
        get() = panelState == PanelState.ASSET_BROWSER

    val isTransformPanelActive: Boolean
        get() = panelState == PanelState.TRANSFORM_CONTROL

    val showObjectOptionContainer: Boolean
        get() = selectedMyObject != null && panelState == PanelState.ASSET_BROWSER

    val unityContainerHeightFraction: Float
        get() = if (panelState == PanelState.TRANSFORM_CONTROL || panelState == PanelState.TEXT_EDITOR) 0.6f else 0.5f

    val selectedMyObjectIdx: Long?
        get() = selectedMyObject?.cardElement?.elementId

    val hasPendingTransform: Boolean
        get() = tempTransformState?.let { temp ->
            selectedMyObject?.let { selected ->
                temp.posX != selected.cardElement.posX ||
                temp.posY != selected.cardElement.posY ||
                temp.scale != selected.cardElement.scale
            } ?: false
        } ?: false

    val selectedText: TempTextElementState?
        get() = selectedTextTempId?.let { id -> tempTextList.find { it.tempId == id } }
}