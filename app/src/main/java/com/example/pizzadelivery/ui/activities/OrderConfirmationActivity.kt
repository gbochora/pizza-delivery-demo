package com.example.pizzadelivery.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzadelivery.R


const val EXTRA_ORDER_INFO = "EXTRA_ORDER_INFO"
const val EXTRA_ORDER_PRICE = "EXTRA_ORDER_PRICE"

class OrderConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val mIntent = intent
        val description = mIntent.getStringExtra(EXTRA_ORDER_INFO)
        val price = mIntent.getFloatExtra(EXTRA_ORDER_PRICE, 0f)

        val descriptionView = findViewById<TextView>(R.id.orderInfoView)
        descriptionView.text = description

        val priceView = findViewById<TextView>(R.id.orderPriceInfo)
        priceView.text = "%.2f".format(price)
    }

    fun onBackToMenu(view : View) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
