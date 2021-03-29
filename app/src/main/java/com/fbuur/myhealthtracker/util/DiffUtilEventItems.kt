package com.fbuur.myhealthtracker.util

import androidx.recyclerview.widget.DiffUtil
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry

class DiffUtilEventItems(
    private val oldList: List<EventItemEntry>,
    private val newList: List<EventItemEntry>
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
        return oldList[oldItemPosition].name == newList[newItemPosition].name &&
                oldList[oldItemPosition].date == newList[newItemPosition].date &&
                oldList[oldItemPosition].iconColor == newList[newItemPosition].iconColor &&
                oldList[oldItemPosition].eventParameterList == newList[newItemPosition].eventParameterList
    }
}