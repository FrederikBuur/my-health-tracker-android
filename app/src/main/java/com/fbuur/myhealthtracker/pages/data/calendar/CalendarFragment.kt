package com.fbuur.myhealthtracker.pages.data.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var calendarViewModel: CalendarViewModel

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {

        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        val calenderGridAdapter = CalenderGridAdapter(
            calenderDays = emptyList()
        )

        calendarViewModel.readCalenderDayItemsByMonth(0) {
            calenderGridAdapter.setDate(it)
        }

        binding.apply {
            calenderGridView.adapter = calenderGridAdapter
        }

    }
}