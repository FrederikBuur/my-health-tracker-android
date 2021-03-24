package com.fbuur.myhealthtracker.pages.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.RegistrationViewModel
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.RegistrationType
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.databinding.FragmentEventsBinding
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterAdapter
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterEntry
import com.fbuur.myhealthtracker.util.MyDialog
import com.fbuur.myhealthtracker.util.SwipeToDeleteCallback
import com.fbuur.myhealthtracker.util.hideKeyboard
import java.util.*


class EventsFragment : Fragment(R.layout.fragment_events) {

    private lateinit var registrationViewModel: RegistrationViewModel

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private var eventItemEntries = emptyList<EventItemEntry>()
    private var quickRegisterEntries = emptyList<QuickRegisterEntry>()

    private var quickRegisterIdLongClicked = -1L

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
        val quickRegisterAdapter = QuickRegisterAdapter(
            onQuickRegisterClicked,
            onQuickRegisterNoteClicked,
            onQuickRegisterLongClicked
        )

        // setup view models
        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        // events data
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
        // quick register data
        registrationViewModel.readAllQuickRegisterEntries.observe(
            viewLifecycleOwner,
            { quickRegisterEntries ->
                this.quickRegisterEntries = quickRegisterEntries
                quickRegisterAdapter.setData(this.quickRegisterEntries)
                binding.quickRegister.quickRegisterRecyclerView.scrollToPosition(0)
            })

        // setup view binding
        binding.createEventView.createEventBtn.setOnClickListener { v -> onCreateNewEventClicked() }
        binding.eventsRecyclerView.adapter = eventsAdapter
        binding.quickRegister.quickRegisterRecyclerView.adapter = quickRegisterAdapter

        onSwipeListener.attachToRecyclerView(binding.eventsRecyclerView)
    }

    private val onQuickRegisterClicked: (Long) -> Unit = { temId ->
        registrationViewModel.addRegistration(
            Registration(
                id = 0,
                temId = temId,
                date = Date(),
                type = RegistrationType.EVENT
            )
        )
        registrationViewModel.updateTemplateLastUsed(temId)
    }

    private val onQuickRegisterLongClicked: (Long) -> Unit = { temId ->
        this.quickRegisterIdLongClicked = temId
    }

    private val onQuickRegisterNoteClicked: () -> Unit = {
        // create note
        val registration = Registration(
            id = 0,
            temId = -1,
            date = Date(),
            type = RegistrationType.NOTE
        )
        registrationViewModel.addRegistration(registration)

    }

    private val onSwipeListener =
        ItemTouchHelper(object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val event = this@EventsFragment.eventItemEntries[viewHolder.adapterPosition]
                val id = event.id.split(':').first().toLong()
                registrationViewModel.deleteRegistrationById(id)
            }
        })

    private fun onCreateNewEventClicked() {
        val name = binding.createEventView.inputCreateEvent.text.toString()
        if (name.isBlank()) return

        val template = Template(
            id = 0,
            name = name,
            lastUsed = Date(),
            color = genColorForTemplate()
        )
        registrationViewModel.addTemplate(template) { temId ->
            val registration = Registration(
                id = 0,
                temId = temId,
                date = Date(),
                type = RegistrationType.EVENT
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

    private fun genColorForTemplate(): String {
        val colors = resources.getStringArray(R.array.eventColors)
        return colors.toList().shuffled().first()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val temId = this.quickRegisterIdLongClicked
        when(item.itemId) {
            R.id.rename_template -> {
                MyDialog { newName ->
                    registrationViewModel.updateTemplateName(temId, newName)
                }.show(parentFragmentManager, this.javaClass.toString())
            }
            R.id.delete_template -> {
                registrationViewModel.deleteTemplateById(temId)
            }
        }
        return super.onContextItemSelected(item)
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