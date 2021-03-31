package com.example.stockmarkettracker.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stockmarkettracker.R
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stock_table")
data class Stock(
    @PrimaryKey
    @ColumnInfo(name = "ticker")
    var ticker: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "isFavourite")
    var isFavourite: Boolean = false,
    var logo: String? = null
) : Parcelable