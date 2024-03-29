package com.fbuur.myhealthtracker.pages.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.data.model.RegistrationType
import com.fbuur.myhealthtracker.databinding.*
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import java.util.*

class EventsListAdapter(
    private val onAddParameterClicked: (Long, Long) -> Unit,
    private val onRemoveParameterClicked: (Long, Long, ParameterType) -> Unit,
    private val onParameterChanged: (EventItemParameter) -> Unit,
    private val onParameterNoteClicked: (EventItemParameter.Note) -> Unit,
    private val onParameterTitleRenameClicked: (EventItemParameter) -> Unit,
    private val onParameterDateEditDayClicked: (EventItemEntry, Date) -> Unit,
    private val onParameterDateEditTimeClicked: (EventItemEntry, Date) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var eventsList = emptyList<EventItemEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            RegistrationType.EVENT.ordinal -> {

                EventViewHolder(
                    itemBinding = ItemEventBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onCreateParameterNoteBinding = {
                        ItemParameterNoteBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    },
                    onCreateParameterSliderBinding = {
                        ItemParameterSliderBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    },
                    onCreateParameterNumberBinding = {
                        ItemParameterNumberBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    },
                    editDateBinding = ItemParameterDateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onRemoveParameterClicked = onRemoveParameterClicked
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
                    event,
                    onParameterNoteClicked
                )
            }
            RegistrationType.EVENT.ordinal -> {
                (holder as EventViewHolder).bind(
                    event,
                    onAddParameterClicked,
                    onParameterChanged,
                    onParameterNoteClicked,
                    onParameterTitleRenameClicked,
                    { d ->
                        onParameterDateEditDayClicked(event, d)
                    },
                    { d ->
                        onParameterDateEditTimeClicked(event, d)
                    }
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
        eventsList = newEvents
        diffResults.dispatchUpdatesTo(this)
    }

}
