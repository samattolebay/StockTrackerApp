package com.example.stockmarkettracker.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.stockmarkettracker.database.MainRepository
import com.example.stockmarkettracker.database.Stock
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {
    private val _stocks = repository.stocks.asLiveData()
    val stocks: LiveData<List<Stock>>
        get() = _stocks

    private val _favouriteStocks = repository.favouriteStocks.asLiveData()
    val favouriteStocks: LiveData<List<Stock>>
        get() = _favouriteStocks

    fun fetchStock() {
        Log.d("MainViewModel", "Launch coroutine to fetch Stock")
        viewModelScope.launch {
            repository.fetchStocks("MSFT") // AAPL, MSFT, YNDX
        }
    }

    fun insertStock(stock: Stock) {
        Log.d("MainViewModel", "Launch coroutine to insert Stock")
        viewModelScope.launch {
            repository.insertStock(stock)
        }
    }

    fun deleteStocks() {
        viewModelScope.launch {
            repository.deleteStocks()
        }
    }
}

class MainViewModelFactory(private val repository: MainRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}