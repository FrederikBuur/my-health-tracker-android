package com.fbuur.myhealthtracker.pages.data.calendar.calendarview

import androidx.recyclerview.widget.DiffUtil
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDay
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry

class DiffUtilCalendarDay(
    private val oldList: List<CalenderDay>,
    private val newList: List<CalenderDay>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].day == newList[newItemPosition].day &&
                oldList[oldItemPosition].calenderDayType == newList[newItemPosition].calenderDayType &&
                oldList[oldItemPosition].events.toString() == newList[newItemPosition].events.toString() &&
                oldList[oldItemPosition].isSelected == newList[newItemPosition].isSelected
    }
}