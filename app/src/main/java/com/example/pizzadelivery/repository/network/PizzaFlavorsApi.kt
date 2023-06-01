package com.example.pizzadelivery.repository.network

import com.example.pizzadelivery.domain.PizzaFlavor
import retrofit2.Response
import retrofit2.http.GET

interface PizzaFlavorsApi {
    @GET("pizzas.json")
    suspend fun getFlavors(): Response<List<PizzaFlavor>>
}