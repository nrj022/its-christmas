package com.zcard.card.cardeditor

import com.zcard.card.cardeditor.model.DialogState
import com.zcard.card.cardeditor.model.PanelType
import com.zcard.card.cardeditor.model.TempTextElement
import com.zcard.card.cardeditor.model.TempTransform
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.Asset
import com.zcard.domain.model.TextElement

data class CardEditorState(
    val panelType: PanelType = PanelType.ASSET_BROWSER,
    val isLoading: Boolean = false,
    val isTransformCameraFocus: Boolean = true,
    val tempTransform: TempTransform? = null,
    val tempTextList: List<TempTextElement> = emptyList(),
    val dialogState: DialogState = DialogState.NONE,
    val originalCardTitle: String = "New Card",
    val cardTitle: String = "New Card",
    val cardUrl: String = "",
    val loadingText: String = "Loading",
    val objects: List<Asset> = emptyList(),
    val backgrounds: List<Asset> = emptyList(),
    val spawnedObjects: List<CardElementWithAssetKeys> = emptyList(),
    val texts: List<TextElement> = emptyList(),
    val loadingObjectIds: Set<Long> = emptySet(),
    val selectedBackgroundId: Long = 1,
    val selectedSpawnedObject: CardElementWithAssetKeys? = null,
    val selectedTextTempId: Long? = null,
) {
    val isAssetBrowserPanelActive: Boolean
        get() = panelType == PanelType.ASSET_BROWSER

    val showLinkDetailButton: Boolean
        get() = panelType == PanelType.ASSET_BROWSER && cardUrl.isNotBlank()

    val isTransformPanelActive: Boolean
        get() = panelType == PanelType.TRANSFORM_CONTROL

    val isTitleChanged: Boolean
        get() = originalCardTitle != cardTitle

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
                temp.posZ != selected.cardElement.posZ ||
                temp.scale != selected.cardElement.scale
            } ?: false
        } ?: false

    val selectedText: TempTextElement?
        get() = selectedTextTempId?.let { id -> tempTextList.find { it.tempId == id } }
}