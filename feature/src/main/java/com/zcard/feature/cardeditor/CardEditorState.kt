package com.zcard.feature.cardeditor

import com.zcard.feature.cardeditor.model.DialogState
import com.zcard.feature.cardeditor.model.PanelType
import com.zcard.feature.cardeditor.model.TempTextElement
import com.zcard.feature.cardeditor.model.TempTransform
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.Asset
import com.zcard.domain.model.TextElement

data class CardEditorState(
    val panelType: PanelType = PanelType.ASSET_BROWSER,
    val dialogState: DialogState = DialogState.NONE,
    val isLoading: Boolean = false,
    val isTransformCameraFocus: Boolean = true,
    val tempTransform: TempTransform? = null,
    val tempTextList: List<TempTextElement> = emptyList(),
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

    val isTitleChanged: Boolean
        get() = originalCardTitle != cardTitle

    val showObjectOptionContainer: Boolean
        get() = selectedSpawnedObject != null && panelType == PanelType.ASSET_BROWSER

    val showTextOptionContainer: Boolean
        get() = panelType == PanelType.TEXT_EDITOR

    val unityContainerHeightFraction: Float
        get() = if (panelType == PanelType.TEXT_EDITOR) 0.6f else 0.52f

    val selectedSpawnedObjectId: Long?
        get() = selectedSpawnedObject?.cardElement?.elementId

    val selectedText: TempTextElement?
        get() = selectedTextTempId?.let { id -> tempTextList.find { it.tempId == id } }
}