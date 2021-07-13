package com.example.stockmarkettracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.stockmarkettracker.MyApplication
import com.example.stockmarkettracker.R
import com.example.stockmarkettracker.STOCK
import com.example.stockmarkettracker.adapter.StocksAdapter
import com.example.stockmarkettracker.databinding.FragmentListBinding
import com.example.stockmarkettracker.viewmodel.MainViewModel
import com.example.stockmarkettracker.viewmodel.MainViewModelFactory

class ListFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels { MainViewModelFactory((activity?.application as MyApplication).repository) }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()
        observeVm()

        viewModel.searchStocks()

        return view
    }

    private fun observeVm() {
        viewModel.stocks.observe(viewLifecycleOwner, {
            if (viewModel.isFavouriteShown.value == false) {
                (binding.stocks.adapter as StocksAdapter).submitList(it)
            }
        })

        viewModel.favouriteStocks.observe(viewLifecycleOwner, {
            if (viewModel.isFavouriteShown.value == true) {
                (binding.stocks.adapter as StocksAdapter).submitList(it)
            }
        })

        viewModel.isFavouriteShown.observe(viewLifecycleOwner) {
            val stocks = if (it) viewModel.favouriteStocks.value else viewModel.stocks.value
            val stocksTextSize = if (it) 18 else 28
            val favouriteTextSize = if (it) 28 else 18
            val stocksTextColor =
                if (it) R.color.menu_unselected_color else R.color.menu_selected_color
            val favouriteTextColor =
                if (it) R.color.menu_selected_color else R.color.menu_unselected_color

            (binding.stocks.adapter as StocksAdapter).submitList(stocks)
            setTextSize(binding.stocksText, stocksTextSize)
            setTextSize(binding.favouriteText, favouriteTextSize)
            binding.stocksText.setTextColor(
                getColor(
                    requireContext(),
                    stocksTextColor
                )
            )
            binding.favouriteText.setTextColor(
                getColor(
                    requireContext(),
                    favouriteTextColor
                )
            )
        }
    }

    private fun initViews() {
        val redColor = getColorStateList(requireContext(), R.color.red)
        val greenColor = getColorStateList(requireContext(), R.color.green)

        binding.stocks.adapter =
            StocksAdapter(
                { stock ->
                    val bundle = bundleOf(STOCK to stock)
                    findNavController().navigate(R.id.action_listFragment_to_cardFragment, bundle)
                },
                { stock -> viewModel.insertStock(stock) },
                { ticker -> viewModel.setPrices(ticker) },
                redColor,
                greenColor
            )

        binding.stocksText.setOnClickListener {
            viewModel.changeList()
        }

        binding.favouriteText.setOnClickListener {
            viewModel.changeList()
        }

        binding.textSearchArea.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_searchFragment)
        }
    }

    private fun setTextSize(text: TextView, size: Int) {
        text.textSize = size.toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}