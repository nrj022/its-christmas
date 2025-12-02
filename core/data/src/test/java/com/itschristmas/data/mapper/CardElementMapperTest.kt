package com.itschristmas.data.mapper

import com.itschristmas.database.entity.CardElementEntity
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.enum.UnityFontWeight
import com.itschristmas.domain.enum.UnityTextAlign
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.UnityTextStyle
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CardElementMapperTest {

    @Test
    fun `toDomain converts object element entity to domain correctly`() {
        // Given
        val entity = CardElementEntity(
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

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(1L, domain.elementId)
        assertEquals(100L, domain.cardId)
        assertEquals(200L, domain.assetId)
        assertEquals(ElementType.OBJECT, domain.elementType)
        assertEquals(10, domain.posX)
        assertEquals(20, domain.posY)
        assertEquals(30, domain.posZ)
        assertEquals(40, domain.rotX)
        assertEquals(50, domain.rotY)
        assertEquals(60, domain.rotZ)
        assertEquals(2, domain.scale)
        assertNull(domain.unityTextStyle)
    }

    @Test
    fun `toDomain converts text element entity to domain with text style`() {
        // Given
        val entity = CardElementEntity(
            elementId = 2L,
            cardId = 100L,
            assetId = null,
            elementType = "TEXT",
            posX = 5,
            posY = 15,
            posZ = 0,
            rotX = 0,
            rotY = 0,
            rotZ = 0,
            scale = 1,
            textContent = "Hello World",
            fontFamily = "Arial",
            fontSize = 16f,
            fontWeight = "BOLD",
            textColor = "#FF0000",
            textAlign = "CENTER"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(2L, domain.elementId)
        assertEquals(ElementType.TEXT, domain.elementType)
        assertNull(domain.assetId)
        assertNotNull(domain.unityTextStyle)
        assertEquals("Hello World", domain.unityTextStyle?.textContent)
        assertEquals("Arial", domain.unityTextStyle?.fontFamily)
        assertEquals(16f, domain.unityTextStyle?.fontSize)
        assertEquals(UnityFontWeight.BOLD, domain.unityTextStyle?.fontWeight)
        assertEquals("#FF0000", domain.unityTextStyle?.textColor)
        assertEquals(UnityTextAlign.CENTER, domain.unityTextStyle?.textAlign)
    }

    @Test
    fun `toDomain uses default values for missing text style fields`() {
        // Given
        val entity = CardElementEntity(
            elementId = 3L,
            cardId = 100L,
            assetId = null,
            elementType = "TEXT",
            posX = 0,
            posY = 0,
            textContent = "Test",
            fontFamily = null,
            fontSize = null,
            fontWeight = null,
            textColor = null,
            textAlign = null
        )

        // When
        val domain = entity.toDomain()

        // Then
        val defaultStyle = UnityTextStyle()
        assertNotNull(domain.unityTextStyle)
        assertEquals("Test", domain.unityTextStyle?.textContent)
        assertEquals(defaultStyle.fontFamily, domain.unityTextStyle?.fontFamily)
        assertEquals(defaultStyle.fontSize, domain.unityTextStyle?.fontSize)
        assertEquals(defaultStyle.fontWeight, domain.unityTextStyle?.fontWeight)
        assertEquals(defaultStyle.textColor, domain.unityTextStyle?.textColor)
        assertEquals(defaultStyle.textAlign, domain.unityTextStyle?.textAlign)
    }

    @Test
    fun `toDomain handles default transform values`() {
        // Given
        val entity = CardElementEntity(
            elementId = 4L,
            cardId = 100L,
            assetId = 300L,
            elementType = "OBJECT",
            posX = 0,
            posY = 0,
            posZ = 0,
            rotX = 0,
            rotY = 0,
            rotZ = 0,
            scale = 1
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(0, domain.posZ)
        assertEquals(0, domain.rotX)
        assertEquals(0, domain.rotY)
        assertEquals(0, domain.rotZ)
        assertEquals(1, domain.scale)
    }

    @Test
    fun `toEntity converts object domain to entity correctly`() {
        // Given
        val domain = CardElement(
            elementId = 5L,
            cardId = 101L,
            assetId = 201L,
            elementType = ElementType.OBJECT,
            posX = 11,
            posY = 21,
            posZ = 31,
            rotX = 41,
            rotY = 51,
            rotZ = 61,
            scale = 3,
            unityTextStyle = null
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals(5L, entity.elementId)
        assertEquals(101L, entity.cardId)
        assertEquals(201L, entity.assetId)
        assertEquals("OBJECT", entity.elementType)
        assertEquals(11, entity.posX)
        assertEquals(21, entity.posY)
        assertEquals(31, entity.posZ)
        assertEquals(41, entity.rotX)
        assertEquals(51, entity.rotY)
        assertEquals(61, entity.rotZ)
        assertEquals(3, entity.scale)
        assertNull(entity.textContent)
        assertNull(entity.fontFamily)
        assertNull(entity.fontSize)
        assertNull(entity.fontWeight)
        assertNull(entity.textColor)
        assertNull(entity.textAlign)
    }

    @Test
    fun `toEntity converts text domain to entity with text style`() {
        // Given
        val textStyle = UnityTextStyle(
            textContent = "Sample Text",
            fontFamily = "Times",
            fontSize = 18f,
            fontWeight = UnityFontWeight.NORMAL,
            textColor = "#00FF00",
            textAlign = UnityTextAlign.RIGHT
        )
        val domain = CardElement(
            elementId = 6L,
            cardId = 102L,
            assetId = null,
            elementType = ElementType.TEXT,
            posX = 12,
            posY = 22,
            posZ = 0,
            rotX = 0,
            rotY = 0,
            rotZ = 0,
            scale = 1,
            unityTextStyle = textStyle
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals(6L, entity.elementId)
        assertEquals("TEXT", entity.elementType)
        assertNull(entity.assetId)
        assertEquals("Sample Text", entity.textContent)
        assertEquals("Times", entity.fontFamily)
        assertEquals(18f, entity.fontSize)
        assertEquals("NORMAL", entity.fontWeight)
        assertEquals("#00FF00", entity.textColor)
        assertEquals("RIGHT", entity.textAlign)
    }

    @Test
    fun `toEntity and toDomain are inverse operations for object`() {
        // Given
        val originalDomain = CardElement(
            elementId = 7L,
            cardId = 103L,
            assetId = 203L,
            elementType = ElementType.OBJECT,
            posX = 13,
            posY = 23,
            posZ = 33,
            rotX = 43,
            rotY = 53,
            rotZ = 63,
            scale = 4
        )

        // When
        val entity = originalDomain.toEntity()
        val resultDomain = entity.toDomain()

        // Then
        assertEquals(originalDomain.elementId, resultDomain.elementId)
        assertEquals(originalDomain.cardId, resultDomain.cardId)
        assertEquals(originalDomain.assetId, resultDomain.assetId)
        assertEquals(originalDomain.elementType, resultDomain.elementType)
        assertEquals(originalDomain.posX, resultDomain.posX)
        assertEquals(originalDomain.posY, resultDomain.posY)
        assertEquals(originalDomain.scale, resultDomain.scale)
    }

    @Test
    fun `toEntity and toDomain are inverse operations for text`() {
        // Given
        val textStyle = UnityTextStyle(
            textContent = "Round Trip Test",
            fontFamily = "Courier",
            fontSize = 20f,
            fontWeight = UnityFontWeight.BOLD,
            textColor = "#0000FF",
            textAlign = UnityTextAlign.LEFT
        )
        val originalDomain = CardElement(
            elementId = 8L,
            cardId = 104L,
            assetId = null,
            elementType = ElementType.TEXT,
            posX = 14,
            posY = 24,
            unityTextStyle = textStyle
        )

        // When
        val entity = originalDomain.toEntity()
        val resultDomain = entity.toDomain()

        // Then
        assertEquals(originalDomain.elementId, resultDomain.elementId)
        assertEquals(originalDomain.cardId, resultDomain.cardId)
        assertEquals(originalDomain.elementType, resultDomain.elementType)
        assertNotNull(resultDomain.unityTextStyle)
        assertEquals(textStyle.textContent, resultDomain.unityTextStyle?.textContent)
        assertEquals(textStyle.fontFamily, resultDomain.unityTextStyle?.fontFamily)
        assertEquals(textStyle.fontSize, resultDomain.unityTextStyle?.fontSize)
        assertEquals(textStyle.fontWeight, resultDomain.unityTextStyle?.fontWeight)
        assertEquals(textStyle.textColor, resultDomain.unityTextStyle?.textColor)
        assertEquals(textStyle.textAlign, resultDomain.unityTextStyle?.textAlign)
    }
}