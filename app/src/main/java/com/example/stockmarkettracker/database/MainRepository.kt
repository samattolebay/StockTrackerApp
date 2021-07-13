package com.example.stockmarkettracker.database

import android.util.Log
import com.finnhub.api.apis.DefaultApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(
    private val stockDao: StockDao,
    private val finnhubApi: DefaultApi,
    private val gSon: Gson,
    private val jsonFileString: String?
) {
    val stocks = stockDao.getStocks()
    val favouriteStocks = stockDao.getFavouriteStocks()
    val s = mutableSetOf<String>()

    suspend fun getSearchStocks(query: String): List<Stock> = stockDao.getSearchStocks(query)

    suspend fun fetchStocks() {
        withContext(Dispatchers.IO) {
            Log.d("Repository", "Fetch Stocks")
            val listStockType = object : TypeToken<List<Stock>>() {}.type

            val stocks: List<Stock> = gSon.fromJson(jsonFileString, listStockType)
            stocks.forEach {
                val stock = stockDao.getStock(it.ticker)
                if (stock != null) it.isFavourite = stock.isFavourite
                insertStock(it)
            }
        }
    }

    private fun calculateDayDelta(currentPrice: Float, previousPrice: Float): Float {
        return (currentPrice - previousPrice) / previousPrice
    }

    suspend fun insertStock(stock: Stock) {
        withContext(Dispatchers.IO) {
            Log.d("Repository", "Insert Stock")
            stockDao.insert(stock)
        }
    }

    suspend fun setPrice(ticker: String) {
        withContext(Dispatchers.IO) {
            Log.d("Repository", "Set Prices")
            try {
                val stock = stockDao.getStock(ticker)
                if (stock != null) {
                    if (stock.isPriceLoaded) return@withContext
                    val companyPrice = finnhubApi.quote(ticker)
                    val t = finnhubApi.companyProfile2(ticker, null, null)
                    Log.d("MainRepository", "Currency: ${t.currency}")
                    s.add(t.currency.toString())
                    val currentPrice = companyPrice.c ?: 0f
                    val dayDelta = calculateDayDelta(companyPrice.c ?: 0f, companyPrice.pc ?: 1f)
                    stock.price = currentPrice
                    stock.previousPrice = companyPrice.pc ?: 1f
                    stock.dayDelta = dayDelta
                    stock.isPriceLoaded = true
                    insertStock(stock)
                } else {
                    Log.d("MainRepository", "No such stock in database, $ticker")
                }
            } catch (exception: Exception) {
                Log.d("MainRepository", exception.toString())
            }
        }
    }
}