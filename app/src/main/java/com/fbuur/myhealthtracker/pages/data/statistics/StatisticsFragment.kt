package com.fbuur.myhealthtracker.pages.data.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentStatisticsBinding
import com.fbuur.myhealthtracker.pages.data.DataViewModel

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private lateinit var dataViewModel: DataViewModel

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {

        // setup view model
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        // observers
        dataViewModel.barChartData.observe(viewLifecycleOwner) { barChart ->
            binding.barChartView.barChartData = barChart
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}