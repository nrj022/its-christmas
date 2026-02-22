package com.zcard.data.bridgeimpl

import com.zcard.data.dto.ObjectDto
import com.zcard.data.dto.SceneDto
import com.zcard.data.dto.TextDto
import com.zcard.data.dto.TextListDto
import com.zcard.data.dto.UpdateDto
import com.zcard.data.dto.Vector3Dto
import com.zcard.data.mapper.toDto
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.RgbaColor
import com.zcard.domain.model.TextElement
import com.unity3d.player.UnityPlayer
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import javax.inject.Inject

class UnityBridgeImpl @Inject constructor(): UnityBridge {

    override fun initScene(
        objectElements: List<CardElementWithAssetKeys>,
        textElements: List<TextElement>
    ) {
        val jsonString = Json.encodeToString(
            SceneDto(
                objects = objectElements.map {
                    ObjectDto(
                        id = "${it.cardElement.elementId}",
                        prefabName = it.unityKey,
                        position = Vector3Dto(
                            x = it.cardElement.posX,
                            y = it.cardElement.posY,
                            z = it.cardElement.posZ
                        ),
                        scale = it.cardElement.scale
                  )},
                texts = textElements.map {
                    TextDto(
                        id = "${it.elementId}",
                        position = Vector3Dto(
                            x = it.posX,
                            y = it.posY,
                            z = it.posZ
                        ),
                        textContent = it.attributes.content,
                        fontFamilyName = it.attributes.fontFamily.key,
                        fontSize = it.attributes.fontSize,
                        color = it.attributes.textColor.rgbaColor.toDto(),
                        textAlignInt = it.attributes.alignment.alignCode,
                    )
                }
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "InitializeObjectsFromAndroid", jsonString)
    }

    override fun changeBackground(backgroundKey: String) {
        UnityPlayer.UnitySendMessage("BackgroundManager", "ChangeBackground", backgroundKey)
    }

    override fun createObject(
        unityKey: String,
        elementId: Long,
        posX: Float,
        posY: Float,
        posZ: Float,
        scale: Int
    ) {
        val jsonString = Json.encodeToString(
            ObjectDto(
                id = "$elementId",
                prefabName = unityKey,
                position = Vector3Dto(posX, posY, posZ),
                scale = scale,
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "CreateObjectFromAndroid", jsonString)
    }

    override fun createText(
        tempId: Long?,
        textElement: TextElement
    ) {
        val jsonString = Json.encodeToString(
            TextDto(
                id = "${tempId ?: textElement.elementId}",
                position = Vector3Dto(textElement.posX, textElement.posY, textElement.posZ),
                textContent = textElement.attributes.content,
                fontFamilyName = textElement.attributes.fontFamily.key,
                fontSize = textElement.attributes.fontSize,
                color = textElement.attributes.textColor.rgbaColor.toDto(),
                textAlignInt = textElement.attributes.alignment.alignCode
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "CreateTextFromAndroid", jsonString)
    }

    override fun updatePosition(
        elementId: Long,
        posX: Float,
        posY: Float,
        posZ: Float,
    ) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = Vector3Dto(posX, posY, posZ)
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdatePositionFromAndroid", jsonString)
    }

    override fun updateScale(elementId: Long, scale: Int) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = scale
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateScaleFromAndroid", jsonString)
    }

    override fun updateFontSize(elementId: Long, fontSize: Float) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = fontSize
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateFontSizeFromAndroid", jsonString)
    }

    override fun updateTextColor(elementId: Long, rgbaColor: RgbaColor) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = rgbaColor.toDto()
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateTextColorFromAndroid", jsonString)
    }

    override fun updateTextContent(elementId: Long, textContent: String) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = textContent
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateTextContentFromAndroid", jsonString)
    }

    override fun updateFont(elementId: Long, fontFamilyName: String) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = fontFamilyName
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateFontFromAndroid", jsonString)
    }

    override fun updateTextAlign(elementId: Long, textAlignInt: Int) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = textAlignInt
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateTextAlignFromAndroid", jsonString)
    }

    override fun replaceAllTexts(textElements: List<TextElement>) {
        val jsonString = Json.encodeToString(
            TextListDto(
                texts = textElements.mapNotNull {
                    it.elementId?.let { id ->
                        TextDto(
                            id = "$id",
                            position = Vector3Dto(it.posX, it.posY, it.posZ),
                            textContent = it.attributes.content,
                            fontFamilyName = it.attributes.fontFamily.key,
                            fontSize = it.attributes.fontSize,
                            color = it.attributes.textColor.rgbaColor.toDto(),
                            textAlignInt = it.attributes.alignment.alignCode
                        )
                    }
                }
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "ReplaceAllTextsFromAndroid", jsonString)
    }

    override fun clearAllTexts() {
        UnityPlayer.UnitySendMessage("ObjectManager", "ClearAllTexts", "")
    }

    override fun selectObject(elementId: Long) {
        UnityPlayer.UnitySendMessage("ObjectManager", "SelectObject", "$elementId")
    }

    override fun clearSelection() {
        UnityPlayer.UnitySendMessage("ObjectManager", "ClearSelection", "")
    }

    override fun deleteObject(elementId: Long) {
        UnityPlayer.UnitySendMessage("ObjectManager", "DeleteObject", "$elementId")
    }

    override fun exportAndUpload() {
        UnityPlayer.UnitySendMessage("GlbExportManager", "ExportAndUpload", "")
    }
}