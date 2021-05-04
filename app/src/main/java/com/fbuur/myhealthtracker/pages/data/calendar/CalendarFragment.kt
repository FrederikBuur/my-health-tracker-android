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

        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        val calenderGridAdapter = CalenderGridAdapter(
            calenderDayList = emptyList(),
            activity = (context as? Activity) ?: throw Exception("test 123 ???")
        )

        calendarViewModel.readCalenderDayItemsByMonth(0) { eventItemMap ->
            calenderGridAdapter.setDate(eventItemMap)
        }

        binding.apply {
            calenderGridView.adapter = calenderGridAdapter
        }

    }
}