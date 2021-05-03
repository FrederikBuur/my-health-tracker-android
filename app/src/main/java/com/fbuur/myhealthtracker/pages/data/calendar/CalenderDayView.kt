package com.fbuur.myhealthtracker.pages.data.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.ViewCalenderDayBinding
import com.fbuur.myhealthtracker.databinding.ViewCalenderDayEventIconBinding
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry

class CalenderDayView : FrameLayout {

    private val binding: ViewCalenderDayBinding =
        ViewCalenderDayBinding.inflate(LayoutInflater.from(context), this, true)

    var calenderDayItem: CalenderDayItem
        //        CalenderDayItem("", 0, false, emptyList())
        set(value) {
            field = value
            setupUI(value)
        }

    constructor(calenderDay: CalenderDayItem, context: Context) : super(context) {
        this.calenderDayItem = calenderDay
    }

    constructor(calenderDay: CalenderDayItem, context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        this.calenderDayItem = calenderDay
    }

    constructor(
        calenderDay: CalenderDayItem,
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.calenderDayItem = calenderDay
    }

    private fun setupUI(calenderDayItem: CalenderDayItem) {

        (calenderDayItem as? CalenderDayItem.Day)?.let {
            binding.day.text = calenderDayItem.day.toString()

            calenderDayItem.calenderDayEvents?.let { list ->
                val size = list.size
                binding.apply {
                    icon1.setup(list.getOrNull(0))
                    icon2.setup(list.getOrNull(1))
                    icon3.setup(list.getOrNull(2))
                    icon4.setup(list.getOrNull(3), size)
                }
            }
        } ?: kotlin.run {

            // make empty view
            binding.root.visibility = INVISIBLE

        }

    }

    private fun ViewCalenderDayEventIconBinding.setup(
        calenderDayEvent: CalenderDayEvent? = null,
        eventCount: Int = 0
    ) {
        calenderDayEvent?.let {
            eventIconText.text = it.name
            eventIcon.setCardBackgroundColor(it.background)
            if (it.count > 1) {
                notificationBadgeNumber.text = it.count.toString()
            } else {
                notificationBadge.visibility = View.INVISIBLE
            }
        }
        if (eventCount > 4) {
            eventIconText.text = "+$eventCount"
            eventIcon.setCardBackgroundColor(R.drawable.background_calender_day_event_more)
        }

    }

}


sealed class CalenderDayItem {
    class Day(val events: List<EventItemEntry>) : CalenderDayItem()
    object Fill : CalenderDayItem()
}
