package com.example.pizzadelivery.ui.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzadelivery.R
import com.example.pizzadelivery.domain.PizzaFlavor

class MenuAdapter(private val onAddListener: OnClickListener) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private var itemsList = mutableListOf<PizzaFlavor>()
    private val selectedItems = mutableSetOf<Int>()

    fun setPizzaFlavors(flavors: List<PizzaFlavor>) {
        this.itemsList = flavors.toMutableList()
        notifyDataSetChanged()
    }

    fun setSelectedItems(selectedItemsIndexes : List<Int>) {
        selectedItems.clear()
        for (index in selectedItemsIndexes) {
            selectedItems.add(index)
        }
        notifyDataSetChanged()
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pizza_flavor_view, parent, false)
        val holder = MenuViewHolder(view)

        // set onClick listener to the ADD button
        holder.addButton.setOnClickListener(onAddListener)
        return holder
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = itemsList[position]
        holder.textView.text = item.name
        holder.priceView.text = item.price.toString()
        val buttonLabelId = if (selectedItems.contains(position)) R.string.remove_button else R.string.add_button
        holder.addButton.text = holder.textView.context.resources.getString(buttonLabelId)
        holder.addButton.tag = position
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

