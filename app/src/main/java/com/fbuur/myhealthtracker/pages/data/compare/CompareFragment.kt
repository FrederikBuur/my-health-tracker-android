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
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.databinding.FragmentCompareBinding
import com.fbuur.myhealthtracker.pages.data.DataViewModel
import com.fbuur.myhealthtracker.util.dpToPx

class CompareFragment : Fragment(R.layout.fragment_compare) {

    private lateinit var dataViewModel: DataViewModel

    private var _binding: FragmentCompareBinding? = null
    private val binding get() = _binding!!

    private val adapterPrimaryValueOfInterest by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayListOf("None")
        )
    }
    private val adapterSecondaryValueOfInterest by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayListOf("None")
        )
    }

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
            updateSelectValueOfInterestDropDown(
                parameterListPrimary = selectedParameterLists.first,
                parameterListSecondary = selectedParameterLists.second
            )
        }

        setupSelectValueOfInterestDropDown()
        binding. apply {
            
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
                                dataViewModel.setPrimarySelectedEventTypeIds(
                                    Pair(t.id, EVENT_COUNT_AS_INTEREST)
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
                        adapterPrimaryValueOfInterest.clear()
                        adapterPrimaryValueOfInterest.notifyDataSetChanged()
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
                        val isColouredBackground =
                            (parent?.getItemAtPosition(position) as? Template)?.let { t ->
                                selectEventSecondary.root.setCardBackgroundColor(Color.parseColor(t.color))
                                dataViewModel.setSecondarySelectedEventTypeIds(
                                    Pair(t.id, EVENT_COUNT_AS_INTEREST)
                                )
                                true
                            } ?: run {
                                selectEventSecondary.root.setCardBackgroundColor(
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
                            selectEventSecondary.root.strokeWidth = 0
                            selectEventSecondary.eventTypeTitle.setTextColor(color)
                            selectEventSecondary.valueOfInterestTitle.setTextColor(color)
                        } else {
                            val color = ContextCompat.getColor(
                                requireContext(),
                                R.color.mth_text_primary_onwhite
                            )
                            selectEventSecondary.root.strokeWidth = 2.dpToPx.toInt()
                            selectEventSecondary.eventTypeTitle.setTextColor(color)
                            selectEventSecondary.valueOfInterestTitle.setTextColor(color)
                        }
                        adapterSecondaryValueOfInterest.clear()
                        adapterSecondaryValueOfInterest.notifyDataSetChanged()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        }
    }

    private fun setupSelectValueOfInterestDropDown() {
        binding.selectEventPrimary.valueOfInterestDropDown.adapter = adapterPrimaryValueOfInterest
        binding.selectEventPrimary.valueOfInterestDropDown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    (parent?.getItemAtPosition(position) as? String)?.let { pName ->
                        // update view model selected data
                        dataViewModel.setPrimarySelectedEventParameter(pName)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        binding.selectEventSecondary.valueOfInterestDropDown.adapter =
            adapterSecondaryValueOfInterest
        binding.selectEventSecondary.valueOfInterestDropDown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    (parent?.getItemAtPosition(position) as? String)?.let { pName ->
                        // update view model selected data
                        dataViewModel.setSecondarySelectedEventParameter(pName)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

    }

    private fun updateSelectValueOfInterestDropDown(
        parameterListPrimary: List<String>?,
        parameterListSecondary: List<String>?
    ) {
        parameterListPrimary?.let {
            this.adapterPrimaryValueOfInterest.clear()
            this.adapterPrimaryValueOfInterest.notifyDataSetChanged()
            this.adapterPrimaryValueOfInterest.addAll(it)
            this.adapterPrimaryValueOfInterest.notifyDataSetChanged()
        }
        parameterListSecondary?.let {
            this.adapterSecondaryValueOfInterest.clear()
            this.adapterSecondaryValueOfInterest.notifyDataSetChanged()
            this.adapterSecondaryValueOfInterest.addAll(it)
            this.adapterSecondaryValueOfInterest.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EVENT_COUNT_AS_INTEREST = "Registration count"
    }

}