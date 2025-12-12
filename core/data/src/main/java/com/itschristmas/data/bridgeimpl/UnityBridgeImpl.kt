package com.itschristmas.data.bridgeimpl

import com.itschristmas.data.dto.ColorDto
import com.itschristmas.data.dto.ObjectDto
import com.itschristmas.data.dto.SceneDto
import com.itschristmas.data.dto.TextDto
import com.itschristmas.data.dto.UpdateDto
import com.itschristmas.data.dto.Vector3Dto
import com.itschristmas.domain.bridge.UnityBridge
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.TextElement
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
                            x = it.cardElement.posX.toFloat(),
                            y = it.cardElement.posY.toFloat(),
                            z = it.cardElement.posZ.toFloat()
                        ),
                        scale = it.cardElement.scale
                  )},
                texts = textElements.map {
                    TextDto(
                        id = "${it.elementId}",
                        position = Vector3Dto(
                            x = it.posX.toFloat(),
                            y = it.posY.toFloat(),
                            z = 0f
                        ),
                        textContent = it.attributes.content,
                        fontFamilyName = it.attributes.fontFamily.key,
                        fontSize = it.attributes.fontSize,
                        color = ColorDto(
                            r = it.attributes.textColor.rgbaColor.r,
                            g = it.attributes.textColor.rgbaColor.g,
                            b = it.attributes.textColor.rgbaColor.b,
                            a = it.attributes.textColor.rgbaColor.a,
                        ),
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
        posX: Double,
        posY: Double,
        posZ: Double,
        scale: Int
    ) {
        val jsonString = Json.encodeToString(
            ObjectDto(
                id = "$elementId",
                prefabName = unityKey,
                position = Vector3Dto(posX.toFloat(), posY.toFloat(), posZ.toFloat()),
                scale = scale,
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "CreateObjectFromAndroid", jsonString)
    }

    override fun createText(
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
    ) {
        val jsonString = Json.encodeToString(
            TextDto(
                id = "$elementId",
                position = Vector3Dto(posX.toFloat(), posY.toFloat(), posZ.toFloat()),
                textContent = textContent,
                fontFamilyName = fontFamilyName,
                fontSize = fontSize,
                color = ColorDto(colorR, colorG, colorB, colorA),
                textAlignInt = textAlignInt
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

    override fun updateTextColor(
        elementId: Long,
        colorR: Float,
        colorG: Float,
        colorB: Float,
        colorA: Float
    ) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = ColorDto(colorR, colorG, colorB, colorA)
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateTextColor", jsonString)
    }

    override fun updateTextContent(elementId: Long, textContent: String) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = textContent
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateTextContent", jsonString)
    }

    override fun updateFont(elementId: Long, fontFamilyName: String) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = fontFamilyName
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateFont", jsonString)
    }

    override fun updateTextAlign(elementId: Long, textAlignInt: Int) {
        val jsonString = Json.encodeToString(
            UpdateDto(
                id = "$elementId",
                value = textAlignInt
            )
        )
        UnityPlayer.UnitySendMessage("AndroidMessageHandler", "UpdateTextAlign", jsonString)
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
}