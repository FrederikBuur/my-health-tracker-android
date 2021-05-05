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

        // setup view model
        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        // setup adapters
        val calendarDayEventsAdapter = CalendarSelectedDayAdapter()
        val calenderGridAdapter = CalenderGridAdapter(
            calenderDayList = emptyList(),
            activity = (context as? Activity) ?: throw Exception("test 123 ???")
        ) { selectedDay ->
            calendarViewModel.setSelectedDayOfMonth(selectedDay)

            // update selected day adapter data
            calendarViewModel.readCalenderEventsByDay { list ->
                calendarDayEventsAdapter.setData(list)
            }
        }

        // read data to be displayed
        calendarViewModel.readCalenderDayItemsByMonth(0) { eventItemMap ->
            calenderGridAdapter.setData(eventItemMap)
        }
        calendarViewModel.readCalenderEventsByDay { list ->
            calendarDayEventsAdapter.setData(list)
        }

        // setup ui
        binding.apply {
            monthTitle.text = calendarViewModel.getSelectedDayAsDate().toMonthYearString()
            calenderGridView.adapter = calenderGridAdapter
            selectedDayEvents.adapter = calendarDayEventsAdapter
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}