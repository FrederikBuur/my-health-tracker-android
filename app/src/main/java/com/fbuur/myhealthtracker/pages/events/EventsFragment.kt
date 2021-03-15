package com.fbuur.myhealthtracker.pages.events

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.RegistrationViewModel
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.databinding.FragmentEventsBinding
import com.fbuur.myhealthtracker.util.hideKeyboard
import java.util.*


class EventsFragment : Fragment(R.layout.fragment_events) {

    private lateinit var registrationViewModel: RegistrationViewModel

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private var registrationList = emptyList<Registration>()
    private var templateList = emptyList<Template>()
    private var parameterList = emptyList<Parameter>()

    private var realList = arrayListOf<EventItemEntry>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        setup()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setup() {

        //setup listeners
        setupInputListener()

        val adapter = EventsListAdapter()

        // setup view models
        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        registrationViewModel.readAllTemplates.observe(viewLifecycleOwner, { templates ->
            this.templateList = templates
            // update relevant event // todo
        })


        registrationViewModel.readAllRegistrations.observe(viewLifecycleOwner, { registrations ->
            this.registrationList = registrations

            // make registration + template + parameter mapping
            val tempList = arrayListOf<EventItemEntry>()
            this.registrationList.forEach { registration ->
                this.templateList.firstOrNull { t -> t.id == registration.temId }?.let { t ->
                    tempList.add(
                        EventItemEntry(
                            id = "${registration.id}${t.id}",
                            name = t.name,
                            date = registration.date,
                            iconColor = t.color,
                            parameterList = emptyList() // todo
                        )
                    )
                } ?: run {
                    println(" test123 cant find template id: ${registration.temId}, for registration id: ${registration.id}")
                }
            }
            realList = tempList
            adapter.setData(this.realList)

        })

        // setup view
        binding.createEventView.createEventBtn.setOnClickListener { v ->
            onCreateNewEventClicked()
        }

        binding.eventsRecyclerView.adapter = adapter
    }

    private fun onCreateNewEventClicked() {

        val name = binding.createEventView.inputCreateEvent.text.toString()
        if (name.isBlank()) return

        val template = Template(
            id = 0,
            name = name,
            lastUsed = Date(),
            color = "#EE5959" // todo get random color from int array in resources
        )
        registrationViewModel.addTemplate(template) { temId ->
            val registration = Registration(
                id = 0,
                temId = temId,
                date = Date()
            )
            registrationViewModel.addRegistration(registration)
        }
        binding.createEventView.inputCreateEvent.text?.clear()
        hideKeyboard()
    }

    private fun setupInputListener() {
        binding.createEventView.apply {
            inputCreateEvent.addTextChangedListener(
                onTextChanged = { text, _, _, _ ->
                    if (text?.toString()?.length ?: 0 > 0) {
                        createEventBtn.visibility = View.VISIBLE
                    } else {
                        createEventBtn.visibility = View.GONE
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}