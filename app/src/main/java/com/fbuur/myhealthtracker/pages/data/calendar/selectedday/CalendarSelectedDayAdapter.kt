package com.fbuur.myhealthtracker.pages.data.calendar.selectedday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemCalendarSelectedDayBinding
import com.fbuur.myhealthtracker.databinding.ItemCalendarSelectedDayDataBinding

class CalendarSelectedDayAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var calenderSelectedDayEvents = emptyList<CalendarSelectedDayEvent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CalendarSelectedDayEventViewHolder(
            itemBinding = ItemCalendarSelectedDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) {
            ItemCalendarSelectedDayDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = calenderSelectedDayEvents[position]
        (holder as? CalendarSelectedDayEventViewHolder)?.bind(item)
    }

    override fun getItemCount() = calenderSelectedDayEvents.size

    fun setData(list: List<CalendarSelectedDayEvent>) {
        this.calenderSelectedDayEvents = list
        this.notifyDataSetChanged()
    }

}
