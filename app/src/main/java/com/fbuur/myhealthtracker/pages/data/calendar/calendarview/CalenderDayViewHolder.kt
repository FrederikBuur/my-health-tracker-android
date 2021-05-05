package com.fbuur.myhealthtracker.pages.data.calendar.calendarview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ViewCalenderDayBinding
import com.fbuur.myhealthtracker.databinding.ViewCalenderDayEventIconBinding
import com.fbuur.myhealthtracker.util.getInitials

class CalenderDayViewHolder(
    private val itemBinding: ViewCalenderDayBinding,
    private val onDaySelected: (Int) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(calenderDay: CalenderDay) {
        when (calenderDay.calenderDayType) {
            CalenderDayType.DAY -> {
                setupDay(calenderDay)
            }
            CalenderDayType.WHITESPACE -> {
                setupWhiteSpace(calenderDay)
            }
            else -> {
                throw NotImplementedError("not supported: ${calenderDay.calenderDayType}")
            }
        }
    }

    private fun setupDay(calenderDay: CalenderDay) {
        itemBinding.apply {

            setDaySelected(calenderDay.isSelected)

            day.text = calenderDay.day.toString()
            daySelectedIndicator.text = calenderDay.day.toString()

            icon1.setup(calenderDay.events.getOrNull(0))
            icon2.setup(calenderDay.events.getOrNull(1))
            icon3.setup(calenderDay.events.getOrNull(2))
            icon4.setup(calenderDay.events.getOrNull(3), calenderDay.events.size)
            root.setOnClickListener {
                onDaySelected(calenderDay.day)
                calenderDay.isSelected = true
                setDaySelected(true)
            }
        }
    }

    fun setDaySelected(isSelected: Boolean) {
        itemBinding.apply {
            if (isSelected) {
                day.visibility = View.INVISIBLE
                daySelectedIndicator.visibility = View.VISIBLE
            } else {
                day.visibility = View.VISIBLE
                daySelectedIndicator.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupWhiteSpace(calenderDay: CalenderDay) {
        itemBinding.root.visibility = View.GONE
    }

    private fun ViewCalenderDayEventIconBinding.setup(
        calenderEvent: CalenderEvent? = null,
        eventCount: Int = 0
    ) {
        calenderEvent?.let {

            // setup text and background
            if (eventCount > 4) {
                moreIconText.text = "+${eventCount.minus(3)}"
                moreIcon.visibility = View.VISIBLE
                eventIcon.visibility = View.INVISIBLE
            } else {
                eventIconText.text = it.name.getInitials()
                eventIcon.setCardBackgroundColor(it.backgroundColor)
                moreIcon.visibility = View.INVISIBLE
                eventIcon.visibility = View.VISIBLE
            }

            // setup badge
            if (it.badgeCount > 1) {
                notificationBadgeNumber.text = it.badgeCount.toString()
            } else {
                notificationBadge.visibility = View.INVISIBLE
            }
        } ?: kotlin.run {
            root.visibility = View.INVISIBLE
        }

    }

    companion object {

        var dayOffset = 0
        var selectedDay = 0
        set(value) {
            field = dayOffset + value
        }

    }

}

class CalenderDay(
    val day: Int,
    val calenderDayType: CalenderDayType,
    val events: List<CalenderEvent>,
    var isSelected : Boolean
)

class CalenderEvent(
    val id: Long,
    val tempId: Long,
    val name: String,
    val backgroundColor: Int,
    val badgeCount: Int
)

enum class CalenderDayType {
    WHITESPACE, DAY
}

