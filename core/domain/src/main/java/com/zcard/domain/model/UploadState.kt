package com.zcard.domain.model

sealed class UploadState {
    data class Progress(val percent: Int): UploadState()
    data object Success: UploadState()
    data object Failure: UploadState()
}