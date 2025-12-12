package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.model.DialogState
import com.itschristmas.card.cardeditor.model.PanelType
import com.itschristmas.card.cardeditor.model.TempTextElement
import com.itschristmas.card.cardeditor.model.TempTransform
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.Asset

data class CardEditorState(
    val panelType: PanelType = PanelType.ASSET_BROWSER,
    val tempTransform: TempTransform? = null,
    val tempTextList: List<TempTextElement> = emptyList(),
    val dialogState: DialogState = DialogState.NONE,
    val cardTitle: String = "New Card",
    val objects: List<Asset> = emptyList(),
    val backgrounds: List<Asset> = emptyList(),
    val spawnedObjects: List<CardElementWithAssetKeys> = emptyList(),
    val selectedBackgroundId: Long? = null,
    val selectedSpawnedObject: CardElementWithAssetKeys? = null,
    val selectedTextTempId: Long? = null,
) {
    val isAssetBrowserPanelActive: Boolean
        get() = panelType == PanelType.ASSET_BROWSER

    val isTransformPanelActive: Boolean
        get() = panelType == PanelType.TRANSFORM_CONTROL

    val showObjectOptionContainer: Boolean
        get() = selectedSpawnedObject != null && panelType == PanelType.ASSET_BROWSER

    val showTextOptionContainer: Boolean
        get() = panelType == PanelType.TEXT_EDITOR

    val unityContainerHeightFraction: Float
        get() = if (panelType == PanelType.TRANSFORM_CONTROL || panelType == PanelType.TEXT_EDITOR) 0.6f else 0.5f

    val selectedSpawnedObjectId: Long?
        get() = selectedSpawnedObject?.cardElement?.elementId

    val hasPendingTransform: Boolean
        get() = tempTransform?.let { temp ->
            selectedSpawnedObject?.let { selected ->
                temp.posX != selected.cardElement.posX ||
                temp.posY != selected.cardElement.posY ||
                temp.scale != selected.cardElement.scale
            } ?: false
        } ?: false

    val selectedText: TempTextElement?
        get() = selectedTextTempId?.let { id -> tempTextList.find { it.tempId == id } }
}