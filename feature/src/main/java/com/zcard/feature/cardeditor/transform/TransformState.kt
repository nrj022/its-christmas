package com.zcard.feature.cardeditor.transform

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

    enum class Axis { NONE, X, Y, Z }
}