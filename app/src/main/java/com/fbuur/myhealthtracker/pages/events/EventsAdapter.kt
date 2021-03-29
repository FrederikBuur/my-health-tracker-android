package com.fbuur.myhealthtracker.pages.events

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.RegistrationType
import com.fbuur.myhealthtracker.databinding.ItemEventBinding
import com.fbuur.myhealthtracker.databinding.ItemNoteBinding
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.util.DiffUtilEventItems
import com.fbuur.myhealthtracker.util.getInitials
import com.fbuur.myhealthtracker.util.toDateString

class EventsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var eventsList = emptyList<EventItemEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            RegistrationType.EVENT.ordinal -> EventViewHolder(
                ItemEventBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )
            RegistrationType.NOTE.ordinal -> NoteViewHolder(
                ItemNoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )
            else -> {
                throw Exception("event list adapter: unknown view type")
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = eventsList[position]

        when(holder.itemViewType) {
            RegistrationType.NOTE.ordinal -> {
                (holder as NoteViewHolder).bind(
                    event
                )
            }
            RegistrationType.EVENT.ordinal -> {
                (holder as EventViewHolder).bind(
                    event
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
    private val itemBinding: ItemEventBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

    private var isExpanded = false

    private fun expansionCollapseView() {
        if (isExpanded) {
            isExpanded = !isExpanded
            itemBinding.eventItemContainer.transitionToStart()
        } else {
            isExpanded = !isExpanded
            itemBinding.eventItemContainer.transitionToEnd()
        }
    }

    fun bind(eventItemEntry: EventItemEntry) {
        itemBinding.apply {
            eventName.text = eventItemEntry.name
            eventDate.text = eventItemEntry.date.toDateString()
            eventIcon.setCardBackgroundColor(Color.parseColor(eventItemEntry.iconColor))
            eventIconInitials.text = eventItemEntry.name.getInitials()
            eventItemContainer.setOnClickListener {
                expansionCollapseView()
            }
        }
    }
}

class NoteViewHolder(
    private val itemBinding: ItemNoteBinding
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(eventItemEntry: EventItemEntry) {

        val text = (eventItemEntry.eventParameterList.firstOrNull() as? Parameter.Note)?.description

        itemBinding.apply {
            eventName.text = eventItemEntry.name
            eventDate.text = eventItemEntry.date.toDateString()
            noteDescription.text = text
        }
    }
}