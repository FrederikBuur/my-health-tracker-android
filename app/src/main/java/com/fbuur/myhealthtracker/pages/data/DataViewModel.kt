package com.fbuur.myhealthtracker.pages.data

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.data.registration.TrackingRepository
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDay
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderDayType
import com.fbuur.myhealthtracker.pages.data.calendar.calendarview.CalenderEvent
import com.fbuur.myhealthtracker.pages.data.calendar.selectedday.CalendarSelectedDayEvent
import com.fbuur.myhealthtracker.pages.data.compare.CompareFragment
import com.fbuur.myhealthtracker.pages.data.compare.CompareGraphData
import com.fbuur.myhealthtracker.pages.data.compare.CompareGraphEntity
import com.fbuur.myhealthtracker.pages.data.statistics.BarChartView
import com.fbuur.myhealthtracker.pages.data.statistics.StatisticsAverageEntity
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterEntry
import com.fbuur.myhealthtracker.util.toDayMonthYearString
import com.fbuur.myhealthtracker.util.toWeekMonthYear
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

class DataViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: TrackingRepository

    init {
        val registrationDAO = TrackingDatabase.getTrackingDatabase(application).trackingDAO()
        repository = TrackingRepository(registrationDAO)
    }

    // calendar live data
    private val selectedDayDate = MutableLiveData(Date())

    // statistics live data
    private val dataScope = MutableLiveData(DataScope.WEEK)
    private val selectedScopeDate = MutableLiveData(Date())

    // compare live data
