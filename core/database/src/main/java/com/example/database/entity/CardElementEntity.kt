package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_elements")
data class CardElementEntity(
    @PrimaryKey(autoGenerate = true)
    val elementId: Long = 0,

    val cardId: Long,              // FK to Card.cardId
    val assetId: Long? = null,     // null when elementType = TEXT
    val elementType: ElementType, // OBJECT / TEXT

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
    val fontWeight: String? = null, // normal / bold
    val textColor: String? = null,
    val textAlign: TextAlign? = null,
    val lineSpacing: Float? = null,
    val letterSpacing: Float? = null
)

enum class ElementType {
    OBJECT,
    TEXT
}

enum class TextAlign {
    LEFT,
    CENTER,
    RIGHT
}
