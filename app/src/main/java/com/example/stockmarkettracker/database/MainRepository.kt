package com.example.stockmarkettracker.database

import android.util.Log
import com.finnhub.api.apis.DefaultApi
import com.finnhub.api.models.CompanyProfile2
import com.finnhub.api.models.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainRepository(
    private val stockDao: StockDao,
    private val finnhubApi: DefaultApi
) {
    val stocks = stockDao.getStocks()
    val favouriteStocks = stockDao.getFavouriteStocks()

    suspend fun fetchStocks(symbol: String) {
        Log.d("MainRepository", "Inside the first context")
        withContext(Dispatchers.IO) {
            var companyInfo = CompanyProfile2()
            var companyPrice = Quote()
            coroutineScope {
                launch {
                    companyInfo =
                        finnhubApi.companyProfile2(symbol = symbol, isin = null, cusip = null)
                    companyPrice = finnhubApi.quote(symbol = symbol)
                }
            }
            Log.d("MainRepository", "Creating Stock, ${companyInfo}, $companyPrice")
            val stock = Stock(
                Random.nextInt(1000),
                companyInfo.ticker!!,
                companyInfo.name!!,
                companyPrice.c!!,
                calculateDelta(companyPrice.c!!, companyPrice.pc!!),
                image = companyInfo.logo
            )
            stockDao.insert(stock)
        }
    }

    private fun calculateDelta(currentPrice: Float, previousPrice: Float): Float {
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
}