package com.fbuur.myhealthtracker.pages.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.databinding.FragmentEventsBinding
import com.fbuur.myhealthtracker.databinding.FragmentStatisticsBinding
import com.wajahatkarim3.roomexplorer.RoomExplorer

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {

        binding.root.setOnClickListener { v ->
            RoomExplorer.show(context, TrackingDatabase::class.java, "tracking_database")
        }

    }

}