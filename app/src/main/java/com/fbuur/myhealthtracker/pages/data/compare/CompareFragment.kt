package com.fbuur.myhealthtracker.pages.data.compare

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.databinding.FragmentCompareBinding
import com.fbuur.myhealthtracker.pages.data.DataViewModel
import com.fbuur.myhealthtracker.util.dpToPx

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
        dataViewModel.templates.observe(viewLifecycleOwner) { templates ->
            setupSelectEventDropDown(
                arrayListOf<Any?>("Select").apply {
                    addAll(templates)
                }
            )
        }
        dataViewModel.selectedEventParameters.observe(viewLifecycleOwner) { selectedParameterLists ->
            setupSelectValueOfInterestDropDown(
                parameterListPrimary = selectedParameterLists.first,
                parameterListSecondary = selectedParameterLists.second
            )
        }

    }

    private fun setupSelectEventDropDown(templates: List<Any?>) {
        val adapterPrimary = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            templates
        )
        val adapterSecondary = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            templates
        )
        binding.apply {
            selectEventPrimary.eventTypeDropDown.adapter = adapterPrimary
            selectEventPrimary.eventTypeDropDown.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        p3: Long
                    ) {
                        val isColouredBackground =
                            (parent?.getItemAtPosition(position) as? Template)?.let { t ->
                                selectEventPrimary.root.setCardBackgroundColor(Color.parseColor(t.color))
                                // fetch parameters from view model
                                dataViewModel.setPrimarySelectedEventTypeIds(
                                    Pair(
                                        t.id,
                                        EVENT_COUNT_AS_INTEREST
                                    )
                                )
                                true
                            } ?: run {
                                selectEventPrimary.root.setCardBackgroundColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.mth_white
                                    )
                                )
                                false
                            }

                        if (isColouredBackground) {
                            val color = ContextCompat.getColor(
                                requireContext(),
                                R.color.mth_white
                            )
                            selectEventPrimary.root.strokeWidth = 0
                            selectEventPrimary.eventTypeTitle.setTextColor(color)
                            selectEventPrimary.valueOfInterestTitle.setTextColor(color)
                        } else {
                            val color = ContextCompat.getColor(
                                requireContext(),
                                R.color.mth_text_primary_onwhite
                            )
                            selectEventPrimary.root.strokeWidth = 2.dpToPx.toInt()
                            selectEventPrimary.eventTypeTitle.setTextColor(color)
                            selectEventPrimary.valueOfInterestTitle.setTextColor(color)
                        }

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
            selectEventSecondary.eventTypeDropDown.adapter = adapterSecondary
            selectEventSecondary.eventTypeDropDown.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        p3: Long
                    ) {
                        (parent?.getItemAtPosition(position) as? Template)?.let { t ->
                            // fetch parameters from view model
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        }
    }

    private fun setupSelectValueOfInterestDropDown(
        parameterListPrimary: List<Parameter>?,
        parameterListSecondary: List<Parameter>?
    ) {
        parameterListPrimary?.let { parameters ->
            val adapterPrimary = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                parameters
            )
            binding.selectEventPrimary.valueOfInterestDropDown.adapter = adapterPrimary
            binding.selectEventPrimary.valueOfInterestDropDown.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        p3: Long
                    ) {
                        (parent?.getItemAtPosition(position) as? Parameter)?.let { p ->
                            // update view model selected data
                            dataViewModel.setPrimarySelectedEventParameter(p.title)
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
        }

        parameterListSecondary?.let {
            val adapterSecondary = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                it
            )
            binding.selectEventSecondary.valueOfInterestDropDown.adapter = adapterSecondary
            binding.selectEventSecondary.valueOfInterestDropDown.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        p3: Long
                    ) {
                        (parent?.getItemAtPosition(position) as? Parameter)?.let { p ->
                            // update view model selected data
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
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