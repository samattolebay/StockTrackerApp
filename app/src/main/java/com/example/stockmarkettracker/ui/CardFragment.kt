package com.example.stockmarkettracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.stockmarkettracker.MyApplication
import com.example.stockmarkettracker.R
import com.example.stockmarkettracker.STOCK
import com.example.stockmarkettracker.database.Stock
import com.example.stockmarkettracker.databinding.FragmentCardBinding
import com.example.stockmarkettracker.viewmodel.MainViewModel
import com.example.stockmarkettracker.viewmodel.MainViewModelFactory

class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels { MainViewModelFactory((activity?.application as MyApplication).repository) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardBinding.inflate(inflater, container, false)
        val view = binding.root

        val stock = arguments?.getParcelable<Stock>(STOCK)

        binding.stockInfoText.text = stock.toString()

        if (stock != null) {
            binding.stockTickerText.text = stock.ticker
            binding.stockNameText.text = stock.name
            setFavouriteImage(stock.isFavourite)

            binding.starButton.setOnClickListener {
                stock.isFavourite = !stock.isFavourite
                viewModel.insertStock(stock)
                setFavouriteImage(stock.isFavourite)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        return view
    }

    private fun setFavouriteImage(isFavourite: Boolean) {
        val favouriteImage =
            if (isFavourite) R.drawable.ic_favourite else R.drawable.ic_not_favourite
        binding.starButton.setImageResource(favouriteImage)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}