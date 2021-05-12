package com.fbuur.myhealthtracker.pages.data.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentCalendarBinding
import com.fbuur.myhealthtracker.pages.data.CalendarManager
import com.fbuur.myhealthtracker.pages.data.DataViewModel
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderGridAdapter
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayAdapter
import com.fbuur.myhealthtracker.util.toMonthYearString
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var dataViewModel: DataViewModel

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
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)


        // setup adapters
        val calenderGridAdapter = CalenderGridAdapter() { selectedDay ->
            // set view model selected day to selected day
            dataViewModel.setSelectedDate(CalendarManager.getDateAtDay(selectedDay))
            // set ui
//            binding.selectedDayEventsSpinner.visibility = View.VISIBLE
//            binding.selectedDayEventsEmptyView.container.visibility = View.GONE
        }
        val calendarDayEventsAdapter = CalendarSelectedDayAdapter()


        // live data observers
        dataViewModel.calendarDays.observe(viewLifecycleOwner) { calendarDays ->
            // update data
            calenderGridAdapter.setData(calendarDays)
            // update ui
//            binding.calendarViewSpinner.visibility = View.GONE
            binding.monthPicker.monthTitle.text = dataViewModel.getSelectedDate()?.toMonthYearString()
        }
        dataViewModel.selectedDayEvents.observe(viewLifecycleOwner) { selectedCalendarDayEventList ->
            // update data
            calendarDayEventsAdapter.setData(selectedCalendarDayEventList)
            // update ui
//            binding.selectedDayEventsSpinner.visibility = View.GONE
            if (selectedCalendarDayEventList.isEmpty()) {
//                binding.selectedDayEventsEmptyView.container.visibility = View.VISIBLE
            } else {
//                binding.selectedDayEventsEmptyView.container.visibility = View.GONE
                binding.selectedDayEvents.scrollToPosition(0)
            }
        }


        // set binding values
        binding.apply {
            calenderGridView.layoutManager =
                GridLayoutManager(this@CalendarFragment.requireContext(), 7)
            calenderGridView.itemAnimator = null
            calenderGridView.adapter = calenderGridAdapter
            selectedDayEvents.adapter = calendarDayEventsAdapter
                monthPicker.monthTitle.text = dataViewModel.getSelectedDate().toMonthYearString()
//            binding.selectedDayEventsSpinner.visibility = View.VISIBLE
//            binding.calendarViewSpinner.visibility = View.VISIBLE
                monthPicker.arrowPrevious.setOnClickListener {
//                calendarViewSpinner.visibility = View.VISIBLE
//                selectedDayEventsSpinner.visibility = View.VISIBLE
//                binding.selectedDayEventsEmptyView.container.visibility = View.GONE
                dataViewModel.getSelectedDate().let {
                    dataViewModel.setSelectedDate(
                        CalendarManager.getPreviousMonthAsDate(it)
                    )
                }
            }
                monthPicker.arrowNext.setOnClickListener {
//                calendarViewSpinner.visibility = View.VISIBLE
//                selectedDayEventsSpinner.visibility = View.VISIBLE
//                binding.selectedDayEventsEmptyView.container.visibility = View.GONE
                dataViewModel.getSelectedDate().let {
                    dataViewModel.setSelectedDate(
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