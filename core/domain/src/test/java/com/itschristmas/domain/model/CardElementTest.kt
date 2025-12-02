package com.itschristmas.domain.model

import com.itschristmas.domain.enum.ElementType
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CardElementTest {

    @Test
    fun `CardElement for OBJECT type has no text style`() {
        // When
        val element = CardElement(
            elementId = 1L,
            cardId = 100L,
            assetId = 200L,
            elementType = ElementType.OBJECT,
            posX = 10,
            posY = 20
        )

        // Then
        assertEquals(ElementType.OBJECT, element.elementType)
        assertNotNull(element.assetId)
        assertNull(element.unityTextStyle)
    }

    @Test
    fun `CardElement for TEXT type has text style and no assetId`() {
        // When
        val textStyle = UnityTextStyle(textContent = "Hello")
        val element = CardElement(
            elementId = 2L,
            cardId = 100L,
            assetId = null,
            elementType = ElementType.TEXT,
            posX = 5,
            posY = 10,
            unityTextStyle = textStyle
        )

        // Then
        assertEquals(ElementType.TEXT, element.elementType)
        assertNull(element.assetId)
        assertNotNull(element.unityTextStyle)
        assertEquals("Hello", element.unityTextStyle?.textContent)
    }

    @Test
    fun `CardElement uses default transform values`() {
        // When
        val element = CardElement(
            elementId = 3L,
            cardId = 100L,
            assetId = 200L,
            elementType = ElementType.OBJECT,
            posX = 0,
            posY = 0
        )

        // Then
        assertEquals(0, element.posZ)
        assertEquals(0, element.rotX)
        assertEquals(0, element.rotY)
        assertEquals(0, element.rotZ)
        assertEquals(1, element.scale)
    }

    @Test
    fun `CardElement can have custom transform values`() {
        // When
        val element = CardElement(
            elementId = 4L,
            cardId = 100L,
            assetId = 200L,
            elementType = ElementType.OBJECT,
            posX = 10,
            posY = 20,
            posZ = 30,
            rotX = 45,
            rotY = 90,
            rotZ = 180,
            scale = 3
        )

        // Then
        assertEquals(10, element.posX)
        assertEquals(20, element.posY)
        assertEquals(30, element.posZ)
        assertEquals(45, element.rotX)
        assertEquals(90, element.rotY)
        assertEquals(180, element.rotZ)
        assertEquals(3, element.scale)
    }

    @Test
    fun `CardElement copy function works correctly`() {
        // Given
        val original = CardElement(
            elementId = 5L,
            cardId = 100L,
            assetId = 200L,
            elementType = ElementType.OBJECT,
            posX = 10,
            posY = 20
        )

        // When
        val modified = original.copy(posX = 30, scale = 2)

        // Then
        assertEquals(30, modified.posX)
        assertEquals(2, modified.scale)
        assertEquals(original.elementId, modified.elementId)
        assertEquals(original.posY, modified.posY)
    }
}