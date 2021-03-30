package com.fbuur.myhealthtracker.pages.addparameters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.RegistrationViewModel
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.databinding.FragmentAddParametersBinding

class AddParametersFragment : Fragment(R.layout.fragment_add_parameters) {

    private lateinit var registrationViewModel: RegistrationViewModel

    private var _binding: FragmentAddParametersBinding? = null
    private val binding get() = _binding!!

    private val args: AddParametersFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddParametersBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {

        // setup view models
        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)

        // parameters list
        val parametersList = listOf(
            AddParameterEntry("Note", "This can be used to write a small note to give some context to your event registration", ParameterType.NOTE),
            AddParameterEntry("Slider/Scale", "A slider can allow you to make a selection from a range of values", ParameterType.SLIDER),
            AddParameterEntry("Binary", "An either/or type of information", ParameterType.BINARY),
            AddParameterEntry("Location", "This will attach a GPS location", ParameterType.LOCATION),
        )

        context?.let {
            binding.parameterList.adapter = AddParametersAdapter(
                it,
                R.layout.item_add_parameters,
                parametersList
            ) { numberOfSelected ->
                if (numberOfSelected > 0) {
                    binding.addParametersButton.visibility = View.VISIBLE
                } else {
                    binding.addParametersButton.visibility = View.GONE
                }
            }
            binding.addParametersButton.setOnClickListener {
                // todo add parameters to template
                // todo add empty parameters to registration
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}