package com.itschristmas.domain.bridge

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.RgbaColor
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
        posX: Float,
        posY: Float,
        posZ: Float,
        scale: Int
    )

    fun createText(
        tempId: Long? = null,
        textElement: TextElement
    )

    fun updatePosition(
        elementId: Long,
        posX: Float,
        posY: Float,
        posZ: Float = 0f,
    )

    fun updateScale(elementId: Long, scale: Int)

    fun updateTextContent(elementId: Long, textContent: String)

    fun updateFontSize(elementId: Long, fontSize: Float)

    fun updateTextColor(elementId: Long, rgbaColor: RgbaColor)

    fun updateFont(elementId: Long, fontFamilyName: String)

    fun updateTextAlign(elementId: Long, textAlignInt: Int)

    fun replaceAllTexts(textElements: List<TextElement>)

    fun clearAllTexts()

    fun selectObject(elementId: Long)

    fun clearSelection()

    fun deleteObject(elementId: Long)
}