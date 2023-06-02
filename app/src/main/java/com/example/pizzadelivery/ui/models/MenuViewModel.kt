package com.example.pizzadelivery.ui.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzadelivery.R
import com.example.pizzadelivery.domain.PizzaFlavor
import com.example.pizzadelivery.repository.PizzaRepository
import kotlinx.coroutines.*

const val MAX_FLAVORS_NUM = 2

class MenuViewModel constructor(private val mainRepository: PizzaRepository) : ViewModel() {
    // to handle order confirmation
    val orderConfirmation = MutableLiveData<OrderInfo>()
    val order = MutableLiveData<OrderInfo>()
    val error = MutableLiveData<Int>()
    val flavorsList = MutableLiveData<List<PizzaFlavor>>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        error.value = R.string.network_err_msg
    }

    fun getAllPizzaFlavors() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getAllPizzaFlavors()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    flavorsList.postValue(response.body())
                } else {
                    error.value = R.string.network_err_msg
                }
            }
        }
    }


    // adds a pizza flavor to the order by its position number if the flavor is not already added.
    // or removes the flavor if it's already in the order.
    fun addRemoveFlavorToOrder(index : Int) {
        val orderInfo = order.value ?: OrderInfo(emptyList(), 0f, "")

        // if flavor already selected, remove it from order
        if (orderInfo.selectedFlavors.contains(index)) {
            order.value = removeFlavorFromOrder(index, orderInfo)
            return
        }

        // can't add flavor reached max number
        if (orderInfo.selectedFlavors.size >= MAX_FLAVORS_NUM) {
            error.value = R.string.max_flavors_limit_msg
            return
        }

        // add flavor to order
        order.value = addFlavorToOrder(index, orderInfo)
    }

    // confirms order if it's not empty
    fun confirmOrder() {
        val orderInfo = order.value ?: OrderInfo(emptyList(), 0f, "")
        if (orderInfo.selectedFlavors.isEmpty()) {
            error.value = R.string.order_empty_msg
            return
        }
        orderConfirmation.value = order.value
    }

    override fun onCleared() {
        super.onCleared()
        this.job?.cancel()
    }

    // removes the flavor from the order and returns new OrderInfo instance
    private fun removeFlavorFromOrder(index: Int, orderInfo: OrderInfo) : OrderInfo {
        val newList = orderInfo.selectedFlavors.toMutableList()
        newList.remove(index)
        return OrderInfo(newList, calcOrderPrice(newList), constructOrderDescription(newList))
    }

    // adds the flavor to the order and returns new OrderInfo instance
    private fun addFlavorToOrder(index: Int, orderInfo: OrderInfo) : OrderInfo {
        val newList = orderInfo.selectedFlavors.toMutableList()
        newList.add(index)
        return OrderInfo(newList, calcOrderPrice(newList), constructOrderDescription(newList))
    }

    // calculates the order total price based on flavors list
    private fun calcOrderPrice(selectedFlavors: List<Int>) : Float {
        var total = 0f
        for (pos in selectedFlavors) {
            total += flavorsList.value?.get(pos)!!.price / selectedFlavors.size
        }
        return total
    }

    // constructs the order description based on flavors list
    private fun constructOrderDescription(selectedFlavors: List<Int>) : String {
        var description = ""
        var partsLabel = ""
        if (selectedFlavors.size == 1) partsLabel = "1 "
        if (selectedFlavors.size == 2) partsLabel = "1/2 "

        for (pos in selectedFlavors) {
            description += partsLabel + flavorsList.value?.get(pos)!!.name + "\n"
        }
        return description
    }
}

data class OrderInfo (
    val selectedFlavors: List<Int>,
    val totalPrice: Float,
    val description: String
)