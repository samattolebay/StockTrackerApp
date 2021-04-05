package com.example.stockmarkettracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
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

        initVm()
        initViews()
        observeVm()

        ApiClient.apiKey["token"] = API_KEY
    }

    private fun observeVm() {
        viewModel.stocks.observe(this, {
            if (stocksButtonClicked) {
                (binding.recyclerView.adapter as StocksAdapter).submitList(it)
            }
        })

        viewModel.favouriteStocks.observe(this, {
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

        viewModel.fetchStocks()
    }

    private fun initViews() {
        val redColor = getColorStateList(R.color.red)
        val greenColor = getColorStateList(R.color.green)
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
                { ticker -> viewModel.setPrices(ticker) },
                redColor,
                greenColor
            )

        binding.stocksText.setOnClickListener {
            if (!stocksButtonClicked) {
                stocksButtonClicked = true
                (binding.recyclerView.adapter as StocksAdapter).submitList(viewModel.stocks.value)
                setTextSize(binding.stocksText, 28)
                setTextSize(binding.favouriteText, 18)
                binding.stocksText.setTextColor(getColor(R.color.menu_selected_color))
                binding.favouriteText.setTextColor(getColor(R.color.menu_unselected_color))
            }
        }

        binding.favouriteText.setOnClickListener {
            if (stocksButtonClicked) {
                stocksButtonClicked = false
                (binding.recyclerView.adapter as StocksAdapter).submitList(viewModel.favouriteStocks.value)
                setTextSize(binding.stocksText, 18)
                setTextSize(binding.favouriteText, 28)
                binding.stocksText.setTextColor(getColor(R.color.menu_unselected_color))
                binding.favouriteText.setTextColor(getColor(R.color.menu_selected_color))
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchStocks(newText)
                return false
            }
        })
    }

    private fun setTextSize(text: TextView, size: Int) {
        text.textSize = size.toFloat()
    }
}