package com.zcard.domain.bridge

import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.RgbaColor
import com.zcard.domain.model.TextElement

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
        posZ: Float
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

    fun exportAndUpload()

    fun getGlbDownloadUrl(fileName: String)
}