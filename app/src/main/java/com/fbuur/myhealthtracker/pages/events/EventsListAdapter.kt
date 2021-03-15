package com.fbuur.myhealthtracker.pages.events

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemEventBinding
import com.fbuur.myhealthtracker.util.DiffUtilEventItems
import com.fbuur.myhealthtracker.util.getInitials
import com.fbuur.myhealthtracker.util.toDateString

class EventsListAdapter(
) : RecyclerView.Adapter<EventViewHolder>() {

    private var eventsList = emptyList<EventItemEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val registration = eventsList[position]
        holder.bind(registration)
    }

    override fun getItemCount() = eventsList.size


    fun setData(newEvents: List<EventItemEntry>) {
        val diffUtil = DiffUtilEventItems(this.eventsList, newEvents)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        diffResults.dispatchUpdatesTo(this)
        eventsList = newEvents
//        notifyDataSetChanged()
    }

}

class EventViewHolder(
    private val itemBinding: ItemEventBinding
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(eventItemEntry: EventItemEntry) {

        itemBinding.apply {
            eventName.text = eventItemEntry.name
            eventDate.text = eventItemEntry.date.toDateString()
            eventIcon.setCardBackgroundColor(Color.parseColor(eventItemEntry.iconColor))
            eventIconInitials.text = eventItemEntry.name.getInitials()
        }
    }
}