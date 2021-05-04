package com.fbuur.myhealthtracker.pages.data.calendar

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.fbuur.myhealthtracker.databinding.ViewCalenderDayBinding
import java.lang.Exception

class CalenderGridAdapter(
    calenderDayList: List<CalenderDay>,
    private val activity: Activity
) : BaseAdapter() {

    private var list = calenderDayList

    override fun getCount() = list.size

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var v = convertView

        val viewHolder: CalenderDayViewHolder
        if (convertView == null) {
            viewHolder = CalenderDayViewHolder(ViewCalenderDayBinding.inflate(activity.layoutInflater, null, false))
            v = viewHolder.itemView
            v.tag = viewHolder
        } else {
            viewHolder = (v?.tag as? CalenderDayViewHolder) ?: throw Exception("test123 should not happen?")
        }

        // setup binding
        val item = getItem(position)
        viewHolder.bind(item)

        return viewHolder.itemView
    }

    fun setDate(list: List<CalenderDay>) {
        this.list = list
        this.notifyDataSetChanged()
    }

}