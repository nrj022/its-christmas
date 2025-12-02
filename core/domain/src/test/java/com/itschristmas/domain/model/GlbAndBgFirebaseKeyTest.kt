package com.itschristmas.domain.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GlbAndBgFirebaseKeyTest {

    @Test
    fun `GlbAndBgFirebaseKey with both keys`() {
        // When
        val keys = GlbAndBgFirebaseKey(
            glbKey = "glb/model.glb",
            backgroundKey = "backgrounds/winter.jpg"
        )

        // Then
        assertEquals("glb/model.glb", keys.glbKey)
        assertEquals("backgrounds/winter.jpg", keys.backgroundKey)
    }

    @Test
    fun `GlbAndBgFirebaseKey with null glbKey`() {
        // When
        val keys = GlbAndBgFirebaseKey(
            glbKey = null,
            backgroundKey = "backgrounds/default.jpg"
        )

        // Then
        assertNull(keys.glbKey)
        assertEquals("backgrounds/default.jpg", keys.backgroundKey)
    }

    @Test
    fun `GlbAndBgFirebaseKey handles empty background key`() {
        // When
        val keys = GlbAndBgFirebaseKey(
            glbKey = "glb/test.glb",
            backgroundKey = ""
        )

        // Then
        assertEquals("glb/test.glb", keys.glbKey)
        assertEquals("", keys.backgroundKey)
    }
}