package com.example.stockmarkettracker.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stockmarkettracker.STOCK
import com.example.stockmarkettracker.database.Stock
import com.example.stockmarkettracker.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.infoStockText.text = intent.getParcelableExtra<Stock>(STOCK).toString()

    }
}