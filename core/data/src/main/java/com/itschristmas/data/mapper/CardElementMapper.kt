package com.itschristmas.data.mapper

import com.itschristmas.database.entity.CardElementEntity
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.enum.UnityFontWeight
import com.itschristmas.domain.enum.UnityTextAlign
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.UnityTextStyle

fun CardElementEntity.toDomain(): CardElement {
    val elementType = ElementType.valueOf(elementType)
    val unityTextStyle = textContent?.let {
        val default = UnityTextStyle()
        UnityTextStyle(
            textContent = it,
            fontFamily = fontFamily ?: default.fontFamily,
            fontSize = fontSize ?: default.fontSize,
            fontWeight = fontWeight?.let { UnityFontWeight.valueOf(it) } ?: default.fontWeight,
            textColor = textColor ?: default.textColor,
            textAlign = textAlign?.let { UnityTextAlign.valueOf(it) } ?: default.textAlign,
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
        unityTextStyle = unityTextStyle
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
        textContent = unityTextStyle?.textContent,
        fontFamily = unityTextStyle?.fontFamily,
        fontSize = unityTextStyle?.fontSize,
        fontWeight = unityTextStyle?.fontWeight?.name,
        textColor = unityTextStyle?.textColor,
        textAlign = unityTextStyle?.textAlign?.name,
    )
}