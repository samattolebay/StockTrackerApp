package com.example.stockmarkettracker

import android.app.Application
import com.example.stockmarkettracker.database.MainRepository
import com.example.stockmarkettracker.database.StockDatabase
import com.finnhub.api.apis.DefaultApi

class MyApplication : Application() {
    private val database by lazy { StockDatabase.getDatabase(this) }
    private val apiClient by lazy { DefaultApi() }
    val repository by lazy { MainRepository(database.stockDao(), apiClient) }
}