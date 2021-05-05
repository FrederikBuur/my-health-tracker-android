package com.fbuur.myhealthtracker.pages.data.calendar

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.data.registration.RegistrationRepository
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDay
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDayType
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDayViewHolder
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderEvent
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: RegistrationRepository

    private var selectedDay = Calendar.getInstance()

    fun setSelectedDayOfMonth(selectedDay: Int) {
        this.selectedDay.set(Calendar.DAY_OF_MONTH, selectedDay)
    }

    fun getSelectedDayAsDate() = this.selectedDay.time

    init {
        val registrationDAO = TrackingDatabase.getTrackingDatabase(application).registrationDao()
        repository = RegistrationRepository(registrationDAO)
    }

    fun readCalenderDayItemsByMonth(
        monthOffset: Int,
        results: (List<CalenderDay>) -> Unit
    ) {

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE)
        c.clear(Calendar.SECOND)
        c.clear(Calendar.MILLISECOND)

        // get start of current month
        c.set(Calendar.DAY_OF_MONTH, 1)
        // get start of the wanted month
        c.add(Calendar.MONTH, monthOffset)
        val fromDateMillis = c.timeInMillis

//        val temp = c.get(Calendar.DAY_OF_MONTH)
//        temp
//        val temp2 = c.get(Calendar.DAY_OF_WEEK_IN_MONTH)
//        temp2
//        val temp3 = c.get(Calendar.DAY_OF_WEEK)
//        temp3
//        val temp4 = c.firstDayOfWeek
//        temp4
//        val temp5 = c.minimalDaysInFirstWeek
//        temp5
//        val temp1 = c.time.toDateString()
//        temp1

        // get days in month
        val daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        // sunday = 1. converted to monday = 1
        val firstWeekDayInMonth =
            if (c.get(Calendar.DAY_OF_WEEK) == 1) 7 else c.get(Calendar.DAY_OF_WEEK) - 1

        CalenderDayViewHolder.dayOffset = firstWeekDayInMonth - 1

        // get start of the next month
        c.add(Calendar.MONTH, 1)
        val toDateMillis = c.timeInMillis
//        val temp6 = c.time.toDateString()
//        temp6

        viewModelScope.launch(Dispatchers.IO) {

            val calenderDayList = arrayListOf<CalenderDay>()

            // fill with whitespace events
            for (i in 1 until firstWeekDayInMonth) {
                calenderDayList.add(
                    CalenderDay(-1, CalenderDayType.WHITESPACE, emptyList(), false)
                )
            }

            // create calendar day items
            val registrations = repository.readRegistrationByTime(fromDateMillis, toDateMillis)

            for (i in 1..daysInMonth) {

                registrations.filter { r ->
                    // filter all events registered i'th day
                    c.time = r.date
                    c.get(Calendar.DAY_OF_MONTH) == i
                }.also { regDayList ->

                    val calenderEvents = if (regDayList.isNotEmpty()) {
                        val distinctList = regDayList.distinctBy { it.temId }
                        // map of calender events for i'th day
                        distinctList.map { distinctReg ->
                            val regOfType = regDayList.filter { it.temId == distinctReg.temId }
                            val template = repository.readTemplateById(distinctReg.temId)
                            CalenderEvent(
                                id = distinctReg.id,
                                tempId = distinctReg.temId,
                                name = template.name,
                                backgroundColor = Color.parseColor(template.color),
                                badgeCount = regOfType.size
                            )
                        }

                    } else {
                        emptyList()
                    }

                    val curIIsToday =
                        this@CalendarViewModel.selectedDay.get(Calendar.DAY_OF_MONTH) == i
                    if (curIIsToday) {
                        CalenderDayViewHolder.selectedDay = i-1
                    } else {

                    }

                    // add calender day to final list
                    calenderDayList.add(
                        CalenderDay(
                            day = i,
                            calenderDayType = CalenderDayType.DAY,
                            events = calenderEvents,
                            isSelected = curIIsToday
                        )
                    )

                }
            }

            withContext(Dispatchers.Main) {
                results(calenderDayList)
            }
        }

    }

    fun readCalenderEventsByDay(
        results: (List<CalendarSelectedDayEvent>) -> Unit
    ) {

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE)
        c.clear(Calendar.SECOND)
        c.clear(Calendar.MILLISECOND)

        // get wanted month
        c.set(Calendar.MONTH, this.selectedDay.get(Calendar.MONTH))

        // get start of wanted day
        c.set(Calendar.DAY_OF_MONTH, this.selectedDay.get(Calendar.DAY_OF_MONTH))
        val startDateMillis = c.timeInMillis

        // get end of wanted day
        c.add(Calendar.DAY_OF_MONTH, 1)
        val endDateMillis = c.timeInMillis

        viewModelScope.launch(Dispatchers.IO) {
            val calenderDayEvents = arrayListOf<CalendarSelectedDayEvent>()

            repository.readRegistrationByTime(startDateMillis, endDateMillis).forEach { r ->
                val template = repository.readTemplateById(r.temId)
                val additionalData = arrayListOf<String>()

                repository.readAllParametersByRegId(r.id).forEach { p ->
                    val test = p.toString()
                    additionalData.add(test)
                }

                calenderDayEvents.add(
                    CalendarSelectedDayEvent(
                        name = template.name,
                        iconColor = Color.parseColor(template.color),
                        date = r.date,
                        additionalData = additionalData
                    )
                )
            }

            withContext(Dispatchers.Main) {
                results(calenderDayEvents)
            }
        }

    }


}