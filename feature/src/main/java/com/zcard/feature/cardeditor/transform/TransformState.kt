package com.zcard.feature.cardeditor.transform

import com.zcard.feature.cardeditor.model.ElementTransform

data class TransformState(
    val elementKey: String = "",
    val thumbnailKey: String = "",
    val initialTransform: ElementTransform = ElementTransform(),
    val tempTransform: ElementTransform = ElementTransform(),
    val showUnsavedChangesDialog: Boolean = false,
    val isCameraFocus: Boolean = true,
) {
    val hasPendingTransform: Boolean
        get() = initialTransform != tempTransform
}