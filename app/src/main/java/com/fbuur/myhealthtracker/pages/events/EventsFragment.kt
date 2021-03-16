package com.fbuur.myhealthtracker.pages.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.RegistrationViewModel
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.databinding.FragmentEventsBinding
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterAdapter
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterEntry
import com.fbuur.myhealthtracker.util.hideKeyboard
import java.util.*


class EventsFragment : Fragment(R.layout.fragment_events) {

    private lateinit var registrationViewModel: RegistrationViewModel

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private var eventItemEntries = emptyList<EventItemEntry>()
    private var quickRegisterEntries = emptyList<QuickRegisterEntry>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        setup()
        return binding.root
    }

    private fun setup() {

        //setup listeners
        setupInputListener()

        val eventsAdapter = EventsListAdapter()
        val quickRegisterAdapter = QuickRegisterAdapter()

        // setup view models
        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        registrationViewModel.readAllEventItemEntries.observe(
            viewLifecycleOwner,
            { eventItemEntries ->
                this.eventItemEntries = eventItemEntries
                eventsAdapter.setData(this.eventItemEntries)
                binding.eventsRecyclerView.scrollToPosition(0)

                if (this.eventItemEntries.isEmpty()) {
                    binding.emptyListContainer.root.visibility = View.VISIBLE
                } else {
                    binding.emptyListContainer.root.visibility = View.GONE
                }

            })

        registrationViewModel.readAllQuickRegisterEntries.observe(
            viewLifecycleOwner,
            { quickRegisterEntries ->
                this.quickRegisterEntries = quickRegisterEntries
                quickRegisterAdapter.setData(this.quickRegisterEntries)
                binding.quickRegister.quickRegisterRecyclerView.scrollToPosition(0)
            })

        // setup view
        binding.createEventView.createEventBtn.setOnClickListener { v ->
            onCreateNewEventClicked()
        }
        binding.eventsRecyclerView.adapter = eventsAdapter
        binding.quickRegister.quickRegisterRecyclerView.adapter = quickRegisterAdapter
    }

    private fun onCreateNewEventClicked() {
        val name = binding.createEventView.inputCreateEvent.text.toString()
        if (name.isBlank()) return

        val template = Template(
            id = 0,
            name = name,
            lastUsed = Date(),
            color = genColorForTemplate() // todo get random color from int array in resources
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

    private fun onCreateQuickEventClicked(temId: Long) {
        registrationViewModel.addRegistration(
            Registration(
                id = 0,
                temId = temId,
                date = Date()
                )
        )
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

    private fun genColorForTemplate(): String {
        val colors = resources.getStringArray(R.array.eventColors)
        val color = colors.toList().shuffled().first()
        return color
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}