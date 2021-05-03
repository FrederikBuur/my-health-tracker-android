package com.fbuur.myhealthtracker.pages.data.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentCompareBinding

class CompareFragment : Fragment(R.layout.fragment_compare) {

    private var _binding: FragmentCompareBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompareBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {



    }
}