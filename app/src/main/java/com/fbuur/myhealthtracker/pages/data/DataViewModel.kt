package com.fbuur.myhealthtracker.pages.data

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.data.registration.TrackingRepository
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDay
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDayType
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderEvent
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayEvent
import com.fbuur.myhealthtracker.pages.data.statistics.BarChartView
import kotlinx.coroutines.Dispatchers
import java.util.*

class DataViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: TrackingRepository

    init {
        val registrationDAO = TrackingDatabase.getTrackingDatabase(application).trackingDAO()
        repository = TrackingRepository(registrationDAO)
    }

    private val selectedDayDate = MutableLiveData(Date())
    private val dataScope = MutableLiveData(DataScope.WEEK)

    // calendar fragment live data
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

    // statistics fragment live data
    val barChartData: LiveData<BarChartView.BarChart> =
        Transformations.switchMap(dataScope) { dataScope ->
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(readBarChartData())
            }
        }

    fun getSelectedDate() = selectedDayDate.value!!
    fun setSelectedDate(date: Date) {
        selectedDayDate.value = date
    }

    fun getDataScope() = dataScope.value
    fun setDataScope(scope: DataScope) {
        dataScope.value = scope
    }

    // calendar page
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
                CalenderDay(
                    "fill-$i-$firstWeekDayInMonth",
                    -1,
                    CalenderDayType.WHITESPACE,
                    emptyList(),
                    false
                )
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

                val tid = "day-$i-${calenderEvents.map { it.id }}"
                // add calender day to final list
                calenderDayList.add(
                    CalenderDay(
                        id = tid,
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

    // statistics page
    private suspend fun readBarChartData(
    ): BarChartView.BarChart {

        // read add registrations win the given time period
        repository.readRegistrationByTime(getSelectedDate().time, CalendarManager.getNextMonthAsDate(getSelectedDate()).time)

        // make sub lists of registrations for each day

        // make x axis title list
        val xAxisTitles = arrayListOf<String>()

        // make y axis title list
        val yAxisTitles = arrayListOf<String>()

        return BarChartView.BarChart(
            barGroups = emptyList(),
            xAxisTitles = xAxisTitles,
            yAxisTitles = yAxisTitles
        )
    }

    enum class DataScope {
        DAY, WEEK, MONTH, YEAR
    }

}

