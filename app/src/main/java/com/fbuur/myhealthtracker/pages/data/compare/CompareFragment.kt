package com.fbuur.myhealthtracker.pages.data.compare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.FragmentCompareBinding
import com.fbuur.myhealthtracker.pages.data.DataViewModel

class CompareFragment : Fragment(R.layout.fragment_compare) {

    private lateinit var dataViewModel: DataViewModel

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

        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        dataViewModel.compareGraphData.observe(viewLifecycleOwner) { compareGraphData ->
            compareGraphData?.let {
                binding.compareGraphView.compareGraphData = it
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EVENT_COUNT_AS_INTEREST = "event-count-as-interest"
    }

}