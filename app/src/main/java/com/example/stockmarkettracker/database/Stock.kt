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
    var id: Int,
    var ticker: String,
    var companyName: String,
    var currentPrice: Float,
    var dayDelta: Float,
    @ColumnInfo(name = "isFavourite")
    var isFavourite: Boolean = false,
    var imageResource: Int = R.drawable.ic_not_favourite,
    var image: String? = null
) : Parcelable