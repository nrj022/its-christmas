package com.zcard.feature.cardeditor

import com.zcard.feature.cardeditor.model.DialogState
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.Asset

data class CardEditorState(
    val dialogState: DialogState = DialogState.NONE,
    val isLoading: Boolean = false,
    val originalCardTitle: String = "New Card",
    val cardTitle: String = "New Card",
    val cardUrl: String = "",
    val loadingText: String = "Loading",
    val objects: List<Asset> = emptyList(),
    val backgrounds: List<Asset> = emptyList(),
    val spawnedObjects: List<CardElementWithAssetKeys> = emptyList(),
    val loadingObjectIds: Set<Long> = emptySet(),
    val selectedBackgroundId: Long = 1,
    val selectedSpawnedObject: CardElementWithAssetKeys? = null,
) {

    val showLinkDetailButton: Boolean
        get() = cardUrl.isNotBlank()

    val isTitleChanged: Boolean
        get() = originalCardTitle != cardTitle

    val showObjectOptionContainer: Boolean
        get() = selectedSpawnedObject != null

    val selectedSpawnedObjectId: Long?
        get() = selectedSpawnedObject?.cardElement?.elementId
}