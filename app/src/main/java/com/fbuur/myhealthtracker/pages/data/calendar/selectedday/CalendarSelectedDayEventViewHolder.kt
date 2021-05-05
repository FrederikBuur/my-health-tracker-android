package com.fbuur.myhealthtracker.pages.data.calendar.selectedday

import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemCalendarSelectedDayBinding
import com.fbuur.myhealthtracker.databinding.ItemCalendarSelectedDayDataBinding
import com.fbuur.myhealthtracker.util.toHourMinString
import java.util.*

class CalendarSelectedDayEventViewHolder(
    private val itemBinding: ItemCalendarSelectedDayBinding,
    private val onCreateAdditionalData: () -> ItemCalendarSelectedDayDataBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(
        calendarSelectedDayEvent: CalendarSelectedDayEvent
    ) {
        itemBinding.apply {
            eventName.text = calendarSelectedDayEvent.name
            eventDate.text = calendarSelectedDayEvent.date.toHourMinString()
            eventIcon.setCardBackgroundColor(calendarSelectedDayEvent.iconColor)
            calendarSelectedDayEvent.additionalData.forEach { text ->
                additionalData.addView(
                    onCreateAdditionalData().apply {
                        dataText.text = text
                    }.root
                )
            }
        }
    }

}

class CalendarSelectedDayEvent(
    val name: String,
    val iconColor: Int,
    val date: Date,
    val additionalData: List<String>
)