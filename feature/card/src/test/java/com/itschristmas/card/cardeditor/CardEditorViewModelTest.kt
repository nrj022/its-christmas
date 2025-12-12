package com.itschristmas.card.cardeditor

import app.cash.turbine.test
import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.Card
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.repository.AssetRepository
import com.itschristmas.domain.repository.CardElementRepository
import com.itschristmas.domain.repository.CardRepository
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CardEditorViewModelTest {

    private lateinit var assetRepository: AssetRepository
    private lateinit var cardElementRepository: CardElementRepository
    private lateinit var cardRepository: CardRepository
    private lateinit var viewModel: CardEditorViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        assetRepository = mockk()
        cardElementRepository = mockk(relaxed = true)
        cardRepository = mockk()
        viewModel = CardEditorViewModel(assetRepository, cardElementRepository, cardRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onIntent Init loads card data and assets`() = runTest {
        // Given
        val card = Card(
            cardId = 1L,
            backgroundAssetId = 10L,
            createdAt = 0L,
            updatedAt = 0L
        )
        val objects = listOf(
            Asset(assetId = 1L, assetType = AssetType.OBJECT, unityKey = "obj1", thumbnailKey = "thumb1")
        )
        val backgrounds = listOf(
            Asset(assetId = 10L, assetType = AssetType.BACKGROUND, unityKey = "bg1", thumbnailKey = "bgthumb1")
        )
        val spawnedObjects = listOf(
            CardElementWithAssetKeys(
                cardElement = CardElement(
                    elementId = 1L,
                    cardId = 1L,
                    assetId = 1L,
                    elementType = ElementType.OBJECT,
                    posX = 0,
                    posY = 0
                ),
                unityKey = "obj1",
                thumbnailKey = "thumb1"
            )
        )

        coEvery { cardRepository.getCardById(1L) } returns Result.success(card)
        coEvery { assetRepository.getAssetsByType(AssetType.OBJECT) } returns Result.success(objects)
        coEvery { assetRepository.getAssetsByType(AssetType.BACKGROUND) } returns Result.success(backgrounds)
        every { cardElementRepository.getObjectElementsWithAssetKeysByCardId(1L) } returns 
            flowOf(Result.success(spawnedObjects))

        // When
        viewModel.onIntent(CardEditorIntent.Init(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertEquals(10L, state.selectedBackgroundId)
            assertEquals(1, state.objects.size)
            assertEquals(1, state.backgrounds.size)
            assertEquals(1, state.spawnedObjects.size)
        }
    }

    @Test
    fun `onIntent ObjectClicked inserts new card element`() = runTest {
        // Given
        val asset = Asset(
            assetId = 5L,
            assetType = AssetType.OBJECT,
            unityKey = "unity/obj",
            thumbnailKey = "thumb/obj"
        )
        coEvery { cardElementRepository.insertCardElement(any()) } returns Result.success(1L)

        // When
        viewModel.onIntent(CardEditorIntent.CreateObject(cardId = 1L, clickedObject = asset))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            cardElementRepository.insertCardElement(
                match {
                    it.cardId == 1L &&
                    it.assetId == 5L &&
                    it.elementType == ElementType.OBJECT &&
                    it.posX == 0 &&
                    it.posY == 0 &&
                    it.scale == 1
                }
            )
        }
    }

    @Test
    fun `onIntent BackgroundClicked toggles background selection`() = runTest {
        // When
        viewModel.onIntent(CardEditorIntent.ChangeBackground(assetId = 5L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertEquals(5L, state.selectedBackgroundId)
        }

        // When - Click again to deselect
        viewModel.onIntent(CardEditorIntent.ChangeBackground(assetId = 5L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertNull(state.selectedBackgroundId)
        }
    }

    @Test
    fun `onIntent SpawnedObjectClicked toggles object selection`() = runTest {
        // When
        viewModel.onIntent(CardEditorIntent.SelectSpawnedObject(assetId = 10L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertEquals(10L, state.selectedSpawnedObject)
        }

        // When - Click again to deselect
        viewModel.onIntent(CardEditorIntent.SelectSpawnedObject(assetId = 10L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertNull(state.selectedSpawnedObject)
        }
    }

    @Test
    fun `Init handles empty asset lists gracefully`() = runTest {
        // Given
        val card = Card(cardId = 1L, backgroundAssetId = 1L, createdAt = 0L, updatedAt = 0L)
        coEvery { cardRepository.getCardById(1L) } returns Result.success(card)
        coEvery { assetRepository.getAssetsByType(any()) } returns Result.success(emptyList())
        every { cardElementRepository.getObjectElementsWithAssetKeysByCardId(1L) } returns 
            flowOf(Result.success(emptyList()))

        // When
        viewModel.onIntent(CardEditorIntent.Init(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertEquals(0, state.objects.size)
            assertEquals(0, state.backgrounds.size)
            assertEquals(0, state.spawnedObjects.size)
        }
    }

    @Test
    fun `Init handles repository failures gracefully`() = runTest {
        // Given
        coEvery { cardRepository.getCardById(1L) } returns Result.failure(RuntimeException("Error"))
        coEvery { assetRepository.getAssetsByType(any()) } returns Result.failure(RuntimeException("Error"))
        every { cardElementRepository.getObjectElementsWithAssetKeysByCardId(1L) } returns 
            flowOf(Result.failure(RuntimeException("Error")))

        // When
        viewModel.onIntent(CardEditorIntent.Init(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            // State should remain in initial state
            assertEquals(0, state.objects.size)
            assertEquals(0, state.backgrounds.size)
            assertEquals(0, state.spawnedObjects.size)
        }
    }

    @Test
    fun `spawnedObject flow updates state when new elements are added`() = runTest {
        // Given
        val initialElements = listOf(
            CardElementWithAssetKeys(
                cardElement = CardElement(
                    elementId = 1L,
                    cardId = 1L,
                    assetId = 1L,
                    elementType = ElementType.OBJECT,
                    posX = 0,
                    posY = 0
                ),
                unityKey = "obj1",
                thumbnailKey = "thumb1"
            )
        )
        every { cardElementRepository.getObjectElementsWithAssetKeysByCardId(1L) } returns 
            flowOf(Result.success(initialElements))

        // When
        viewModel.onIntent(CardEditorIntent.Init(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertEquals(1, state.spawnedObjects.size)
            assertEquals(1L, state.spawnedObjects[0].cardElement.elementId)
        }
    }

    @Test
    fun `ObjectClicked with different assets adds multiple elements`() = runTest {
        // Given
        val asset1 = Asset(assetId = 1L, assetType = AssetType.OBJECT, unityKey = "obj1", thumbnailKey = "thumb1")
        val asset2 = Asset(assetId = 2L, assetType = AssetType.OBJECT, unityKey = "obj2", thumbnailKey = "thumb2")
        coEvery { cardElementRepository.insertCardElement(any()) } returns Result.success(1L)

        // When
        viewModel.onIntent(CardEditorIntent.CreateObject(cardId = 1L, clickedObject = asset1))
        viewModel.onIntent(CardEditorIntent.CreateObject(cardId = 1L, clickedObject = asset2))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { cardElementRepository.insertCardElement(any()) }
    }
}