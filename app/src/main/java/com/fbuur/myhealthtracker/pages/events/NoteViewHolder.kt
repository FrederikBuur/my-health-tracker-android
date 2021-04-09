package com.fbuur.myhealthtracker.pages.events

import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemNoteBinding
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import com.fbuur.myhealthtracker.util.toDateString

class NoteViewHolder(
    private val itemBinding: ItemNoteBinding
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(
        eventItemEntry: EventItemEntry,
        onParameterNoteClicked: (EventItemParameter.Note) -> Unit
    ) {

        val noteParameter =
            (eventItemEntry.eventParameterList.firstOrNull() as? EventItemParameter.Note)

        itemBinding.apply {
            eventName.text = eventItemEntry.name
            eventDate.text = eventItemEntry.date.toDateString()
            noteDescription.text = noteParameter?.description
            noteDescription.setOnClickListener {
                noteParameter?.let {
                    onParameterNoteClicked(it)
                }
            }
        }
    }
}