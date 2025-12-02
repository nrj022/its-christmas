package com.itschristmas.data.repositoryImpl

import app.cash.turbine.test
import com.itschristmas.database.dao.CardElementDao
import com.itschristmas.database.dto.CardElementWithAssetKeysDto
import com.itschristmas.database.entity.CardElementEntity
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.model.CardElement
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CardElementRepositoryImplTest {

    private lateinit var cardElementDao: CardElementDao
    private lateinit var repository: CardElementRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cardElementDao = mockk()
        repository = CardElementRepositoryImpl(cardElementDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertCardElements returns success with inserted IDs`() = runTest {
        // Given
        val cardElements = listOf(
            CardElement(
                elementId = 0L,
                cardId = 1L,
                assetId = 10L,
                elementType = ElementType.OBJECT,
                posX = 0,
                posY = 0
            )
        )
        coEvery { cardElementDao.insertAll(any()) } returns listOf(1L)

        // When
        val result = repository.insertCardElements(cardElements)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(1L), result.getOrNull())
        coVerify { cardElementDao.insertAll(any()) }
    }

    @Test
    fun `insertCardElement returns success with inserted ID`() = runTest {
        // Given
        val cardElement = CardElement(
            elementId = 0L,
            cardId = 1L,
            assetId = 10L,
            elementType = ElementType.OBJECT,
            posX = 5,
            posY = 10
        )
        coEvery { cardElementDao.insertElement(any()) } returns 1L

        // When
        val result = repository.insertCardElement(cardElement)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        coVerify { cardElementDao.insertElement(any()) }
    }

    @Test
    fun `deleteCardElementsByCardId returns success with deleted count`() = runTest {
        // Given
        coEvery { cardElementDao.deleteByCardId(1L) } returns 3

        // When
        val result = repository.deleteCardElementsByCardId(1L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull())
        coVerify { cardElementDao.deleteByCardId(1L) }
    }

    @Test
    fun `getObjectElementsWithAssetKeysByCardId emits success with elements`() = runTest {
        // Given
        val dto = CardElementWithAssetKeysDto(
            cardElement = CardElementEntity(
                elementId = 1L,
                cardId = 1L,
                assetId = 10L,
                elementType = "OBJECT",
                posX = 0,
                posY = 0
            ),
            unityKey = "unity/key",
            thumbnailKey = "thumb/key"
        )
        every { cardElementDao.getObjectElementsWithAssetKeysByCardId(1L) } returns flowOf(listOf(dto))

        // When/Then
        repository.getObjectElementsWithAssetKeysByCardId(1L).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("unity/key", result.getOrNull()?.get(0)?.unityKey)
            awaitComplete()
        }
    }

    @Test
    fun `getObjectElementsWithAssetKeysByCardId emits failure on error`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        every { cardElementDao.getObjectElementsWithAssetKeysByCardId(1L) } returns 
            kotlinx.coroutines.flow.flow { throw exception }

        // When/Then
        repository.getObjectElementsWithAssetKeysByCardId(1L).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `getTextElementsByCardId returns success with text elements`() = runTest {
        // Given
        val textEntity = CardElementEntity(
            elementId = 1L,
            cardId = 1L,
            assetId = null,
            elementType = "TEXT",
            posX = 0,
            posY = 0,
            textContent = "Test",
            fontFamily = "Arial",
            fontSize = 14f
        )
        coEvery { cardElementDao.getTextElementsByCardId(1L) } returns listOf(textEntity)

        // When
        val result = repository.getTextElementsByCardId(1L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals(ElementType.TEXT, result.getOrNull()?.get(0)?.elementType)
    }

    @Test
    fun `updateElementPosition returns success with updated count`() = runTest {
        // Given
        coEvery { cardElementDao.updateElementPosition(1L, 10, 20) } returns 1

        // When
        val result = repository.updateElementPosition(1L, 10, 20)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        coVerify { cardElementDao.updateElementPosition(1L, 10, 20) }
    }

    @Test
    fun `updateElementScale returns success with updated count`() = runTest {
        // Given
        coEvery { cardElementDao.updateElementScale(1L, 2) } returns 1

        // When
        val result = repository.updateElementScale(1L, 2)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }

    @Test
    fun `updateTextFontSize returns success with updated count`() = runTest {
        // Given
        coEvery { cardElementDao.updateTextFontSize(1L, 16f) } returns 1

        // When
        val result = repository.updateTextFontSize(1L, 16f)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }

    @Test
    fun `updateTextColor returns success with updated count`() = runTest {
        // Given
        coEvery { cardElementDao.updateTextColor(1L, "#FF0000") } returns 1

        // When
        val result = repository.updateTextColor(1L, "#FF0000")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }

    @Test
    fun `updateTextContent returns success with updated count`() = runTest {
        // Given
        coEvery { cardElementDao.updateTextContent(1L, "New Text") } returns 1

        // When
        val result = repository.updateTextContent(1L, "New Text")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }

    @Test
    fun `updateTextFont returns success with updated count`() = runTest {
        // Given
        coEvery { cardElementDao.updateTextFont(1L, "Times") } returns 1

        // When
        val result = repository.updateTextFont(1L, "Times")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }

    @Test
    fun `updateTextAlign returns success with updated count`() = runTest {
        // Given
        coEvery { cardElementDao.updateTextAlign(1L, "CENTER") } returns 1

        // When
        val result = repository.updateTextAlign(1L, "CENTER")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }

    @Test
    fun `update operations return failure on error`() = runTest {
        // Given
        coEvery { cardElementDao.updateElementPosition(any(), any(), any()) } throws RuntimeException("Error")

        // When
        val result = repository.updateElementPosition(1L, 10, 20)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}