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
            // set view model selected day to selected day
            calendarViewModel.setSelectedDate(CalendarManager.getDateAtDay(selectedDay))
            binding.selectedDayEventsSpinner.visibility = View.VISIBLE
        }
        val calendarDayEventsAdapter = CalendarSelectedDayAdapter()


        // live data observers
        calendarViewModel.calendarDays.observe(viewLifecycleOwner) { calendarDays ->
            // update data
            calenderGridAdapter.setData(calendarDays)
            // update ui
            binding.calendarViewSpinner.visibility = View.GONE
            binding.calendarViewPlaceholder.visibility = View.GONE
            binding.monthTitle.text = calendarViewModel.getSelectedDate()?.toMonthYearString()
        }
        calendarViewModel.selectedDayEvents.observe(viewLifecycleOwner) { selectedCalendarDayEventList ->
            // update data
            calendarDayEventsAdapter.setData(selectedCalendarDayEventList)
            // update ui
            binding.selectedDayEventsSpinner.visibility = View.GONE
            if (selectedCalendarDayEventList.isEmpty()) {
                binding.selectedDayEventsEmptyView.root.visibility = View.VISIBLE
            } else {
                binding.selectedDayEventsEmptyView.root.visibility = View.GONE
                binding.selectedDayEvents.scrollToPosition(0)
            }
        }


        // set binding values
        binding.apply {
            calenderGridView.adapter = calenderGridAdapter
            selectedDayEvents.adapter = calendarDayEventsAdapter
            monthTitle.text = calendarViewModel.getSelectedDate()?.toMonthYearString()
            arrowPrevious.setOnClickListener {
                calendarViewSpinner.visibility = View.VISIBLE
                selectedDayEventsSpinner.visibility = View.VISIBLE
                calendarViewModel.getSelectedDate()?.let {
                    calendarViewModel.setSelectedDate(
                        CalendarManager.getPreviousMonthAsDate(it)
                    )
                }
            }
            arrowNext.setOnClickListener {
                calendarViewSpinner.visibility = View.VISIBLE
                selectedDayEventsSpinner.visibility = View.VISIBLE
                calendarViewModel.getSelectedDate()?.let {
                    calendarViewModel.setSelectedDate(
                        CalendarManager.getNextMonthAsDate(it)
                    )
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}