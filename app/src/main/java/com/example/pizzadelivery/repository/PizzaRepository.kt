package com.example.pizzadelivery.repository

import com.example.pizzadelivery.repository.network.PizzaFlavorsApi

class PizzaRepository constructor(private val pizzaFlavorsApi: PizzaFlavorsApi) {
    suspend fun getAllPizzaFlavors() = pizzaFlavorsApi.getFlavors()
}