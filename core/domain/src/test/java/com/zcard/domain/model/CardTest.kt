package com.zcard.domain.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CardTest {

    @Test
    fun `Card with minimal required fields`() {
        // Given
        val timestamp = System.currentTimeMillis()

        // When
        val card = Card(
            cardId = 0L,
            createdAt = timestamp,
            updatedAt = timestamp
        )

        // Then
        assertEquals(0L, card.cardId)
        assertEquals(1L, card.backgroundAssetId)
        assertTrue(card.isDraft)
        assertNull(card.title)
        assertNull(card.glbKey)
        assertNull(card.thumbnailPath)
    }

    @Test
    fun `Card with all fields populated`() {
        // Given
        val timestamp = System.currentTimeMillis()

        // When
        val card = Card(
            cardId = 1L,
            title = "My Christmas Card",
            glbKey = "glb/card_model.glb",
            thumbnailPath = "thumbnails/card.jpg",
            backgroundAssetId = 5L,
            isDraft = false,
            createdAt = timestamp,
            updatedAt = timestamp
        )

        // Then
        assertEquals(1L, card.cardId)
        assertEquals("My Christmas Card", card.title)
        assertEquals("glb/card_model.glb", card.glbKey)
        assertEquals("thumbnails/card.jpg", card.thumbnailPath)
        assertEquals(5L, card.backgroundAssetId)
        assertEquals(false, card.isDraft)
    }

    @Test
    fun `Card default backgroundAssetId is 1`() {
        // When
        val card = Card(
            cardId = 0L,
            createdAt = 0L,
            updatedAt = 0L
        )

        // Then
        assertEquals(1L, card.backgroundAssetId)
    }

    @Test
    fun `Card copy function works correctly`() {
        // Given
        val original = Card(
            cardId = 1L,
            title = "Original",
            createdAt = 100L,
            updatedAt = 100L
        )

        // When
        val modified = original.copy(
            title = "Modified",
            isDraft = false
        )

        // Then
        assertEquals("Modified", modified.title)
        assertEquals(false, modified.isDraft)
        assertEquals(original.cardId, modified.cardId)
        assertEquals(original.createdAt, modified.createdAt)
    }
}