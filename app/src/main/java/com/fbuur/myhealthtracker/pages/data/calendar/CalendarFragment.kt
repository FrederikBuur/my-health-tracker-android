package com.fbuur.myhealthtracker.pages.data.calendar

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentCalendarBinding
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderGridAdapter
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayAdapter
import com.fbuur.myhealthtracker.util.toMonthYearString
import java.lang.Exception
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var calendarViewModel: CalendarViewModel

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private var selectedDay = Date()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {

        // setup view model
        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        // setup adapters
        val calenderGridAdapter = CalenderGridAdapter(
            calenderDayList = emptyList(),
            activity = (context as? Activity) ?: throw Exception("test 123 ???")
        ) { selectedDay ->
            // set viewmodel selected day to selected day
            calendarViewModel.setSelectedDate(CalendarManager.getDateAtDay(selectedDay))

            // todo start recyclerview loader
        }
        val calendarDayEventsAdapter = CalendarSelectedDayAdapter()

        // live data observers
        calendarViewModel.calendarDays.observe(viewLifecycleOwner) { calendarDays ->
            calenderGridAdapter.setData(calendarDays)
            // in case month has been changed
            updateUI()
        }
        calendarViewModel.selectedDayEvents.observe(viewLifecycleOwner) { selectedCalendarDayEventList ->
            calendarDayEventsAdapter.setData(selectedCalendarDayEventList)

            // todo stop recyclerview loader
        }

        // set adapters
        binding.apply {
            calenderGridView.adapter = calenderGridAdapter
            selectedDayEvents.adapter = calendarDayEventsAdapter
        }

        updateUI()

    }

    private fun updateUI() {
        binding.apply {
            monthTitle.text = this@CalendarFragment.selectedDay.toMonthYearString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}