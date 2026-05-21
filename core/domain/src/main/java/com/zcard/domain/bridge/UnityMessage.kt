@file:OptIn(InternalSerializationApi::class)

package com.zcard.domain.bridge

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

enum class UnityEventType {
    SCENE_RESET,
    CREATE_CARD_THUMB,
    CREATE_OBJECT,
    EXPORT_GLB,
}

enum class UnityEventStatus  {
    // Result
    SUCCESS,
    FAILURE,
    CANCELED,
}

@Serializable
data class UnityMessage(
    val type: UnityEventType,
    val status: UnityEventStatus,
    val data: String
)