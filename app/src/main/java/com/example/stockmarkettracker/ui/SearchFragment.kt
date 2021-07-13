package com.example.stockmarkettracker.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.stockmarkettracker.MyApplication
import com.example.stockmarkettracker.R
import com.example.stockmarkettracker.STOCK
import com.example.stockmarkettracker.adapter.StocksAdapter
import com.example.stockmarkettracker.databinding.FragmentSearchBinding
import com.example.stockmarkettracker.viewmodel.MainViewModel
import com.example.stockmarkettracker.viewmodel.MainViewModelFactory
import com.google.android.material.chip.Chip

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels { MainViewModelFactory((activity?.application as MyApplication).repository) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews(view)
        observeVM(view)

        return view
    }

    private fun initViews(view: ConstraintLayout) {
        binding.buttonBackFromSearch.setOnClickListener {
            hideKeyboard(view)
            findNavController().popBackStack()
        }

        binding.buttonClear.setOnClickListener {
            binding.edittextSearch.setText("")
        }

        binding.edittextSearch.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    hideKeyboard(view)
                    // Todo: add to searched requests
                    Log.d("SearchFragment", "Submit clicked! Query: ${binding.edittextSearch.text}")
                    true
                }
                else -> false
            }
        }

        val redColor = AppCompatResources.getColorStateList(requireContext(), R.color.red)
        val greenColor = AppCompatResources.getColorStateList(requireContext(), R.color.green)

        binding.results.adapter =
            StocksAdapter(
                { stock ->
                    val bundle = bundleOf(STOCK to stock)
                    findNavController().navigate(R.id.action_searchFragment_to_cardFragment, bundle)
                },
                { stock -> viewModel.insertStock(stock) },
                { ticker -> viewModel.setPrices(ticker) },
                redColor,
                greenColor
            )

        binding.edittextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.isNullOrEmpty()) {
                    binding.buttonClear.visibility = View.VISIBLE
                    binding.resultsGroup.visibility = View.VISIBLE
                    binding.requestsGroup.visibility = View.GONE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.buttonClear.visibility = View.GONE
                    binding.resultsGroup.visibility = View.GONE
                    binding.requestsGroup.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.searchStocks(binding.edittextSearch.text.toString())
                Log.d("SearchFragment", s.toString())
            }

        })

        for (request in viewModel.popularRequests) {
            binding.popularGroup.addView(Chip(view.context).apply {
                text = request
                setOnClickListener {
                    binding.edittextSearch.setText(request)
                }
            })
        }
    }

    private fun observeVM(view: ConstraintLayout) {
        viewModel.searchedRequests.observe(viewLifecycleOwner, {
            for (request in it) {
                binding.searchedGroup.addView(Chip(view.context).apply {
                    text = request
                    setOnClickListener {
                        binding.edittextSearch.setText(request)
                    }
                })
            }
        })

        viewModel.stocks.observe(viewLifecycleOwner, {
            (binding.results.adapter as StocksAdapter).submitList(it)
//            viewModel.searchStocks(binding.edittextSearch.text.toString())
        })
    }

    private fun hideKeyboard(view: ConstraintLayout) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}