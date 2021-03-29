package com.example.stockmarkettracker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM stock_table")
    fun getStocks(): Flow<List<Stock>>

    @Query("SELECT * FROM stock_table WHERE isFavourite = 1")
    fun getFavouriteStocks(): Flow<List<Stock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Query("DELETE FROM stock_table")
    suspend fun deleteStocks()
}
