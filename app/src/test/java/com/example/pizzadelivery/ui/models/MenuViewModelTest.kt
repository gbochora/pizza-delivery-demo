package com.example.pizzadelivery.ui.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pizzadelivery.domain.PizzaFlavor
import com.example.pizzadelivery.repository.PizzaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    private val pizzaRepository: PizzaRepository = mockk<PizzaRepository>()
    private var viewModel = MenuViewModel(pizzaRepository)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the test dispatcher as the main dispatcher
        Dispatchers.setMain(dispatcher)
    }
    @Test
    fun `When fetching the pizza flavors list`() {
        val expected = listOf(PizzaFlavor("pizza1", 101f),
                                PizzaFlavor("pizza2", 110f),
                                PizzaFlavor("pizza3", 111f),
                                PizzaFlavor("pizza4", 120f),
                                PizzaFlavor("pizza5", 101f))

        val viewStates = mutableListOf<PizzaFlavor>()
        viewModel.flavorsList.observeForever {
            println("observeForever!!")
            viewStates.addAll(it)
        }

        coEvery { pizzaRepository.getAllPizzaFlavors() } returns Response.success(expected)
        viewModel.getAllPizzaFlavors()
        dispatcher.scheduler.advanceUntilIdle()

        println(viewModel.flavorsList.value.toString())
//        assertEquals(expected, viewModel.flavorsList.value)
    }
}