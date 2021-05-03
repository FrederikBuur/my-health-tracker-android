package com.fbuur.myhealthtracker.pages.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.databinding.FragmentDataBinding
import com.wajahatkarim3.roomexplorer.RoomExplorer

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

    }

}