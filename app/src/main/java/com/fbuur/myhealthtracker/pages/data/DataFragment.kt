package com.fbuur.myhealthtracker.pages.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentDataBinding
import com.fbuur.myhealthtracker.pages.data.calendar.CalendarFragment
import com.fbuur.myhealthtracker.pages.data.calendar.CompareFragment
import com.fbuur.myhealthtracker.pages.data.calendar.StatisticsFragment
import com.google.android.material.tabs.TabLayoutMediator


class DataFragment : Fragment(R.layout.fragment_data) {

    private var _binding: FragmentDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {

        val fragmentList = arrayListOf(
            CalendarFragment(),
            StatisticsFragment(),
            CompareFragment()
        )

        val viewPagerAdapter = DataViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.apply {
            viewPager.adapter = viewPagerAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Calendar"
                    1 -> "Statistics"
                    2 -> "Compare"
                    else -> "not supported"

                }
            }.attach()
        }

    }

}