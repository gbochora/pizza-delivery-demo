package com.example.pizzadelivery.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzadelivery.R
import com.example.pizzadelivery.domain.PizzaFlavor

class MenuAdapter() : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private var itemsList = mutableListOf<PizzaFlavor>()

    fun setPizzaFlavors(flavors: List<PizzaFlavor>) {
        this.itemsList = flavors.toMutableList()
        notifyDataSetChanged()
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pizza_flavor_view, parent, false)
        return MenuViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = itemsList[position]
        holder.textView.text = item.name
        holder.priceView.text = item.price.toString()
        holder.addButton.text = "ADD"
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return itemsList.size
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.nameView)
        val priceView: TextView = itemView.findViewById(R.id.priceView)
        val addButton: Button = itemView.findViewById(R.id.addRemove)
    }
}

