package com.fbuur.myhealthtracker.pages.data.calendar

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.data.registration.RegistrationRepository
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDay
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDayType
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderEvent
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayEvent
import kotlinx.coroutines.Dispatchers
import java.util.*

class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: RegistrationRepository

    init {
        val registrationDAO = TrackingDatabase.getTrackingDatabase(application).registrationDao()
        repository = RegistrationRepository(registrationDAO)
    }

    private val selectedDayDate = MutableLiveData(Date())

    val calendarDays: LiveData<List<CalenderDay>> =
        Transformations.switchMap(selectedDayDate) { selectedDate ->
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(readCalenderDayItemsByMonth())
            }
        }

    val selectedDayEvents: LiveData<List<CalendarSelectedDayEvent>> =
        Transformations.switchMap(selectedDayDate) { selectedDate ->
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(readCalenderEventsByDay())
            }
        }

    fun getSelectedDate() = selectedDayDate.value
    fun setSelectedDate(date: Date) {
        selectedDayDate.value = date
    }

    private suspend fun readCalenderDayItemsByMonth(
    ): List<CalenderDay> {

        val c = Calendar.getInstance()
        c.time = this.selectedDayDate.value ?: Date()
        c.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE)
        c.clear(Calendar.SECOND)
        c.clear(Calendar.MILLISECOND)

        // get start of current month
        c.set(Calendar.DAY_OF_MONTH, 1)
        val fromDateMillis = c.timeInMillis

        // get days in month
        val daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH)

        // sunday = 1. converted to monday = 1
        val firstWeekDayInMonth =
            if (c.get(Calendar.DAY_OF_WEEK) == 1) 7 else c.get(Calendar.DAY_OF_WEEK) - 1

        // get start of the next month
        c.add(Calendar.MONTH, 1)
        val toDateMillis = c.timeInMillis

        val calenderDayList = arrayListOf<CalenderDay>()

        // fill with whitespace events
        for (i in 1 until firstWeekDayInMonth) {
            calenderDayList.add(
                CalenderDay("", -1, CalenderDayType.WHITESPACE, emptyList(), false)
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

                val isSelected = CalendarManager.isDaySameDateDay(
                    i,
                    this.selectedDayDate.value ?: Date()
                )

                // add calender day to final list
                calenderDayList.add(
                    CalenderDay(
                        id = regDayList.map { it.id }.toString(),
                        day = i,
                        calenderDayType = CalenderDayType.DAY,
                        events = calenderEvents,
                        isSelected = isSelected
                    )
                )
            }
        }
        return calenderDayList
    }

    private suspend fun readCalenderEventsByDay(
    ): List<CalendarSelectedDayEvent> {

        val c = Calendar.getInstance()
        c.time = this.selectedDayDate.value ?: Date()
        c.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE)
        c.clear(Calendar.SECOND)
        c.clear(Calendar.MILLISECOND)

        // get start of wanted day
        c.get(Calendar.DAY_OF_MONTH)
        val startDateMillis = c.timeInMillis

        // get end of wanted day
        c.add(Calendar.DAY_OF_MONTH, 1)
        val endDateMillis = c.timeInMillis

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
        return calenderDayEvents
    }

}

object CalendarManager {
    private val cal = Calendar.getInstance()

    fun clearCalendarMeta() {
        cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE)
        cal.clear(Calendar.SECOND)
        cal.clear(Calendar.MILLISECOND)
    }

    fun getPreviousMonthAsDate(date: Date): Date {
        cal.time = date
        cal.add(Calendar.MONTH, -1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.time
    }

    fun getNextMonthAsDate(date: Date): Date {
        cal.time = date
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.time
    }

    fun getDateAtDay(day: Int): Date {
        clearCalendarMeta()
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.time
    }

    fun isDaySameDateDay(
        day: Int,
        date: Date,
    ): Boolean {
        cal.time = date
        clearCalendarMeta()
        val dateAsWeekDay = cal.get(Calendar.DAY_OF_MONTH)
        return dateAsWeekDay == day
    }
}