package com.example.pizzadelivery.ui.models

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzadelivery.R
import com.example.pizzadelivery.data.PizzaFlavor
import com.example.pizzadelivery.data.PizzaRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow

const val MAX_FLAVORS_NUM = 2

class MenuViewModel constructor(private val mainRepository: PizzaRepository) : ViewModel() {
    private val _uiState = MutableLiveData(PizzaMenuUiState())
    val uiState: LiveData<PizzaMenuUiState>
        get() = _uiState

    fun fetchAllPizzaFlavors() {
        viewModelScope.launch {
            val response = mainRepository.fetchPizzaFlavors()
            if (response.isSuccessful) {
                _uiState.setValue(PizzaMenuUiState(pizzaList = response.body() ?: emptyList()))
            } else {
                _uiState.setValue(PizzaMenuUiState(errorMessage = R.string.network_err_msg))
            }
        }
    }

    //TODO?: should we replace index on pizza name
    fun addPizzaFlavorToOrder(pizzaIndex: Int) {
        val menuUiState = _uiState.value ?: PizzaMenuUiState()

        //TODO?: add check if pizza flavor already in the list

        if (menuUiState.orderUiState.selectedFlavors.size >= MAX_FLAVORS_NUM) {
            _uiState.value = menuUiState.copy(errorMessage = R.string.max_flavors_limit_msg)
            return
        }

        val newList = menuUiState.orderUiState.selectedFlavors.toMutableList()
        newList.add(pizzaIndex)
        val totalPrice = calcOrderPrice(newList, menuUiState.pizzaList)
        val description = constructOrderDescription(newList, menuUiState.pizzaList)

        val newOrderState = menuUiState.orderUiState.copy(selectedFlavors = newList, totalPrice=totalPrice, description=description)
        _uiState.value = menuUiState.copy(orderUiState = newOrderState)
    }

    fun removePizzaFlavorToOrder(pizzaIndex: Int) {
        val menuUiState = _uiState.value ?: PizzaMenuUiState()

        if (!menuUiState.orderUiState.selectedFlavors.contains(pizzaIndex)) {
            _uiState.value = menuUiState.copy(errorMessage = R.string.no_pizza_in_order)
            return
        }

        val newList = menuUiState.orderUiState.selectedFlavors.toMutableList()
        newList.remove(pizzaIndex)
        val totalPrice = calcOrderPrice(newList, menuUiState.pizzaList)
        val description = constructOrderDescription(newList, menuUiState.pizzaList)

        val newOrderState = menuUiState.orderUiState.copy(selectedFlavors = newList, totalPrice=totalPrice, description=description)
        _uiState.value = menuUiState.copy(orderUiState = newOrderState)
    }

    // confirms order if it's not empty
    fun confirmOrder() {
        val menuUiState = _uiState.value ?: PizzaMenuUiState()
        if (menuUiState.orderUiState.selectedFlavors.isEmpty()) {
            _uiState.value = menuUiState.copy(errorMessage = R.string.order_empty_msg)
            return
        }
        val newOrderState = menuUiState.orderUiState.copy(confirmOrder = true)
        _uiState.value = menuUiState.copy(orderUiState = newOrderState)
    }

    fun errorMessageDisplayed() {
        val menuUiState = _uiState.value ?: PizzaMenuUiState()
        _uiState.value = menuUiState.copy(errorMessage = Resources.ID_NULL)
    }

    // calculates the order total price based on flavors list
    private fun calcOrderPrice(selectedFlavors: List<Int>, pizzaList: List<PizzaFlavor>) : Float {
        var total = 0f
        for (pos in selectedFlavors) {
            total += pizzaList[pos].price / selectedFlavors.size
        }
        return total
    }

    // constructs the order description based on flavors list
    private fun constructOrderDescription(selectedFlavors: List<Int>, pizzaList: List<PizzaFlavor>) : String {
        var description = ""
        var partsLabel = ""
        if (selectedFlavors.size == 1) partsLabel = "1 "
        if (selectedFlavors.size == 2) partsLabel = "1/2 "

        for (pos in selectedFlavors) {
            description += partsLabel + pizzaList[pos].name + "\n"
        }
        return description
    }
}

data class PizzaMenuUiState(
    val pizzaList: List<PizzaFlavor> = listOf(),
    val orderUiState: OrderUiState = OrderUiState(),
    val errorMessage: Int = Resources.ID_NULL,
)

data class OrderUiState (
    val selectedFlavors: List<Int> = listOf(),
    val totalPrice: Float = 0f,
    val description: String = "",
    val confirmOrder: Boolean = false
)