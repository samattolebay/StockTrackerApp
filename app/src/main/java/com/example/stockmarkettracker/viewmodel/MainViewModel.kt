package com.example.stockmarkettracker.viewmodel

import androidx.lifecycle.*
import com.example.stockmarkettracker.database.MainRepository
import com.example.stockmarkettracker.database.Stock
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {
    private var _stocks =
        repository.stocks.asLiveData() as MutableLiveData<List<Stock>>
    val stocks: LiveData<List<Stock>>
        get() = _stocks

    private var _favouriteStocks =
        repository.favouriteStocks.asLiveData() as MutableLiveData<List<Stock>>
    val favouriteStocks: LiveData<List<Stock>>
        get() = _favouriteStocks

    fun fetchStocks() {
        viewModelScope.launch {
            repository.fetchStocks()
        }
    }

    fun insertStock(stock: Stock) {
        viewModelScope.launch {
            repository.insertStock(stock)
        }
    }

    fun setPrices(ticker: String) {
        viewModelScope.launch {
            repository.setPrice(ticker)
        }
    }

    fun searchStocks(newText: String?) {
        viewModelScope.launch {
            val query = if (newText == null) "%%" else "%$newText%"
            val result = repository.getSearchStocks(query)
            _stocks.value = result
            _favouriteStocks.value = result.filter { it.isFavourite }
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