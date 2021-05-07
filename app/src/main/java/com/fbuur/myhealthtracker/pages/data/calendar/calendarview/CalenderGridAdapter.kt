package com.fbuur.myhealthtracker.pages.data.calendar.calendarview

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemCalendarWhitespaceBinding
import com.fbuur.myhealthtracker.databinding.ItemCalenderDayBinding
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayEventViewHolder
import com.fbuur.myhealthtracker.pages.events.DiffUtilEventItems
import java.lang.Exception

class CalenderGridAdapter(
    private val onDaySelected: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = emptyList<CalenderDay>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            CalenderDayType.WHITESPACE.ordinal -> {
                CalendarWhitespaceViewHolder(
                    itemBinding = ItemCalendarWhitespaceBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            CalenderDayType.DAY.ordinal -> {
                CalenderDayViewHolder(
                    itemBinding = ItemCalenderDayBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onDaySelected = onDaySelected
                )
            }
            else -> {
                throw Exception("calendar day list adapter: unknown view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = this.list[position]
        val ok = (holder as? CalenderDayViewHolder)
            ok?.let {
                it.bind(item)
            }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].calenderDayType.ordinal
    }

    override fun getItemCount() = list.size

    fun setData(list: List<CalenderDay>) {
        // todo implement diff util here but needs a recyclerview adapter
        val diffUtil = DiffUtilCalendarDay(this.list, list)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        diffResults.dispatchUpdatesTo(this)
        this.list = list

//        this.list = list
//        notifyDataSetChanged()
    }

}