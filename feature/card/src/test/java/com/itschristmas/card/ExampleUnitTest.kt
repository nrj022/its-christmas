package com.itschristmas.card

import app.cash.turbine.test
import com.itschristmas.card.cardeditor.CardEditorIntent
import com.itschristmas.card.cardeditor.CardEditorViewModel
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
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var assetRepository: AssetRepository
    private lateinit var cardRepository: CardRepository
    private lateinit var cardElementRepository: CardElementRepository
    private lateinit var viewModel: CardEditorViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        assetRepository = mockk()
        cardRepository = mockk()
        cardElementRepository = mockk()

        viewModel = CardEditorViewModel(assetRepository, cardElementRepository, cardRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `handleInit sets initial state correctly`() = runTest {
        val cardId = 1L
        val card = Card(cardId = cardId, backgroundAssetId = 100L, createdAt = 0L, updatedAt = 0L)
        val objects = listOf(
            Asset(assetId = 1, unityKey = "obj1", assetType = AssetType.OBJECT, thumbnailKey = "thumb1"),
            Asset(assetId = 2, unityKey = "obj2", assetType = AssetType.OBJECT, thumbnailKey = "thumb2")
        )
        val backgrounds = listOf(
            Asset(assetId = 10, unityKey = "bg1", assetType = AssetType.BACKGROUND, thumbnailKey = "bgthumb")
        )
        val cardElement = CardElement(elementId = 1, cardId = cardId, assetId = 1, elementType = ElementType.OBJECT, posX = 0, posY = 0, scale = 1)

        val cardElementsWithAssetKeys = listOf(
            CardElementWithAssetKeys(
                cardElement = cardElement,
                unityKey = "obj1",
                thumbnailKey = "thumb1"
            )
        )

        coEvery { cardRepository.getCardById(cardId) } returns Result.success(card)
        coEvery { assetRepository.getAssetsByType(AssetType.OBJECT) } returns Result.success(objects)
        coEvery { assetRepository.getAssetsByType(AssetType.BACKGROUND) } returns Result.success(backgrounds)
        coEvery { cardElementRepository.getObjectElementsWithAssetKeysByCardId(cardId) } returns flowOf(Result.success(cardElementsWithAssetKeys))
        coEvery { assetRepository.getAssetById(1) } returns Result.success(objects[0])

        viewModel.onIntent(CardEditorIntent.Init(cardId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.cardEditorState.test {
            awaitItem()
            awaitItem()
            val finalState = awaitItem()
            assertEquals(100L, finalState.selectedBackground)
            assertEquals(objects, finalState.objects)
            assertEquals(backgrounds, finalState.backgrounds)
            assertEquals(cardElementsWithAssetKeys, finalState.myObjects)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleBackgroundClicked toggles selection`() = runTest {
        val assetId = 10L
        viewModel.onIntent(CardEditorIntent.BackgroundClicked(assetId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertEquals(assetId, state.selectedBackground)
            cancelAndIgnoreRemainingEvents()
        }

        // 클릭 시 해제되는지 확인
        viewModel.onIntent(CardEditorIntent.BackgroundClicked(assetId))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.cardEditorState.test {
            val state = awaitItem()
            assertEquals(null, state.selectedBackground)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleObjectClicked inserts CardElement`() = runTest {
        val cardId = 1L
        val clickedObject = Asset(assetId = 1, unityKey = "obj1", assetType = AssetType.OBJECT, thumbnailKey = "")

        coEvery { cardElementRepository.insertCardElement(any()) } returns Result.success(123L)

        viewModel.onIntent(CardEditorIntent.ObjectClicked(cardId, clickedObject))
        testDispatcher.scheduler.advanceUntilIdle()

        // suspend 함수가 실제로 호출되었는지 검증
        coVerify {
            cardElementRepository.insertCardElement(match {
                it.cardId == cardId && it.assetId == clickedObject.assetId && it.elementType == ElementType.OBJECT
            })
        }
    }
}