//    private val selectedEventsGraphData = MutableLiveData<CompareGraphData?>()
    private val selectedEventTypeIds =
        MutableLiveData<Pair<Pair<Long?, String>,
                Pair<Long?, String>>>()

    // calendar fragment live data
    val calendarDays: LiveData<List<CalenderDay>> =
        Transformations.switchMap(selectedDayDate) {
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(readCalenderDayItemsByMonth())
            }
        }
    val selectedDayEvents: LiveData<List<CalendarSelectedDayEvent>> =
        Transformations.switchMap(selectedDayDate) {
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(readCalenderEventsByDay())
            }
        }

    // statistics fragment live data
    val barChartData: LiveData<Triple<BarChartView.BarChart, List<QuickRegisterEntry>, List<StatisticsAverageEntity>>> =
        Transformations.switchMap(selectedScopeDate) {
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(readStatisticsData())
            }
        }

    // compare fragment live data
    val compareGraphData: LiveData<CompareGraphData> =
        Transformations.switchMap(selectedEventTypeIds) {
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(readCompareGraphData(it))
            }
        }
    val templates: LiveData<List<Template>> = repository.readAllTemplatesLD
    // todo livedata for parameter primary and secondary

    fun getSelectedDate() = selectedDayDate.value!!
    fun setSelectedDate(date: Date) {
        selectedDayDate.value = date
    }

    fun getDataScope() = dataScope.value!!
    fun setDataScope(scope: DataScope) {
        if (scope != dataScope.value) {
            dataScope.value = scope
            setSelectedScopeDataDate(Date())
        }
    }

    fun getSelectedScopedDate() = selectedScopeDate.value!!
    fun getSelectedScopedDateString() = when (getDataScope()) {
        DataScope.DAY -> {
            getSelectedScopedDate().toDayMonthYearString()
        }
        DataScope.WEEK -> {
            getSelectedScopedDate().toWeekMonthYear()
        }
        else -> {
            getSelectedScopedDate().toDayMonthYearString()
        }
    }

    fun setSelectedScopeDataDate(date: Date) {
        selectedScopeDate.value = date
    }

    fun setPrimarySelectedEventTypeIds(pair: Pair<Long?, String>) {
        this.selectedEventTypeIds.value = this.selectedEventTypeIds.value?.copy(first = pair)
    }

    fun setSecondarySelectedEventTypeIds(pair: Pair<Long?, String>) {
        this.selectedEventTypeIds.value = this.selectedEventTypeIds.value?.copy(second = pair)
    }

    // calendar page
    private suspend fun readCalenderDayItemsByMonth(
    ): List<CalenderDay> {

        //todo use getfromandtodates
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

        //todo use getfromandtodates
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
    private suspend fun readStatisticsData(
    ): Triple<BarChartView.BarChart, List<QuickRegisterEntry>, List<StatisticsAverageEntity>> {

        // set from- to date time todo use getfromandtodates
        val c = Calendar.getInstance()
        c.time = this.selectedScopeDate.value ?: Date()
        c.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE)
        c.clear(Calendar.SECOND)
        c.clear(Calendar.MILLISECOND)

        val fromDate: Date
        val toDate: Date

        when (this.dataScope.value) {
            DataScope.DAY -> {
                fromDate = c.time
                c.add(Calendar.DAY_OF_MONTH, 1)
                toDate = c.time
                c.add(Calendar.DAY_OF_MONTH, -1)
            }
            DataScope.WEEK -> {
                c.set(Calendar.DAY_OF_WEEK, 2)
                fromDate = c.time
                c.add(Calendar.WEEK_OF_MONTH, 1)
                toDate = c.time
                c.add(Calendar.WEEK_OF_MONTH, -1)
            }
            DataScope.MONTH -> {
                c.set(Calendar.DAY_OF_MONTH, 1)
                fromDate = c.time
                c.add(Calendar.MONTH, 1)
                toDate = c.time
                c.add(Calendar.MONTH, -1)
            }
            DataScope.YEAR -> {
                c.set(Calendar.MONTH, 1)
                c.set(Calendar.DAY_OF_MONTH, 1)
                fromDate = c.time
                c.add(Calendar.YEAR, 1)
                toDate = c.time
                c.add(Calendar.YEAR, -1)
            }
            else -> {
                throw Exception("Scope not supported")
            }
        }

        // read add registrations win the given time period
        val registrations = repository.readRegistrationByTime(fromDate.time, toDate.time)

        var startFilter = fromDate
        var endFilter = CalendarManager.getNextAsDate(fromDate, this.dataScope.value!!.offsetType)
        c.time = endFilter


        val barGroups: ArrayList<List<BarChartView.BarGroupEntity>> = arrayListOf()
        val templates = arrayListOf<Template>()
        val statAverageEntities = arrayListOf<StatisticsAverageEntity>()
        var maxYAxisValue = 0

        // map registrations into list of bar group entities
        while (endFilter.time <= toDate.time) {

            val barGroupEntities = arrayListOf<BarChartView.BarGroupEntity>()

            // sort registrations into bar groups
            registrations.filter { r ->
                r.date > startFilter && r.date < endFilter
            }.also { subList ->

                // sort bar groups into each type of registration
                val grouped = subList.groupBy {
                    it.temId
                }.values

                if (grouped.isNotEmpty()) {
                    grouped.forEach { list ->
                        if (list.isNotEmpty()) {
                            val template = repository.readTemplateById(list.first().temId)
                            if (list.size > maxYAxisValue) {
                                maxYAxisValue = list.size
                            }
                            templates.add(template)
                            barGroupEntities.add(
                                BarChartView.BarGroupEntity(
                                    temId = template.id,
                                    color = Color.parseColor(template.color),
                                    value = list.size
                                )
                            )
                        }
                    }
                }

                barGroups.add(barGroupEntities.toList())

            }
            startFilter = endFilter
            c.time
            c.add(this.dataScope.value!!.offsetType, 1)
            c.time
            endFilter = c.time
        }

        // make x axis title list
        val xAxisTitles: List<String> = when (this.dataScope.value) {
            DataScope.DAY -> {
                listOf(
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                    "11", "12", "13", "14", "15", "16", "17", "18", "19",
                    "20", "21", "22", "23", "24"
                )
            }
            DataScope.WEEK -> {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            }
            DataScope.MONTH -> {
                listOf("Week 1", "Week 2", "Week 3", "Week 4")
            }
            DataScope.YEAR -> {
                listOf(
                    "Jan", "Feb", "Mar", "Apr", "Maj", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )
            }
            else -> {
                throw Exception("Data scope not supported")
            }
        }

        // make y axis title list
        val yAxisTitles = (1..maxYAxisValue).toList().reversed()

        val filterItems = templates.distinct().map { t ->

            val v1 = repository.readAllRegistrationCountByTemplateAndTime(
                temId = t.id,
                fromDate = fromDate.time,
                toDate = toDate.time
            )
            val tempFrom =
                CalendarManager.getPreviousAsDateScoped(fromDate, this.getDataScope()).time
            val v2 = repository.readAllRegistrationCountByTemplateAndTime(
                temId = t.id,
                fromDate = tempFrom,
                toDate = fromDate.time
            )

            statAverageEntities.add(
                StatisticsAverageEntity(
                    id = t.id,
                    name = t.name.capitalize(Locale.ROOT),
                    color = Color.parseColor(t.color),
                    title1 = "This ${
                        this.getDataScope().name.toLowerCase(Locale.getDefault())
                            .capitalize(Locale.getDefault())
                    }",
                    value1 = v1.toString(),
                    title2 = "Last ${
                        this.getDataScope().name.toLowerCase(Locale.getDefault())
                            .capitalize(Locale.getDefault())
                    }",
                    value2 = v2.toString()
                )
            )

            QuickRegisterEntry(
                id = t.id.toString(),
                temId = t.id,
                name = t.name,
                color = t.color,
                templateTypes = emptyList()
            )
        }

        return Triple(
            BarChartView.BarChart(
                barGroups = barGroups,
                xAxisTitles = xAxisTitles,
                yAxisTitles = yAxisTitles,
                scope = getDataScope()
            ),
            filterItems,
            statAverageEntities // todo populate this list
        )
    }

    // compare page
    private suspend fun readCompareGraphData(
        pair: Pair<
                Pair<Long?, String>,
                Pair<Long?, String>
                >
    ): CompareGraphData {
        val c = Calendar.getInstance()
        val dates = getFromAndToDate(c)
        val fromDate = dates.first
        val toDate = dates.second

        // get all registration by temId and time for primary graph data
        val primaryGraph: CompareGraphEntity? = pair.first.first?.let { temId ->
            getCompareGraphEntity(
                temId = temId,
                valueOfInterest = pair.first.second,
                c = c,
                fromDate = fromDate,
                toDate = toDate
            )
        }

        // get all registration by temId and time for secondary graph data
        val secondaryGraph: CompareGraphEntity? = pair.second.first?.let { temId ->
            getCompareGraphEntity(
                temId = temId,
                valueOfInterest = pair.second.second,
                c = c,
                fromDate = fromDate,
                toDate = toDate
            )
        }

        return CompareGraphData(
            scope = this@DataViewModel.getDataScope(),
            graphs = Pair(
                first = primaryGraph,
                second = secondaryGraph
            )
        )

    }

    private suspend fun getCompareGraphEntity(
        temId: Long,
        valueOfInterest: String,
        c: Calendar,
        fromDate: Date,
        toDate: Date
    ): CompareGraphEntity {
        val template = repository.readTemplateById(temId)

        val dataPoints = arrayListOf<Int?>()

        var startFilter = fromDate
        var endFilter = CalendarManager.getNextAsDate(
            fromDate,
            this@DataViewModel.dataScope.value!!.offsetType
        )
        c.time = endFilter

        // collect data points for each hour/day/week/month
        while (endFilter.time <= toDate.time) {
            val registrationsPrimary = repository.readAllRegistrationsByTemplateAndTime(
                temId,
                startFilter.time,
                endFilter.time
            )

            // determine data points
            // if specified, get parameters with given value of interest
            if (valueOfInterest == CompareFragment.EVENT_COUNT_AS_INTEREST) {
                dataPoints.add(if (registrationsPrimary.isNotEmpty()) registrationsPrimary.size else null)
            } else {
                // get parameters by value of interest
                var dataPoint: Int? = null
                registrationsPrimary.forEach { r ->
                    val parameters = repository.readAllParametersByRegIdAndParameterName(
                        r.id,
                        valueOfInterest
                    )
                    parameters.forEach { p ->
                        p.getValueOfInterest()?.let { v ->
                            dataPoint?.let { dataPoint?.plus(v) } ?: run { dataPoint = v }
                        }
                    }
                }
                dataPoints.add(dataPoint)
            }

            startFilter = endFilter
            c.add(this@DataViewModel.dataScope.value!!.offsetType, 1)
            endFilter = c.time
        }

        return CompareGraphEntity(
            temId = temId,
            color = Color.parseColor(template.color),
            valueOfInterestTitle = valueOfInterest,
            dataPoints = dataPoints
        )
    }


    // util functions
    private fun getFromAndToDate(c: Calendar): Pair<Date, Date> {
        // set from- to date time
        c.time = this@DataViewModel.selectedScopeDate.value ?: Date()
        c.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE)
        c.clear(Calendar.SECOND)
        c.clear(Calendar.MILLISECOND)

        val fromDate: Date
        val toDate: Date

        when (this@DataViewModel.dataScope.value) {
            DataScope.DAY -> {
                fromDate = c.time
                c.add(Calendar.DAY_OF_MONTH, 1)
                toDate = c.time
                c.add(Calendar.DAY_OF_MONTH, -1)
            }
            DataScope.WEEK -> {
                c.set(Calendar.DAY_OF_WEEK, 2)
                fromDate = c.time
                c.add(Calendar.WEEK_OF_MONTH, 1)
                toDate = c.time
                c.add(Calendar.WEEK_OF_MONTH, -1)
            }
            DataScope.MONTH -> {
                c.set(Calendar.DAY_OF_MONTH, 1)
                fromDate = c.time
                c.add(Calendar.MONTH, 1)
                toDate = c.time
                c.add(Calendar.MONTH, -1)
            }
            DataScope.YEAR -> {
                c.set(Calendar.MONTH, 1)
                c.set(Calendar.DAY_OF_MONTH, 1)
                fromDate = c.time
                c.add(Calendar.YEAR, 1)
                toDate = c.time
                c.add(Calendar.YEAR, -1)
            }
            else -> {
                throw Exception("Scope not supported")
            }
        }
        return Pair(fromDate, toDate)
    }

    enum class DataScope(val offsetType: Int) {
        DAY(Calendar.HOUR_OF_DAY),
        WEEK(Calendar.DAY_OF_WEEK),
        MONTH(Calendar.WEEK_OF_MONTH),
        YEAR(Calendar.MONTH)
    }

}

