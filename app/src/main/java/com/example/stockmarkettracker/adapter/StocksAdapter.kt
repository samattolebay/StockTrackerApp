package com.example.stockmarkettracker.adapter

import android.content.res.ColorStateList
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
import kotlin.math.absoluteValue

class StocksAdapter(
    private val onViewClick: (stock: Stock) -> Unit,
    private val onFavouriteClick: (stock: Stock) -> Unit,
    private val setPrice: (ticker: String) -> Unit,
    private val redColor: ColorStateList,
    private val greenColor: ColorStateList
) : ListAdapter<Stock, StocksAdapter.StockViewHolder>(STOCKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        return StockViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(
            current,
            onViewClick,
            onFavouriteClick,
            position,
            setPrice,
            redColor,
            greenColor
        )
    }

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tickerText: TextView = itemView.findViewById(R.id.tickerText)
        private val nameText: TextView = itemView.findViewById(R.id.nameText)
        private val currentPriceText: TextView = itemView.findViewById(R.id.currentPriceText)
        private val dayDeltaText: TextView = itemView.findViewById(R.id.dayDeltaText)
        private val logoImage: ImageView = itemView.findViewById(R.id.logoImage)
        private val favouriteImage: ImageView = itemView.findViewById(R.id.favouriteImage)

        fun bind(
            current: Stock,
            onViewClick: (stock: Stock) -> Unit,
            onFavouriteClick: (stock: Stock) -> Unit,
            position: Int,
            setPrice: (ticker: String) -> Unit,
            redColor: ColorStateList,
            greenColor: ColorStateList
        ) {
            tickerText.text = current.ticker
            nameText.text = current.name

            currentPriceText.text = "$%s".format(current.price)
            val dayDeltaTextColor = if (current.dayDelta < 0f) redColor else greenColor
            var dayDelta = if (current.dayDelta < 0f) "-" else "+"
            dayDelta += "$%.2f (%.2f%%)"
            dayDeltaText.text = dayDelta.format(
                (current.price - current.previousPrice).absoluteValue,
                current.dayDelta.absoluteValue
            )
            dayDeltaText.setTextColor(dayDeltaTextColor)
            setPrice(current.ticker)

            try {
                Picasso.get().load(current.logo).into(logoImage)
            } catch (ex: Exception) {
                Log.d("StocksAdapter", ex.toString())
                logoImage.setImageResource(R.drawable.ic_no_image)
            }

            setFavouriteImageResource(favouriteImage, current.isFavourite)

            val backgroundResource =
                if (position % 2 == 0) R.drawable.odd_item_background else R.drawable.stock_item_background
            itemView.setBackgroundResource(backgroundResource)

            itemView.setOnClickListener {
                onViewClick.invoke(current)
            }
            favouriteImage.setOnClickListener {
                current.isFavourite = !current.isFavourite
                onFavouriteClick.invoke(current)
                setFavouriteImageResource(favouriteImage, current.isFavourite)
            }
        }

        private fun setFavouriteImageResource(favouriteImage: ImageView, favourite: Boolean) {
            val imageResource =
                if (favourite) R.drawable.ic_favourite else R.drawable.ic_not_favourite
            favouriteImage.setImageResource(imageResource)
        }

        companion object {
            fun create(parent: ViewGroup): StockViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.stock_item, parent, false)
                return StockViewHolder(view)
            }
        }
    }

    companion object {
        private val STOCKS_COMPARATOR = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem.ticker == newItem.ticker
            }

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem == newItem
            }
        }
    }
}
