package com.zcard.feature.cardeditor.transform

import com.zcard.feature.cardeditor.model.Direction

sealed class TransformIntent {
    data class Init(val elementId: Long): TransformIntent()
    data object ToggleTransformType: TransformIntent()
    data class MoveObject(val direction: Direction): TransformIntent()
    data object StartRotationDrag: TransformIntent()
    data class RotateObject(val rotation: Rotation): TransformIntent()
    data class ChangeScale(val newScale: Int): TransformIntent()
    data object Exit: TransformIntent()
    data object DismissDialog: TransformIntent()
    data object SaveChanges: TransformIntent()
    data object SaveAndExit: TransformIntent()
    data object DiscardAndExit: TransformIntent()
    data object ResetTransform: TransformIntent()
    data object ToggleCameraFocus: TransformIntent()
    data object ResetCamera: TransformIntent()
}