package com.itschristmas.domain.bridge

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.TextElement

interface UnityBridge {
    fun initScene(
        objectElements: List<CardElementWithAssetKeys>,
        textElements: List<TextElement>
    )

    fun changeBackground(backgroundKey: String)

    fun createObject(
        unityKey: String,
        elementId: Long,
        posX: Double,
        posY: Double,
        posZ: Double,
        scale: Int
    )

    fun createText(
        elementId: Long,
        textContent: String,
        posX: Double,
        posY: Double,
        posZ: Double,
        fontFamilyName: String,
        fontSize: Float,
        colorR: Float,
        colorG: Float,
        colorB: Float,
        colorA: Float,
        textAlignInt: Int,
    )

    fun updatePosition(
        elementId: Long,
        posX: Float,
        posY: Float,
        posZ: Float = 1f,
    )

    fun updateScale(elementId: Long, scale: Int)

    fun updateTextContent(elementId: Long, textContent: String)

    fun updateFontSize(elementId: Long, fontSize: Float)

    fun updateTextColor(
        elementId: Long,
        colorR: Float,
        colorG: Float,
        colorB: Float,
        colorA: Float
    )

    fun updateFont(elementId: Long, fontFamilyName: String)

    fun updateTextAlign(elementId: Long, textAlignInt: Int)

    fun selectObject(elementId: Long)

    fun clearSelection()

    fun deleteObject(elementId: Long)
}