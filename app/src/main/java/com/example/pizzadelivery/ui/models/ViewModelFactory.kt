package com.example.pizzadelivery.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pizzadelivery.repository.PizzaRepository

class ViewModelFactory constructor(private val repository: PizzaRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            MenuViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("MenuViewModel Not Found")
        }
    }
}