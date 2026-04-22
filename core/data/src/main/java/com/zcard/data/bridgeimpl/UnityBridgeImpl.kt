package com.zcard.data.bridgeimpl

import com.zcard.data.mapper.UnityMapper
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.RgbaColor
import com.zcard.domain.model.TextElement
import com.unity3d.player.UnityPlayer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UnityBridgeImpl @Inject constructor(): UnityBridge {

    private companion object {
        const val UNITY_BRIDGE = "UnityBridge"
    }

    private object Methods {
        const val INIT_SCENE = "InitScene"
        const val RESET_SCENE = "ResetScene"
        const val CHANGE_BACKGROUND = "ChangeBackground"

        const val CREATE_OBJECT = "CreateObject"
        const val CREATE_TEXT = "CreateText"

        const val UPDATE_POSITION = "UpdatePosition"
        const val UPDATE_SCALE = "UpdateScale"
        const val UPDATE_FONT_SIZE = "UpdateFontSize"
        const val UPDATE_TEXT_COLOR = "UpdateTextColor"
        const val UPDATE_TEXT_CONTENT = "UpdateTextContent"
        const val UPDATE_FONT = "UpdateFont"
        const val UPDATE_TEXT_ALIGN = "UpdateTextAlign"

        const val REPLACE_ALL_TEXTS = "ReplaceAllTexts"
        const val CLEAR_ALL_TEXTS = "ClearAllTexts"

        const val SELECT_OBJECT = "SelectObject"
        const val CLEAR_SELECTION = "ClearSelection"

        const val DELETE_OBJECT = "DeleteObject"

        const val EXPORT_GLB = "ExportGlb"
    }

    private inline fun <reified T> send(method: String, payload: T) {
        val message = if(payload is String) payload else Json.encodeToString(payload)
        UnityPlayer.UnitySendMessage(UNITY_BRIDGE, method, message)
    }

    override fun initScene(
        objectElements: List<CardElementWithAssetKeys>,
        textElements: List<TextElement>
    ) {
        send(Methods.INIT_SCENE, UnityMapper.toSceneDto(objectElements, textElements))
    }

    override fun resetScene(cardId: Long, resetIdCounter: Int) {
        send(Methods.RESET_SCENE, UnityMapper.toResetSceneDto(cardId, resetIdCounter))
    }

    override fun changeBackground(backgroundKey: String) {
        send(Methods.CHANGE_BACKGROUND, backgroundKey)
    }

    override fun createObject(unityKey: String, elementId: Long, element: CardElement) {
        send(Methods.CREATE_OBJECT, UnityMapper.toObjectDto(unityKey, elementId, element))
    }

    override fun createText(tempId: Long?, textElement: TextElement) {
        send(Methods.CREATE_TEXT, UnityMapper.toTextDto(tempId, textElement))
    }

    override fun updatePosition(elementId: Long, posX: Float, posY: Float, posZ: Float) {
        send(Methods.UPDATE_POSITION, UnityMapper.toUpdatePositionDto(elementId, posX, posY, posZ))
    }

    override fun updateScale(elementId: Long, scale: Int) {
        send(Methods.UPDATE_SCALE, UnityMapper.toUpdateScaleDto(elementId, scale))
    }

    override fun updateFontSize(elementId: Long, fontSize: Float) {
        send(Methods.UPDATE_FONT_SIZE, UnityMapper.toUpdateFontSizeDto(elementId, fontSize))
    }

    override fun updateTextColor(elementId: Long, rgbaColor: RgbaColor) {
        send(Methods.UPDATE_TEXT_COLOR, UnityMapper.toUpdateTextColorDto(elementId, rgbaColor))
    }

    override fun updateTextContent(elementId: Long, textContent: String) {
        send(Methods.UPDATE_TEXT_CONTENT, UnityMapper.toUpdateTextContentDto(elementId, textContent))
    }

    override fun updateFont(elementId: Long, fontFamilyName: String) {
        send(Methods.UPDATE_FONT, UnityMapper.toUpdateFontDto(elementId, fontFamilyName))
    }

    override fun updateTextAlign(elementId: Long, textAlignInt: Int) {
        send(Methods.UPDATE_TEXT_ALIGN, UnityMapper.toUpdateTextAlignDto(elementId, textAlignInt))
    }

    override fun replaceAllTexts(textElements: List<TextElement>) {
        send(Methods.REPLACE_ALL_TEXTS, UnityMapper.toTextListDto(textElements))
    }

    override fun clearAllTexts() {
        send(Methods.CLEAR_ALL_TEXTS, "")
    }

    override fun selectObject(elementId: Long) {
        send(Methods.SELECT_OBJECT, "$elementId")
    }

    override fun clearSelection() {
        send(Methods.CLEAR_SELECTION, "")
    }

    override fun deleteObject(elementId: Long) {
        send(Methods.DELETE_OBJECT, "$elementId")
    }

    override fun exportGlb(exportId: Long) {
        send(Methods.EXPORT_GLB, "$exportId")
    }
}
