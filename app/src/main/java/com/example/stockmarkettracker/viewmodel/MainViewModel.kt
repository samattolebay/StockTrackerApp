package com.example.stockmarkettracker.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.stockmarkettracker.database.MainRepository
import com.example.stockmarkettracker.database.Stock
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private var _isFavouriteShown = MutableLiveData<Boolean>()
    val isFavouriteShown: LiveData<Boolean>
        get() = _isFavouriteShown

    private var _stocks =
        repository.stocks.asLiveData() as MutableLiveData<List<Stock>>
    val stocks: LiveData<List<Stock>>
        get() = _stocks

    private var _favouriteStocks =
        repository.favouriteStocks.asLiveData() as MutableLiveData<List<Stock>>
    val favouriteStocks: LiveData<List<Stock>>
        get() = _favouriteStocks

    private var _searchedRequests = MutableLiveData<MutableList<String>>()
    val searchedRequests: LiveData<MutableList<String>>
        get() = _searchedRequests

    private var _popularRequests = listOf(
        "Apple",
        "Amazon",
        "Google",
        "Tesla",
        "Microsoft",
        "First Solar",
        "Alibaba",
        "Facebook",
        "Mastercard"
    )
    val popularRequests: List<String>
        get() = _popularRequests

    init {
        _isFavouriteShown.value = false
        viewModelScope.launch {
            repository.fetchStocks()
            Log.d("ViewModel", repository.s.joinToString())
        }
    }

    fun insertStock(stock: Stock) {
        viewModelScope.launch {
            Log.d("ViewModel", "Insert stock")
            repository.insertStock(stock)
        }
    }

    fun setPrices(ticker: String) {
        viewModelScope.launch {
            Log.d("ViewModel", "Set Prices")
            repository.setPrice(ticker)
        }
    }

    fun searchStocks(newText: String? = null) {
        Log.d("ViewModel", "Search stock")
        if (!newText.isNullOrEmpty()) {
            _searchedRequests.value = (_searchedRequests.value ?: mutableListOf()).apply {
                add(newText)
            }
        }
        viewModelScope.launch {
            val query = if (newText.isNullOrEmpty()) "%%" else "%$newText%"
            Log.d("ViewModel", "Search stock: $query")
            val result = repository.getSearchStocks(query)
            _stocks.value = result
            _favouriteStocks.value = result.filter { it.isFavourite }
        }
    }

    fun changeList() {
        _isFavouriteShown.value = isFavouriteShown.value != true
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