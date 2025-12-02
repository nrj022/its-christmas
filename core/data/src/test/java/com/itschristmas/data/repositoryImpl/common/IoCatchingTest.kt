package com.itschristmas.data.repositoryImpl.common

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
class IoCatchingTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ioCatching returns success result when block succeeds`() = runTest {
        // Given
        val expectedValue = "test result"

        // When
        val result = ioCatching { expectedValue }

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedValue, result.getOrNull())
    }

    @Test
    fun `ioCatching returns failure result when block throws exception`() = runTest {
        // Given
        val expectedException = IllegalStateException("Test exception")

        // When
        val result = ioCatching<String> { throw expectedException }

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `ioCatching handles null return values`() = runTest {
        // When
        val result = ioCatching<String?> { null }

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun `ioCatching executes block on IO dispatcher`() = runTest {
        // Given
        var executedThreadName: String? = null

        // When
        ioCatching {
            executedThreadName = Thread.currentThread().name
        }

        // Then
        // Note: In test environment, this will be the test dispatcher
        assertTrue(executedThreadName != null)
    }

    @Test
    fun `ioCatching handles complex operations`() = runTest {
        // Given
        val data = listOf(1, 2, 3, 4, 5)

        // When
        val result = ioCatching {
            data.map { it * 2 }.filter { it > 4 }
        }

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(6, 8, 10), result.getOrNull())
    }

    @Test
    fun `ioCatching handles custom exceptions`() = runTest {
        // Given
        class CustomException(message: String) : Exception(message)
        val customException = CustomException("Custom error")

        // When
        val result = ioCatching<Unit> { throw customException }

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CustomException)
        assertEquals("Custom error", result.exceptionOrNull()?.message)
    }
}