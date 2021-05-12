package com.fbuur.myhealthtracker.pages.data.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentStatisticsBinding
import com.fbuur.myhealthtracker.pages.data.CalendarManager
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
            // update data
            binding.barChartView.barChartData = barChart
            // update UI
            binding.monthPicker.monthTitle.text = dataViewModel.getSelectedScopedDateString()
        }

        // setup UI
        binding.apply {
            monthPicker.monthTitle.text = dataViewModel.getSelectedScopedDateString()
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listOf(DataViewModel.DataScope.WEEK, DataViewModel.DataScope.DAY)
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            monthPicker.scopeDropDown.adapter = adapter
            monthPicker.scopeDropDown.visibility = View.VISIBLE
            monthPicker.scopeDropDown.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        p3: Long
                    ) {
                        (parent?.getItemAtPosition(position) as? DataViewModel.DataScope)?.let {
                            dataViewModel.setDataScope(it)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // do nothing
                    }
                }
            monthPicker.arrowPrevious.setOnClickListener {
                val curDate = dataViewModel.getSelectedScopedDate()
                curDate
                val newDate = CalendarManager.getPreviousAsDateScoped(
                    date = dataViewModel.getSelectedScopedDate(),
                    scope = dataViewModel.getDataScope()
                )
                dataViewModel.setSelectedScopeDataDate(newDate)
            }
            monthPicker.arrowNext.setOnClickListener {
                val curDate = dataViewModel.getSelectedScopedDate()
                curDate
                val newDate = CalendarManager.getNextAsDateScoped(
                    date = dataViewModel.getSelectedScopedDate(),
                    scope = dataViewModel.getDataScope()
                )
                dataViewModel.setSelectedScopeDataDate(newDate)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}