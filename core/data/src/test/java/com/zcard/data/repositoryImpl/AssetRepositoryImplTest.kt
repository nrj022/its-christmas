package com.zcard.data.repositoryImpl

import com.zcard.database.dao.AssetDao
import com.zcard.database.entity.AssetEntity
import com.zcard.domain.enum.AssetType
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AssetRepositoryImplTest {

    private lateinit var assetDao: AssetDao
    private lateinit var repository: AssetRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        assetDao = mockk()
        repository = AssetRepositoryImpl(assetDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAssetsByType returns success with assets list`() = runTest {
        // Given
        val assetEntities = listOf(
            AssetEntity(
                assetId = 1L,
                assetType = "OBJECT",
                firebaseKey = null,
                unityKey = "unity/obj1",
                thumbnailKey = "thumb/obj1"
            ),
            AssetEntity(
                assetId = 2L,
                assetType = "OBJECT",
                firebaseKey = null,
                unityKey = "unity/obj2",
                thumbnailKey = "thumb/obj2"
            )
        )
        coEvery { assetDao.getByType("OBJECT") } returns assetEntities

        // When
        val result = repository.getAssetsByType(AssetType.OBJECT)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals(AssetType.OBJECT, result.getOrNull()?.get(0)?.assetType)
        coVerify { assetDao.getByType("OBJECT") }
    }

    @Test
    fun `getAssetsByType returns empty list when no assets found`() = runTest {
        // Given
        coEvery { assetDao.getByType("BACKGROUND") } returns emptyList()

        // When
        val result = repository.getAssetsByType(AssetType.BACKGROUND)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
        coVerify { assetDao.getByType("BACKGROUND") }
    }

    @Test
    fun `getAssetsByType returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { assetDao.getByType(any()) } throws exception

        // When
        val result = repository.getAssetsByType(AssetType.OBJECT)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getAssetById returns success with asset`() = runTest {
        // Given
        val assetEntity = AssetEntity(
            assetId = 1L,
            assetType = "OBJECT",
            firebaseKey = null,
            unityKey = "unity/obj1",
            thumbnailKey = "thumb/obj1"
        )
        coEvery { assetDao.getById(1L) } returns assetEntity

        // When
        val result = repository.getAssetById(1L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull()?.assetId)
        assertEquals("unity/obj1", result.getOrNull()?.unityKey)
        coVerify { assetDao.getById(1L) }
    }

    @Test
    fun `getAssetById returns failure when asset not found`() = runTest {
        // Given
        coEvery { assetDao.getById(999L) } throws NoSuchElementException("Asset not found")

        // When
        val result = repository.getAssetById(999L)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoSuchElementException)
    }

    @Test
    fun `getUnityKeyById returns success with unity key`() = runTest {
        // Given
        coEvery { assetDao.getUnityKeyById(1L) } returns "unity/path/to/asset"

        // When
        val result = repository.getUnityKeyById(1L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("unity/path/to/asset", result.getOrNull())
        coVerify { assetDao.getUnityKeyById(1L) }
    }

    @Test
    fun `getUnityKeyById returns failure when unity key is empty`() = runTest {
        // Given
        coEvery { assetDao.getUnityKeyById(1L) } returns ""

        // When
        val result = repository.getUnityKeyById(1L)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals("Invalid unityKey", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getUnityKeyById returns failure when dao throws exception`() = runTest {
        // Given
        coEvery { assetDao.getUnityKeyById(1L) } throws RuntimeException("Database error")

        // When
        val result = repository.getUnityKeyById(1L)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `getAssetsByType handles different asset types`() = runTest {
        // Given
        val decorationEntities = listOf(
            AssetEntity(
                assetId = 3L,
                assetType = "DECORATION",
                firebaseKey = null,
                unityKey = "unity/dec1",
                thumbnailKey = "thumb/dec1"
            )
        )
        coEvery { assetDao.getByType("DECORATION") } returns decorationEntities

        // When
        val result = repository.getAssetsByType(AssetType.DECORATION)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals(AssetType.DECORATION, result.getOrNull()?.get(0)?.assetType)
    }
}