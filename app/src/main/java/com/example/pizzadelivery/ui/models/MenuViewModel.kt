package com.example.pizzadelivery.ui.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzadelivery.R
import com.example.pizzadelivery.domain.PizzaFlavor
import com.example.pizzadelivery.repository.PizzaRepository
import kotlinx.coroutines.*

const val MAX_FLAVORS_NUM = 2

class MenuViewModel constructor(private val mainRepository: PizzaRepository) : ViewModel() {
    val orderConfirmation = MutableLiveData<OrderInfo>()
    val order = MutableLiveData<OrderInfo>()
    val error = MutableLiveData<Int>()
    val flavorsList = MutableLiveData<List<PizzaFlavor>>()
    var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
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

    fun confirmOrder() {
        val orderInfo = order.value ?: OrderInfo(emptyList(), 0f, "")
        if (orderInfo.selectedFlavors.isNullOrEmpty()) {
            error.value = R.string.order_empty_msg
            return
        }
        orderConfirmation.value = order.value
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    private fun removeFlavorFromOrder(index: Int, orderInfo: OrderInfo) : OrderInfo {
        val newList = orderInfo.selectedFlavors.toMutableList()
        newList.remove(index)
        return OrderInfo(newList, calcOrderPrice(newList), constructOrderDescription(newList))
    }

    private fun addFlavorToOrder(index: Int, orderInfo: OrderInfo) : OrderInfo {
        val newList = orderInfo.selectedFlavors.toMutableList()
        newList.add(index)
        return OrderInfo(newList, calcOrderPrice(newList), constructOrderDescription(newList))
    }

    private fun calcOrderPrice(selectedFlavors: List<Int>) : Float {
        var total = 0f
        for (pos in selectedFlavors) {
            total += flavorsList.value?.get(pos)!!.price / selectedFlavors.size
        }
        return total
    }

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