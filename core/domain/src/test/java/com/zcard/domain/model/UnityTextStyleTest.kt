package com.zcard.domain.model

import com.zcard.domain.enum.UnityFontWeight
import com.zcard.domain.enum.UnityTextAlign
import org.junit.Test
import kotlin.test.assertEquals

class UnityTextStyleTest {

    @Test
    fun `default constructor creates UnityTextStyle with default values`() {
        // When
        val style = UnityTextStyle()

        // Then
        assertEquals("New Text", style.textContent)
        assertEquals("LiberationSans", style.fontFamily)
        assertEquals(14f, style.fontSize)
        assertEquals(UnityFontWeight.NORMAL, style.fontWeight)
        assertEquals("#000000", style.textColor)
        assertEquals(UnityTextAlign.LEFT, style.textAlign)
    }

    @Test
    fun `constructor with parameters creates UnityTextStyle correctly`() {
        // When
        val style = UnityTextStyle(
            textContent = "Custom Text",
            fontFamily = "Arial",
            fontSize = 18f,
            fontWeight = UnityFontWeight.BOLD,
            textColor = "#FF0000",
            textAlign = UnityTextAlign.CENTER
        )

        // Then
        assertEquals("Custom Text", style.textContent)
        assertEquals("Arial", style.fontFamily)
        assertEquals(18f, style.fontSize)
        assertEquals(UnityFontWeight.BOLD, style.fontWeight)
        assertEquals("#FF0000", style.textColor)
        assertEquals(UnityTextAlign.CENTER, style.textAlign)
    }

    @Test
    fun `copy function creates new instance with modified values`() {
        // Given
        val original = UnityTextStyle()

        // When
        val modified = original.copy(
            textContent = "Modified",
            fontSize = 20f
        )

        // Then
        assertEquals("Modified", modified.textContent)
        assertEquals(20f, modified.fontSize)
        assertEquals(original.fontFamily, modified.fontFamily)
        assertEquals(original.fontWeight, modified.fontWeight)
    }

    @Test
    fun `UnityTextStyle supports all font weights`() {
        // When
        val normalStyle = UnityTextStyle(fontWeight = UnityFontWeight.NORMAL)
        val boldStyle = UnityTextStyle(fontWeight = UnityFontWeight.BOLD)

        // Then
        assertEquals(UnityFontWeight.NORMAL, normalStyle.fontWeight)
        assertEquals(UnityFontWeight.BOLD, boldStyle.fontWeight)
    }

    @Test
    fun `UnityTextStyle supports all text alignments`() {
        // When
        val leftStyle = UnityTextStyle(textAlign = UnityTextAlign.LEFT)
        val centerStyle = UnityTextStyle(textAlign = UnityTextAlign.CENTER)
        val rightStyle = UnityTextStyle(textAlign = UnityTextAlign.RIGHT)

        // Then
        assertEquals(UnityTextAlign.LEFT, leftStyle.textAlign)
        assertEquals(UnityTextAlign.CENTER, centerStyle.textAlign)
        assertEquals(UnityTextAlign.RIGHT, rightStyle.textAlign)
    }

    @Test
    fun `UnityTextStyle handles empty text content`() {
        // When
        val style = UnityTextStyle(textContent = "")

        // Then
        assertEquals("", style.textContent)
    }

    @Test
    fun `UnityTextStyle handles different font sizes`() {
        // When
        val smallStyle = UnityTextStyle(fontSize = 10f)
        val mediumStyle = UnityTextStyle(fontSize = 14f)
        val largeStyle = UnityTextStyle(fontSize = 24f)

        // Then
        assertEquals(10f, smallStyle.fontSize)
        assertEquals(14f, mediumStyle.fontSize)
        assertEquals(24f, largeStyle.fontSize)
    }

    @Test
    fun `UnityTextStyle handles different color formats`() {
        // When
        val blackStyle = UnityTextStyle(textColor = "#000000")
        val whiteStyle = UnityTextStyle(textColor = "#FFFFFF")
        val redStyle = UnityTextStyle(textColor = "#FF0000")

        // Then
        assertEquals("#000000", blackStyle.textColor)
        assertEquals("#FFFFFF", whiteStyle.textColor)
        assertEquals("#FF0000", redStyle.textColor)
    }
}