package com.itschristmas.data.mapper

import com.itschristmas.database.dto.GlbAndBgFirebaseKeyDto
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GlbAndBgFirebaseKeyMapperTest {

    @Test
    fun `toDomain converts dto with glbKey to domain model`() {
        // Given
        val dto = GlbAndBgFirebaseKeyDto(
            glbKey = "glb/model/file.glb",
            backgroundKey = "backgrounds/bg_winter.jpg"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals("glb/model/file.glb", domain.glbKey)
        assertEquals("backgrounds/bg_winter.jpg", domain.backgroundKey)
    }

    @Test
    fun `toDomain converts dto with null glbKey to domain model`() {
        // Given
        val dto = GlbAndBgFirebaseKeyDto(
            glbKey = null,
            backgroundKey = "backgrounds/default.jpg"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertNull(domain.glbKey)
        assertEquals("backgrounds/default.jpg", domain.backgroundKey)
    }

    @Test
    fun `toDomain handles empty background key`() {
        // Given
        val dto = GlbAndBgFirebaseKeyDto(
            glbKey = "glb/test.glb",
            backgroundKey = ""
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals("glb/test.glb", domain.glbKey)
        assertEquals("", domain.backgroundKey)
    }

    @Test
    fun `toDomain handles complex file paths`() {
        // Given
        val dto = GlbAndBgFirebaseKeyDto(
            glbKey = "users/123/cards/456/model.glb",
            backgroundKey = "assets/backgrounds/seasonal/winter/snow.png"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals("users/123/cards/456/model.glb", domain.glbKey)
        assertEquals("assets/backgrounds/seasonal/winter/snow.png", domain.backgroundKey)
    }
}