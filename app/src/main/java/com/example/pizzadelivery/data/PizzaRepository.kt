package com.example.pizzadelivery.data

import com.example.pizzadelivery.data.sources.PizzaRemoteDataSource
import retrofit2.Response

interface PizzaRepository {
    suspend fun fetchPizzaFlavors() : Response<List<PizzaFlavor>>
}