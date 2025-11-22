package com.example.database.entity

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
    val posX: Int,
    val posY: Int,
    val posZ: Int? = null,

    val rotX: Int? = null,
    val rotY: Int? = null,
    val rotZ: Int? = null,

    val scale: Int,

    // Text fields (nullable)
    val textContent: String? = null,
    val fontFamily: String? = null,
    val fontSize: Float? = null,
    val fontWeight: String? = null, // UnityFontWeight: NORMAL / BOLD
    val textColor: String? = null,
    val textAlign: String? = null,   // UnityTextAlign: LEFT / CENTER / RIGHT
    val lineSpacing: Float? = null,
    val letterSpacing: Float? = null
)