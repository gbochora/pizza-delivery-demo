package com.example.pizzadelivery.ui.activities

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzadelivery.R
import com.example.pizzadelivery.data.PizzaRepositoryDefault
import com.example.pizzadelivery.data.RetrofitService
import com.example.pizzadelivery.data.sources.PizzaApi
import com.example.pizzadelivery.data.sources.PizzaRemoteDataSource
import com.example.pizzadelivery.ui.adapters.MenuAdapter
import com.example.pizzadelivery.ui.models.MenuViewModel
import com.example.pizzadelivery.ui.models.ViewModelFactory
import kotlinx.coroutines.Dispatchers


class MenuActivity : AppCompatActivity(), PizzaItemClickListener {
    private lateinit var menuViewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // getting order total price and order description views by its id
        val totalPriceView = findViewById<TextView>(R.id.totalAmountView)
        val orderDescriptionView = findViewById<TextView>(R.id.orderDescriptionView)

        // set total price 0 for empty card
        totalPriceView.text = resources.getString(R.string.total, 0f)

        // init retrofit service, repository and view model
        val retrofitService = RetrofitService.getInstance()

        val pizzaRemoteDataSource = PizzaRemoteDataSource(retrofitService.create(PizzaApi::class.java), Dispatchers.IO)
        val mainRepository = PizzaRepositoryDefault(pizzaRemoteDataSource)
        menuViewModel = ViewModelProvider(this, ViewModelFactory(mainRepository))[MenuViewModel::class.java]

        val adapter = MenuAdapter(this)
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        // update ui on state change
        menuViewModel.uiState.observe(this) {
            adapter.setPizzaFlavors(it.pizzaList)

            // notify user about errors
            if (it.errorMessage != Resources.ID_NULL) {
                Toast.makeText(this, resources.getString(it.errorMessage), Toast.LENGTH_LONG).show()
                menuViewModel.errorMessageDisplayed()
            }

            // update order information on change
            adapter.setPizzaFlavors(it.pizzaList)
            totalPriceView.text = resources.getString(R.string.total, it.orderUiState.totalPrice)
            orderDescriptionView.text = it.orderUiState.description
            adapter.setSelectedItems(it.orderUiState.selectedFlavors)

            // show confirmation screen on order confirm
            if (it.orderUiState.confirmOrder) {
                val intent = Intent(this, OrderConfirmationActivity::class.java)
                intent.putExtra(EXTRA_ORDER_INFO, it.orderUiState.description)
                intent.putExtra(EXTRA_ORDER_PRICE, it.orderUiState.totalPrice)
                startActivity(intent)
            }
        }
        if (savedInstanceState == null) {
            // fetch pizza flavors list
            menuViewModel.fetchAllPizzaFlavors()
        }

    }

    fun View.onConfirm() {
        menuViewModel.confirmOrder()
    }

    override fun onAddClick(position: Int) {
        menuViewModel.addPizzaFlavorToOrder(position)
    }

    override fun onRemoveClick(position: Int) {
        menuViewModel.removePizzaFlavorToOrder(position)
    }
}

interface PizzaItemClickListener {
    fun onAddClick(position: Int)
    fun onRemoveClick(position: Int)
}