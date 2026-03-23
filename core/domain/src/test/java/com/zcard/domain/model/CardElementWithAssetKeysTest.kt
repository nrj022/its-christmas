package com.zcard.domain.model

import com.zcard.domain.enum.ElementType
import org.junit.Test
import kotlin.test.assertEquals

class CardElementWithAssetKeysTest {

    @Test
    fun `CardElementWithAssetKeys contains card element and keys`() {
        // Given
        val cardElement = CardElement(
            elementId = 1L,
            cardId = 100L,
            assetId = 200L,
            elementType = ElementType.OBJECT,
            posX = 10,
            posY = 20
        )

        // When
        val withKeys = CardElementWithAssetKeys(
            cardElement = cardElement,
            unityKey = "unity/path/asset",
            thumbnailKey = "thumbnails/asset"
        )

        // Then
        assertEquals(cardElement, withKeys.cardElement)
        assertEquals("unity/path/asset", withKeys.unityKey)
        assertEquals("thumbnails/asset", withKeys.thumbnailKey)
    }

    @Test
    fun `CardElementWithAssetKeys handles empty keys`() {
        // Given
        val cardElement = CardElement(
            elementId = 1L,
            cardId = 100L,
            assetId = 200L,
            elementType = ElementType.OBJECT,
            posX = 0,
            posY = 0
        )

        // When
        val withKeys = CardElementWithAssetKeys(
            cardElement = cardElement,
            unityKey = "",
            thumbnailKey = ""
        )

        // Then
        assertEquals("", withKeys.unityKey)
        assertEquals("", withKeys.thumbnailKey)
    }
}