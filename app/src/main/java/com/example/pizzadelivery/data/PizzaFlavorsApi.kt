package com.example.pizzadelivery.data

import retrofit2.Response
import retrofit2.http.GET

interface PizzaFlavorsApi {
    @GET("pizzas.json")
    suspend fun getFlavors(): Response<List<PizzaFlavor>>
}