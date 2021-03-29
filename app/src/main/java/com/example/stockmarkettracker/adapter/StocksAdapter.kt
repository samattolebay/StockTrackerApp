package com.example.stockmarkettracker.adapter

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarkettracker.R
import com.example.stockmarkettracker.database.Stock
import com.squareup.picasso.Picasso
import java.lang.Exception

const val adapter = "StocksAdapter"

class StocksAdapter(
    private val onViewClick: (stock: Stock) -> Unit,
    private val onFavouriteClick: (stock: Stock) -> Unit,
    private val resources: Resources
) : ListAdapter<Stock, StocksAdapter.StockViewHolder>(STOCKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        return StockViewHolder.create(parent, resources)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onViewClick, onFavouriteClick, position)
    }

    class StockViewHolder(itemView: View, resources: Resources) :
        RecyclerView.ViewHolder(itemView) {
        private val tickerText: TextView = itemView.findViewById(R.id.tickerText)
        private val nameText: TextView = itemView.findViewById(R.id.nameText)
        private val currentPriceText: TextView = itemView.findViewById(R.id.currentPriceText)
        private val dayDeltaText: TextView = itemView.findViewById(R.id.dayDeltaText)
        private val logoImage: ImageView = itemView.findViewById(R.id.logoImage)
        private val favouriteImage: ImageView = itemView.findViewById(R.id.favouriteImage)
        private val redColor = resources.getColor(R.color.red)
        private val greenColor = resources.getColor(R.color.green)

        fun bind(
            current: Stock,
            onViewClick: (stock: Stock) -> Unit,
            onFavouriteClick: (stock: Stock) -> Unit,
            position: Int
        ) {
            tickerText.text = current.ticker
            nameText.text = current.companyName
            currentPriceText.text = current.currentPrice.toString()
            dayDeltaText.text = current.dayDelta.toString()
            if (current.dayDelta < 0) {
                dayDeltaText.setTextColor(redColor)
            } else {
                dayDeltaText.setTextColor(greenColor)
            }
            favouriteImage.setImageResource(current.imageResource)
            try {
                Picasso.get().load(current.image).into(logoImage)
            } catch (ex: Exception) {
                Log.d(adapter, "Error!")
            }

            if (position % 2 == 0) {
                itemView.setBackgroundResource(R.color.odd_item_background)
            } else {
                itemView.setBackgroundResource(R.color.white)
            }

            itemView.setOnClickListener {
                onViewClick.invoke(current)
            }
            favouriteImage.setOnClickListener {
                current.isFavourite = !current.isFavourite
                current.imageResource = if (current.isFavourite) R.drawable.ic_favourite
                else R.drawable.ic_not_favourite

                onFavouriteClick.invoke(current)
                favouriteImage.setImageResource(current.imageResource)
            }
        }

        companion object {
            fun create(parent: ViewGroup, resources: Resources): StockViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.stock_item, parent, false)
                return StockViewHolder(view, resources)
            }
        }
    }

    companion object {
        private val STOCKS_COMPARATOR = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem == newItem
            }
        }
    }
}
