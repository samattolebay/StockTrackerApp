package com.example.stockmarkettracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stockmarkettracker.adapter.StocksAdapter
import com.example.stockmarkettracker.databinding.ActivityMainBinding
import com.example.stockmarkettracker.details.DetailsActivity
import com.example.stockmarkettracker.viewmodel.MainViewModel
import com.example.stockmarkettracker.viewmodel.MainViewModelFactory
import com.finnhub.api.infrastructure.ApiClient

const val STOCK = "Stock"
const val API_KEY = "c1g5qov48v6p69n8mnmg"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var stocksButtonClicked = true

    private lateinit var viewModel: MainViewModel

    private lateinit var showDetailsIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showDetailsIntent = Intent(this, DetailsActivity::class.java)

        initViews()
        initVm()
        observeVm()

        ApiClient.apiKey["token"] = API_KEY
    }

    private fun observeVm() {
        viewModel.stocks.observe(this, Observer {
            if (stocksButtonClicked) {
                (binding.recyclerView.adapter as StocksAdapter).submitList(it)
            }
        })

        viewModel.favouriteStocks.observe(this, Observer {
            if (!stocksButtonClicked) {
                (binding.recyclerView.adapter as StocksAdapter).submitList(it)
            }
        })
    }

    private fun initVm() {
        val app = application as MyApplication
        val repo = app.repository
        val factory = MainViewModelFactory(repo)

        viewModel = ViewModelProvider(this, factory)
            .get(MainViewModel::class.java)
    }

    private fun initViews() {
        binding.recyclerView.adapter =
            StocksAdapter(
                { stock ->
                    startActivity(showDetailsIntent.apply {
                        putExtra(
                            STOCK,
                            stock
                        )
                    })
                },
                { stock -> viewModel.insertStock(stock) },
                resources
            )

        binding.stocksButton.setOnClickListener {
            if (!stocksButtonClicked) {
                stocksButtonClicked = true
                (binding.recyclerView.adapter as StocksAdapter).submitList(viewModel.stocks.value)
            }
        }

        binding.favouriteButton.setOnClickListener {
            if (stocksButtonClicked) {
                stocksButtonClicked = false
                (binding.recyclerView.adapter as StocksAdapter).submitList(viewModel.favouriteStocks.value)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            viewModel.fetchStock()
        }

        binding.floatingActionButton2.setOnClickListener {
            viewModel.deleteStocks()
        }
    }
}