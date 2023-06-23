package com.example.pizzadelivery.data.sources

import com.example.pizzadelivery.data.PizzaFlavor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET

class PizzaRemoteDataSource(
    private val pizzaApi: PizzaApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Fetches the pizza list from the network and returns the result.
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchPizzaFlavors(): Response<List<PizzaFlavor>> =
        // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            pizzaApi.fetchPizzaFlavors()
        }
}

interface PizzaApi {
    @GET("pizzas.json")
    suspend fun fetchPizzaFlavors(): Response<List<PizzaFlavor>>
}
