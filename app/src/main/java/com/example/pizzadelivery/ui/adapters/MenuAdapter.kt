package com.example.pizzadelivery.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzadelivery.R
import com.example.pizzadelivery.data.PizzaFlavor
import com.example.pizzadelivery.ui.activities.PizzaItemClickListener

class MenuAdapter(private val pizzaItemClickListener: PizzaItemClickListener) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>(), OnClickListener {
    private var itemsList = mutableListOf<PizzaFlavor>()
    private val selectedItems = mutableSetOf<Int>()

    // sets new list of flavors
    fun setPizzaFlavors(flavors: List<PizzaFlavor>) {
        this.itemsList = flavors.toMutableList()
        notifyDataSetChanged()
    }


    // updates order selection
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

        return MenuViewHolder(view, this)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(itemsList[position], position, selectedItems.contains(position))
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return itemsList.size
    }

    class MenuViewHolder(itemView: View, onClick: OnClickListener) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.nameView)
        private val priceView: TextView = itemView.findViewById(R.id.priceView)
        private val addButton: Button = itemView.findViewById(R.id.addRemove)

        init {
            addButton.setOnClickListener(onClick)
        }

        fun bind(item: PizzaFlavor, position: Int, selected: Boolean) {
            textView.text = item.name
            priceView.text = "%.2f".format(item.price)
            val buttonLabelId = if (selected) R.string.remove_button else R.string.add_button
            addButton.text = textView.context.resources.getString(buttonLabelId)
            addButton.tag = position
        }
    }

    override fun onClick(view: View?) {
        val position = view?.tag as Int
        if (selectedItems.contains(position)) {
            pizzaItemClickListener.onRemoveClick(position)
        } else {
            pizzaItemClickListener.onAddClick(position)
        }
    }
}

