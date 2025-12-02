package com.itschristmas.data.repositoryImpl

import com.itschristmas.database.dao.CardDao
import com.itschristmas.database.dto.GlbAndBgFirebaseKeyDto
import com.itschristmas.database.entity.CardEntity
import com.itschristmas.domain.model.Card
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CardRepositoryImplTest {

    private lateinit var cardDao: CardDao
    private lateinit var repository: CardRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cardDao = mockk()
        repository = CardRepositoryImpl(cardDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertCard returns success with inserted card ID`() = runTest {
        // Given
        val card = Card(
            cardId = 0L,
            title = "Test Card",
            glbKey = null,
            thumbnailPath = null,
            backgroundAssetId = 1L,
            isDraft = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { cardDao.insertCard(any()) } returns 1L

        // When
        val result = repository.insertCard(card)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        coVerify { cardDao.insertCard(any()) }
    }

    @Test
    fun `insertCard returns failure when dao throws exception`() = runTest {
        // Given
        val card = Card(
            cardId = 0L,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        coEvery { cardDao.insertCard(any()) } throws RuntimeException("Database error")

        // When
        val result = repository.insertCard(card)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `getCardById returns success with card`() = runTest {
        // Given
        val cardEntity = CardEntity(
            cardId = 1L,
            title = "Test Card",
            glbKey = "glb/key",
            thumbnailPath = "thumb/path",
            backgroundAssetId = 1L,
            isDraft = false,
            createdAt = 123456789L,
            updatedAt = 123456789L
        )
        coEvery { cardDao.getCardById(1L) } returns cardEntity

        // When
        val result = repository.getCardById(1L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull()?.cardId)
        assertEquals("Test Card", result.getOrNull()?.title)
        coVerify { cardDao.getCardById(1L) }
    }

    @Test
    fun `getCardById returns failure when card not found`() = runTest {
        // Given
        coEvery { cardDao.getCardById(999L) } throws NoSuchElementException("Card not found")

        // When
        val result = repository.getCardById(999L)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoSuchElementException)
    }

    @Test
    fun `getGlbAndBgFirebaseKey returns success with keys`() = runTest {
        // Given
        val dto = GlbAndBgFirebaseKeyDto(
            glbKey = "glb/model.glb",
            backgroundKey = "bg/image.jpg"
        )
        coEvery { cardDao.getGlbAndBgFirebaseKey(1L) } returns dto

        // When
        val result = repository.getGlbAndBgFirebaseKey(1L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("glb/model.glb", result.getOrNull()?.glbKey)
        assertEquals("bg/image.jpg", result.getOrNull()?.backgroundKey)
        coVerify { cardDao.getGlbAndBgFirebaseKey(1L) }
    }

    @Test
    fun `getGlbAndBgFirebaseKey returns success with null glbKey`() = runTest {
        // Given
        val dto = GlbAndBgFirebaseKeyDto(
            glbKey = null,
            backgroundKey = "bg/default.jpg"
        )
        coEvery { cardDao.getGlbAndBgFirebaseKey(1L) } returns dto

        // When
        val result = repository.getGlbAndBgFirebaseKey(1L)

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull()?.glbKey)
        assertEquals("bg/default.jpg", result.getOrNull()?.backgroundKey)
    }

    @Test
    fun `updateBackgroundAssetId returns success with updated count`() = runTest {
        // Given
        coEvery { cardDao.updateBackgroundAssetId(1L, 2L) } returns 1

        // When
        val result = repository.updateBackgroundAssetId(1L, 2L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        coVerify { cardDao.updateBackgroundAssetId(1L, 2L) }
    }

    @Test
    fun `updateBackgroundAssetId returns zero when no card updated`() = runTest {
        // Given
        coEvery { cardDao.updateBackgroundAssetId(999L, 2L) } returns 0

        // When
        val result = repository.updateBackgroundAssetId(999L, 2L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull())
    }

    @Test
    fun `updateGlbKey returns success with updated count`() = runTest {
        // Given
        coEvery { cardDao.updateGlbKey(1L, "new/glb/key.glb") } returns 1

        // When
        val result = repository.updateGlbKey(1L, "new/glb/key.glb")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        coVerify { cardDao.updateGlbKey(1L, "new/glb/key.glb") }
    }

    @Test
    fun `updateGlbKey returns failure when dao throws exception`() = runTest {
        // Given
        coEvery { cardDao.updateGlbKey(any(), any()) } throws RuntimeException("Database error")

        // When
        val result = repository.updateGlbKey(1L, "glb/key")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `updateGlbKey handles empty string key`() = runTest {
        // Given
        coEvery { cardDao.updateGlbKey(1L, "") } returns 1

        // When
        val result = repository.updateGlbKey(1L, "")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }
}