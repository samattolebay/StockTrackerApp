package com.example.stockmarkettracker.database

import android.util.Log
import com.example.stockmarkettracker.getJsonDataFromAsset
import com.finnhub.api.apis.DefaultApi
import com.finnhub.api.models.CompanyProfile2
import com.finnhub.api.models.Quote
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainRepository(
    private val stockDao: StockDao,
    private val finnhubApi: DefaultApi,
    private val gSon: Gson,
    private val jsonFileString: String?
) {
    val stocks = stockDao.getStocks()
    val favouriteStocks = stockDao.getFavouriteStocks()

    suspend fun fetchStocks(symbol: String) {
        Log.d("MainRepository", "Inside the first context")
        withContext(Dispatchers.IO) {
//            var companyInfo = CompanyProfile2()
//            var companyPrice = Quote()
//            coroutineScope {
//                launch {
//                    try {
//                        companyInfo =
//                            finnhubApi.companyProfile2(symbol = symbol, isin = null, cusip = null)
//                        companyPrice = finnhubApi.quote(symbol = symbol)
//                    } catch (exception: Exception) {
//                        Log.d("MainRepository", exception.toString())
//                    }
//                }
//            }
//            Log.d("MainRepository", "Creating Stock, ${companyInfo}, $companyPrice")
//            val stock = Stock(
//                ticker = companyInfo.ticker.toString(),
//                name = companyInfo.name.toString(),
//                currentPrice = companyPrice.c ?: 0f,
//                dayDelta = calculateDayDelta(companyPrice.c ?: 0f, companyPrice.pc ?: 1f),
//                logo = companyInfo.logo
//            )
//            stockDao.insert(stock)
            val listPersonType = object : TypeToken<List<Stock>>() {}.type

            val stocks: List<Stock> = gSon.fromJson(jsonFileString, listPersonType)
            stocks.forEachIndexed { idx, stock ->
                launch {
                    try {
                        val companyProfile = finnhubApi.companyProfile2(symbol = stock.)
                    }
                }
            }
        }
    }

    private fun calculateDayDelta(currentPrice: Float, previousPrice: Float): Float {
        return (previousPrice - currentPrice) / previousPrice
    }

    suspend fun insertStock(stock: Stock) {
        Log.d("MainRepository", "Inside the insert Stock")
        withContext(Dispatchers.IO) {
            stockDao.insert(stock)
        }
    }

    suspend fun deleteStocks() {
        withContext(Dispatchers.IO) {
            stockDao.deleteStocks()
        }
    }

    suspend fun getPrice(ticker: String): List<Float> {
        withContext(Dispatchers.IO) {
            val companyPrice = finnhubApi.quote(ticker)
            val currentPrice = companyPrice.c ?: 0f
            val dayDelta = calculateDayDelta(companyPrice.c ?: 0f, companyPrice.pc ?: 1f)
            return@withContext listOf(currentPrice, dayDelta)
        }
        return listOf(0f, 0f)
    }
}