package com.fbuur.myhealthtracker.pages.data.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

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



    }
}