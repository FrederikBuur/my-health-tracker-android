package com.fbuur.myhealthtracker.pages.data.calendar.calendarview

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.fbuur.myhealthtracker.databinding.ItemCalenderDayBinding
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayEventViewHolder
import java.lang.Exception

class CalenderGridAdapter(
    calenderDayList: List<CalenderDay>,
    private val activity: Activity,
    private val onDaySelected: (Int) -> Unit
) : BaseAdapter() {

    private var list = calenderDayList

    override fun getCount() = list.size

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        val viewHolder = CalenderDayViewHolder(
            itemBinding = ItemCalenderDayBinding.inflate(activity.layoutInflater, null, false),
            onDaySelected = onDaySelected
        )

        // setup binding
        val item = getItem(position)
        viewHolder.bind(item)

        return viewHolder.itemView
    }

    fun setData(list: List<CalenderDay>) {
        // todo implement diff util here
        this.list = list
        this.notifyDataSetChanged()
    }

}