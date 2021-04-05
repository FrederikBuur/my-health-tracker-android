package com.fbuur.myhealthtracker.pages.events

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.RegistrationType
import com.fbuur.myhealthtracker.databinding.ItemEventBinding
import com.fbuur.myhealthtracker.databinding.ItemNoteBinding
import com.fbuur.myhealthtracker.databinding.ItemParameterNoteBinding
import com.fbuur.myhealthtracker.databinding.ItemParameterSliderBinding
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemAdapter
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import com.fbuur.myhealthtracker.util.DiffUtilEventItems
import com.fbuur.myhealthtracker.util.getInitials
import com.fbuur.myhealthtracker.util.toDateString

class EventsListAdapter(
    private val onAddParameterClicked: (Long, Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var eventsList = emptyList<EventItemEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            RegistrationType.EVENT.ordinal -> {

                EventViewHolder(
                    // inflate parameter views up hgere and pass them inside
                    itemBinding = ItemEventBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    parameterNoteBinding = ItemParameterNoteBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    parameterSliderBinding = ItemParameterSliderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            RegistrationType.NOTE.ordinal -> NoteViewHolder(
                ItemNoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> {
                throw Exception("event list adapter: unknown view type")
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = eventsList[position]

        when (holder.itemViewType) {
            RegistrationType.NOTE.ordinal -> {
                (holder as NoteViewHolder).bind(
                    event
                )
            }
            RegistrationType.EVENT.ordinal -> {
                (holder as EventViewHolder).bind(
                    event,
                    onAddParameterClicked
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return eventsList[position].type.ordinal
    }

    override fun getItemCount() = eventsList.size


    fun setData(newEvents: List<EventItemEntry>) {
        val diffUtil = DiffUtilEventItems(this.eventsList, newEvents)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        diffResults.dispatchUpdatesTo(this)
        eventsList = newEvents
    }

}

class EventViewHolder(
    private val itemBinding: ItemEventBinding,
    private val parameterNoteBinding: ItemParameterNoteBinding,
    private val parameterSliderBinding: ItemParameterSliderBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(
        eventItemEntry: EventItemEntry,
        onAddParameterClicked: (Long, Long) -> Unit
    ) {
        itemBinding.apply {
            expansionCollapseView(eventItemEntry.isExpanded)
            eventName.text = eventItemEntry.name
            eventDate.text = eventItemEntry.date.toDateString()
            eventIcon.setCardBackgroundColor(Color.parseColor(eventItemEntry.iconColor))
            eventIconInitials.text = eventItemEntry.name.getInitials()
            eventItemContainer.setOnClickListener {
                eventItemEntry.isExpanded = !eventItemEntry.isExpanded
                expansionCollapseView(eventItemEntry.isExpanded)
            }
            addParametersButton.setOnClickListener {
                val ids = eventItemEntry.id.split(':')
                onAddParameterClicked.invoke(ids[0].toLong(), ids[1].toLong())
            }

            // make sure parameter view is clear when reusing
            itemBinding.eventParameters.removeAllViews()

            //add parameter views to linear layout
            eventItemEntry.eventParameterList.forEach { p ->

                val view = when (p) {
                    is EventItemParameter.Note -> {
                        setupAsNote(p, this@EventViewHolder.parameterNoteBinding)
                    }
                    is EventItemParameter.Slider -> {
                        setupAsSlider(p, this@EventViewHolder.parameterSliderBinding)
                    }
                }
                itemBinding.eventParameters.addView(view)

            }

        }
    }

    private fun expansionCollapseView(isExpanded: Boolean) {
        if (isExpanded) {
            itemBinding.eventItemContainer.transitionToEnd()
        } else {
            itemBinding.eventItemContainer.transitionToStart()
        }
    }

    private fun setupAsNote(
        note: EventItemParameter.Note,
        binding: ItemParameterNoteBinding
    ): View {
        return binding
            .apply {
                parameterHeader.text = note.title
                parameterNoteText.setText(note.description)
            }.root
    }

    private fun setupAsSlider(
        slider: EventItemParameter.Slider,
        binding: ItemParameterSliderBinding
    ): View {
        return binding
            .apply {
                parameterHeader.text = slider.title
                parameterSlider.value = slider.value.toFloat()
                parameterSlider.valueFrom = slider.lowest.toFloat()
                parameterSlider.valueTo = slider.highest.toFloat()
//                parameterSlider.addOnChangeListener { slider, value, fromUser ->
//                    if (fromUser) {
//                        // update slider value in DB
//                        value
//                    }
//                }
            }.root
    }

}

class NoteViewHolder(
    private val itemBinding: ItemNoteBinding
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(eventItemEntry: EventItemEntry) {

        val text =
            (eventItemEntry.eventParameterList.firstOrNull() as? EventItemParameter.Note)?.description

        itemBinding.apply {
            eventName.text = eventItemEntry.name
            eventDate.text = eventItemEntry.date.toDateString()
            noteDescription.text = text
        }
    }
}