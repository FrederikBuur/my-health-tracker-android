package com.fbuur.myhealthtracker.pages.data.calendar

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class CalenderGridAdapter(
    calenderDays: List<CalenderDayItem>
) : BaseAdapter() {

    private var list = calenderDays

    override fun getCount() = list.size

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var view = convertView

        if (convertView == null) {
            view = CalenderDayView(
                calenderDay = list[position],
                context = parent.context
            )
        }

        return view
    }

    fun setDate(list: List<CalenderDayItem>) {
        this.list = list
        this.notifyDataSetChanged()
    }

}