package com.example.pizzadelivery.data

import com.example.pizzadelivery.data.sources.PizzaRemoteDataSource

class PizzaRepositoryDefault (
    private val pizzaRemoteSource: PizzaRemoteDataSource
) : PizzaRepository {
    override suspend fun fetchPizzaFlavors() = pizzaRemoteSource.fetchPizzaFlavors()
}