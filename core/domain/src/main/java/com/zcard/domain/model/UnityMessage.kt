@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.zcard.domain.model

import kotlinx.serialization.Serializable

enum class UnityEventType {
    LIFECYCLE,
    CREATE_OBJECT,
    EXPORT_GLB
}

enum class UnityEventStatus  {
    // Result
    SUCCESS,
    FAILURE,

    // Lifecycle
    AWAKE,
    START,
    DISABLE,
    DESTROY,
    QUIT
}

@Serializable
data class UnityMessage(
    val type: UnityEventType,
    val status: UnityEventStatus,
    val data: String
)