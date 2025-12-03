package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.common.toBase62
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.math.pow
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CardEditorUtilsTest {
    @Test
    fun `Converting the max 32bit integer to base62 should return CVUcbB`() = runTest {
        val longNum = (2.0.pow(31)-1).toLong()
        assertEquals(toBase62(longNum), "CVUmlB")
    }
}