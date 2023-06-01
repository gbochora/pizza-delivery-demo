package com.example.pizzadelivery.ui.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzadelivery.domain.PizzaFlavor
import com.example.pizzadelivery.repository.PizzaRepository
import kotlinx.coroutines.*

class MenuViewModel constructor(private val mainRepository: PizzaRepository) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val flavorsList = MutableLiveData<List<PizzaFlavor>>()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun getAllPizzaFlavors() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getAllPizzaFlavors()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    flavorsList.postValue(response.body())
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }

    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}