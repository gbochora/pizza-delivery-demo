package com.example.pizzadelivery.ui.activities

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzadelivery.R
import com.example.pizzadelivery.repository.PizzaRepository
import com.example.pizzadelivery.repository.network.PizzaFlavorsApi
import com.example.pizzadelivery.repository.network.RetrofitService
import com.example.pizzadelivery.ui.adapters.MenuAdapter
import com.example.pizzadelivery.ui.models.MenuViewModel
import com.example.pizzadelivery.ui.models.ViewModelFactory

class MenuActivity : AppCompatActivity(), OnClickListener {
    private lateinit var menuViewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val totalPriceView = findViewById<TextView>(R.id.totalAmountView)
        val orderDescriptionView = findViewById<TextView>(R.id.orderDescriptionView)

        // set 0 for empty card
        totalPriceView.text = resources.getString(R.string.total, 0f)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = PizzaRepository(retrofitService.create(PizzaFlavorsApi::class.java))
        menuViewModel = ViewModelProvider(this, ViewModelFactory(mainRepository))[MenuViewModel::class.java]

        val adapter = MenuAdapter(this)
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        recyclerview.adapter = adapter
        menuViewModel.flavorsList.observe(this) {
            adapter.setPizzaFlavors(it)
        }
        menuViewModel.error.observe(this) {
            Toast.makeText(this, resources.getString(it), Toast.LENGTH_LONG).show()
        }
        menuViewModel.order.observe(this) {
            totalPriceView.text = resources.getString(R.string.total, it.totalPrice)
            orderDescriptionView.text = it.description
            adapter.setSelectedItems(it.selectedFlavors)
        }
        menuViewModel.getAllPizzaFlavors()
    }

    override fun onClick(view: View) {
        val itemIndex = view.tag as Int
        menuViewModel.addRemoveFlavorToOrder(itemIndex)
    }
}