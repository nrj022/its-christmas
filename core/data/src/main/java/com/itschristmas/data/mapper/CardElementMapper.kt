package com.itschristmas.data.mapper

import com.itschristmas.database.entity.CardElementEntity
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.ColorOption
import com.itschristmas.domain.model.FontOption
import com.itschristmas.domain.model.TextAlignmentOption
import com.itschristmas.domain.model.TextAttributes

fun CardElementEntity.toDomain(): CardElement {
    val elementType = ElementType.valueOf(elementType)
    val textAttributes = textContent?.let { text ->
        val default = TextAttributes()
        TextAttributes(
            content = text,
            fontFamily = fontFamily?.let { FontOption.valueOf(it) } ?: default.fontFamily,
            fontSize = fontSize ?: default.fontSize,
            textColor = textColor?.let { ColorOption.valueOf(it) } ?: default.textColor,
            alignment = textAlign?.let { TextAlignmentOption.valueOf(it) } ?: default.alignment,
        )
    }

    return CardElement(
        elementId = elementId,
        cardId = cardId,
        assetId = assetId,
        elementType = elementType,
        posX = posX,
        posY = posY,
        posZ = posZ,
        rotX = rotX,
        rotY = rotY,
        rotZ = rotZ,
        scale = scale,
        textAttributes = textAttributes
    )
}

fun CardElement.toEntity(): CardElementEntity {
    return CardElementEntity(
        elementId = elementId,
        cardId = cardId,
        assetId = assetId,
        elementType = elementType.name,
        posX = posX,
        posY = posY,
        posZ = posZ,
        rotX = rotX,
        rotY = rotY,
        rotZ = rotZ,
        scale = scale,
        textContent = textAttributes?.content,
        fontFamily = textAttributes?.fontFamily?.name,
        fontSize = textAttributes?.fontSize,
        textColor = textAttributes?.textColor?.name,
        textAlign = textAttributes?.alignment?.name,
    )
}