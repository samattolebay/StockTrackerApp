package com.example.stockmarkettracker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM stock_table ORDER BY ticker")
    fun getStocks(): Flow<List<Stock>>

    @Query("SELECT * FROM stock_table WHERE isFavourite = 1 ORDER BY ticker")
    fun getFavouriteStocks(): Flow<List<Stock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Query("DELETE FROM stock_table")
    suspend fun deleteStocks()

    @Query("SELECT * FROM stock_table WHERE ticker = :ticker LIMIT 1")
    suspend fun getStock(ticker: String): Stock?

    @Query("SELECT * FROM stock_table WHERE ticker LIKE :query OR name LIKE :query ORDER BY ticker")
    suspend fun getSearchStocks(query: String): List<Stock>
}
