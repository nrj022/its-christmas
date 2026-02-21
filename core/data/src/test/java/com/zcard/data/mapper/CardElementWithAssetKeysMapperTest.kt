package com.zcard.data.mapper

import com.zcard.database.dto.CardElementWithAssetKeysDto
import com.zcard.database.entity.CardElementEntity
import com.zcard.domain.enum.ElementType
import org.junit.Test
import kotlin.test.assertEquals

class CardElementWithAssetKeysMapperTest {

    @Test
    fun `toDomain converts dto to domain model correctly`() {
        // Given
        val cardElementEntity = CardElementEntity(
            elementId = 1L,
            cardId = 100L,
            assetId = 200L,
            elementType = "OBJECT",
            posX = 10,
            posY = 20,
            posZ = 30,
            rotX = 40,
            rotY = 50,
            rotZ = 60,
            scale = 2
        )
        val dto = CardElementWithAssetKeysDto(
            cardElement = cardElementEntity,
            unityKey = "unity/path/to/asset",
            thumbnailKey = "thumbnails/asset_thumb"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(1L, domain.cardElement.elementId)
        assertEquals(100L, domain.cardElement.cardId)
        assertEquals(200L, domain.cardElement.assetId)
        assertEquals(ElementType.OBJECT, domain.cardElement.elementType)
        assertEquals("unity/path/to/asset", domain.unityKey)
        assertEquals("thumbnails/asset_thumb", domain.thumbnailKey)
    }

    @Test
    fun `toDomain handles empty string keys`() {
        // Given
        val cardElementEntity = CardElementEntity(
            elementId = 2L,
            cardId = 101L,
            assetId = 201L,
            elementType = "OBJECT",
            posX = 0,
            posY = 0
        )
        val dto = CardElementWithAssetKeysDto(
            cardElement = cardElementEntity,
            unityKey = "",
            thumbnailKey = ""
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals("", domain.unityKey)
        assertEquals("", domain.thumbnailKey)
    }

    @Test
    fun `toDomain preserves all card element properties`() {
        // Given
        val cardElementEntity = CardElementEntity(
            elementId = 3L,
            cardId = 102L,
            assetId = 202L,
            elementType = "OBJECT",
            posX = 15,
            posY = 25,
            posZ = 35,
            rotX = 45,
            rotY = 55,
            rotZ = 65,
            scale = 5
        )
        val dto = CardElementWithAssetKeysDto(
            cardElement = cardElementEntity,
            unityKey = "test_unity_key",
            thumbnailKey = "test_thumbnail_key"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(15, domain.cardElement.posX)
        assertEquals(25, domain.cardElement.posY)
        assertEquals(35, domain.cardElement.posZ)
        assertEquals(45, domain.cardElement.rotX)
        assertEquals(55, domain.cardElement.rotY)
        assertEquals(65, domain.cardElement.rotZ)
        assertEquals(5, domain.cardElement.scale)
    }

    @Test
    fun `toDomain handles text elements with asset keys`() {
        // Given
        val cardElementEntity = CardElementEntity(
            elementId = 4L,
            cardId = 103L,
            assetId = null,
            elementType = "TEXT",
            posX = 5,
            posY = 10,
            textContent = "Test Text",
            fontFamily = "Arial",
            fontSize = 14f,
            fontWeight = "NORMAL",
            textColor = "#000000",
            textAlign = "LEFT"
        )
        val dto = CardElementWithAssetKeysDto(
            cardElement = cardElementEntity,
            unityKey = "font_unity_key",
            thumbnailKey = "font_thumbnail"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(ElementType.TEXT, domain.cardElement.elementType)
        assertEquals("Test Text", domain.cardElement.unityTextStyle?.textContent)
        assertEquals("font_unity_key", domain.unityKey)
        assertEquals("font_thumbnail", domain.thumbnailKey)
    }
}