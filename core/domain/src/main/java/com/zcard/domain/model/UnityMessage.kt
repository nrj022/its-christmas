@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.zcard.domain.model

import kotlinx.serialization.Serializable

enum class UnityMessageType {
    LIFECYCLE,
    UPLOAD_GLB
}

enum class UnityStatusType {
    SUCCESS,
    FAILURE,
    START,
    STOP,
}

@Serializable
data class UnityMessage(
    val type: UnityMessageType,
    val status: UnityStatusType,
    val data: String
)