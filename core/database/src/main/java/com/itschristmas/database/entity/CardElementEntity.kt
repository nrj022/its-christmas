package com.itschristmas.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_elements")
data class CardElementEntity(
    @PrimaryKey(autoGenerate = true)
    val elementId: Long = 0,

    val cardId: Long,              // FK to Card.cardId
    val assetId: Long? = null,     // null when elementType = TEXT
    val elementType: String, // ElementType: OBJECT / TEXT

    // Transform
    val posX: Float,
    val posY: Float,
    val posZ: Float = 0f,

    val rotX: Int = 0,
    val rotY: Int = 0,
    val rotZ: Int = 0,

    val scale: Int = 1,

    // Text fields (nullable)
    val textContent: String? = null,
    val fontFamily: String? = null,
    val fontSize: Float? = null,
    val textColor: String? = null,
    val textAlign: String? = null,   // UnityTextAlign: LEFT / CENTER / RIGHT
)