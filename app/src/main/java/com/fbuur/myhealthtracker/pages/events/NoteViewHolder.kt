package com.fbuur.myhealthtracker.pages.events

import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemNoteBinding
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import com.fbuur.myhealthtracker.util.toDateString

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