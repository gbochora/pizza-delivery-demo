package com.example.pizzadelivery.ui.activities

import android.os.Bundle
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

class MenuActivity : AppCompatActivity() {
    private lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = PizzaRepository(retrofitService.create(PizzaFlavorsApi::class.java))
        viewModel = ViewModelProvider(this, ViewModelFactory(mainRepository))[MenuViewModel::class.java]

        val adapter = MenuAdapter()
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        recyclerview.adapter = adapter
        viewModel.flavorsList.observe(this) {
            adapter.setPizzaFlavors(it)
        }
        viewModel.getAllPizzaFlavors()
    }
}