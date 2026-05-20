package com.zcard.data.mapper

import com.zcard.data.dto.ColorDto
import com.zcard.data.dto.ObjectDto
import com.zcard.data.dto.ResetSceneDto
import com.zcard.data.dto.SceneDto
import com.zcard.data.dto.TextDto
import com.zcard.data.dto.TextListDto
import com.zcard.data.dto.UpdateDto
import com.zcard.data.dto.Vector3Dto
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.RgbaColor
import com.zcard.domain.model.TextElement

object UnityMapper {
    fun toSceneDto(objects: List<CardElementWithAssetKeys>, texts: List<TextElement>): SceneDto =
        SceneDto(
            objects = objects.map {
                ObjectDto(
                    id = "${it.cardElement.elementId}",
                    prefabName = it.unityKey,
                    position = Vector3Dto(
                        x = it.cardElement.posX,
                        y = it.cardElement.posY,
                        z = it.cardElement.posZ
                    ),
                    scale = it.cardElement.scale
                )
            },
            texts = texts.map {
                val id = requireNotNull(it.elementId) { "TextElement must have elementId before sending to Unity" }
                TextDto(
                    id = "$id",
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

    fun toResetSceneDto(
        cardId: Long,
        resetIdCounter: Int
    ): ResetSceneDto =
        ResetSceneDto(
            cardId = cardId,
            resetIdCounter = resetIdCounter
        )

    fun toObjectDto(
        unityKey: String,
        elementId: Long,
        element: CardElement,
    ): ObjectDto =
        ObjectDto(
            id = "$elementId",
            prefabName = unityKey,
            position = Vector3Dto(element.posX, element.posY, element.posZ),
            scale = element.scale,
        )

    fun toTextDto(
        tempId: Long?,
        textElement: TextElement
    ): TextDto {
        val resolvedId = tempId ?: textElement.elementId
            ?: error("toTextDto requires either tempId or textElement.elementId to be non-null")
        return TextDto(
            id = "$resolvedId",
            position = Vector3Dto(textElement.posX, textElement.posY, textElement.posZ),
            textContent = textElement.attributes.content,
            fontFamilyName = textElement.attributes.fontFamily.key,
            fontSize = textElement.attributes.fontSize,
            color = textElement.attributes.textColor.rgbaColor.toDto(),
            textAlignInt = textElement.attributes.alignment.alignCode
        )
    }

    fun toUpdatePositionDto(elementId: Long, posX: Float, posY: Float, posZ: Float): UpdateDto<Vector3Dto> =
        UpdateDto(
            id = "$elementId",
            value = Vector3Dto(posX, posY, posZ)
        )

    fun toUpdateScaleDto(elementId: Long, scale: Int): UpdateDto<Int> =
        UpdateDto(
            id = "$elementId",
            value = scale
        )

    fun toUpdateFontSizeDto(elementId: Long, fontSize: Float): UpdateDto<Float> =
        UpdateDto(
            id = "$elementId",
            value = fontSize
        )

    fun toUpdateTextColorDto(elementId: Long, rgbaColor: RgbaColor): UpdateDto<ColorDto> =
        UpdateDto(
            id = "$elementId",
            value = rgbaColor.toDto()
        )

    fun toUpdateTextContentDto(elementId: Long, textContent: String): UpdateDto<String> =
        UpdateDto(
            id = "$elementId",
            value = textContent
        )

    fun toUpdateFontDto(elementId: Long, fontFamilyName: String): UpdateDto<String> =
        UpdateDto(
            id = "$elementId",
            value = fontFamilyName
        )

    fun toUpdateTextAlignDto(elementId: Long, textAlignInt: Int): UpdateDto<Int> =
        UpdateDto(
            id = "$elementId",
            value = textAlignInt
        )

    fun toTextListDto(textElements: List<TextElement>): TextListDto =
        TextListDto(
            texts = textElements.map { textElement ->
                val id = requireNotNull(textElement.elementId) { "TextElement must have elementId before sending to Unity" }
                TextDto(
                    id = "$id",
                    position = Vector3Dto(textElement.posX, textElement.posY, textElement.posZ),
                    textContent = textElement.attributes.content,
                    fontFamilyName = textElement.attributes.fontFamily.key,
                    fontSize = textElement.attributes.fontSize,
                    color = textElement.attributes.textColor.rgbaColor.toDto(),
                    textAlignInt = textElement.attributes.alignment.alignCode
                )
            }
        )
}