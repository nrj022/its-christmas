package com.zcard.feature.cardeditor.transform

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zcard.feature.R

data class TransformState(
    val elementKey: String = "",
    val thumbnailKey: String = "",
    val initialTransform: ElementTransform = ElementTransform(),
    val tempTransform: ElementTransform = ElementTransform(),
    val showUnsavedChangesDialog: Boolean = false,
    val isCameraFocus: Boolean = true,
    val transformToggleType: TransformType = TransformType.ROTATION,
) {
    val hasPendingTransform: Boolean
        get() = initialTransform != tempTransform

    enum class Axis { NONE, X, Y, Z }

    enum class TransformType(@DrawableRes val icon: Int, @StringRes val label: Int) {
        ROTATION(R.drawable.ic_3d_rotation, R.string.transform_label_type_rotation),
        TRANSLATION(R.drawable.ic_open_with, R.string.transform_label_type_translation);

        fun toggled() = if (this == ROTATION) TRANSLATION else ROTATION
    }
}