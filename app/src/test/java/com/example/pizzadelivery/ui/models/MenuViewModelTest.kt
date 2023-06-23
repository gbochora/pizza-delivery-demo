package com.example.pizzadelivery.ui.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pizzadelivery.data.PizzaFlavor
import com.example.pizzadelivery.data.PizzaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    private val pizzaRepository: PizzaRepository = mockk<PizzaRepository>()
    private var viewModel = MenuViewModel(pizzaRepository)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatcher = newSingleThreadContext("Main Thread")

    @Before
    fun setUp() {
        // Set the test dispatcher as the main dispatcher
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `When fetching the pizza flavors list`() {
        val expected = listOf(
            PizzaFlavor("pizza1", 101f),
                                PizzaFlavor("pizza2", 110f),
                                PizzaFlavor("pizza3", 111f),
                                PizzaFlavor("pizza4", 120f),
                                PizzaFlavor("pizza5", 101f)
        )

        val latch = CountDownLatch(1)

        viewModel.flavorsList.observeForever {
            assertEquals(expected, it)
            latch.countDown() // Notify the latch that the value has been observed
        }

        coEvery { pizzaRepository.fetchPizzaFlavors() } returns Response.success(expected)
        viewModel.fetchAllPizzaFlavors()

        // Wait for the latch to be counted down or timeout after a specific duration
        val timeout = 2L
        val unit = TimeUnit.SECONDS
        val latchResult = latch.await(timeout, unit)

        assertEquals(true, latchResult) // Assert that the latch countdown occurred
    }
}