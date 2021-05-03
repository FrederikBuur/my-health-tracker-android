package com.fbuur.myhealthtracker.pages.data.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.data.model.mapToEventParameterList
import com.fbuur.myhealthtracker.data.registration.RegistrationRepository
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: RegistrationRepository

    init {
        val registrationDAO = TrackingDatabase.getTrackingDatabase(application).registrationDao()
        repository = RegistrationRepository(registrationDAO)
    }

    fun readCalenderDayItemsByMonth(monthOffset: Int, results: (List<CalenderDayItem>) -> Unit) {

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

        // get start of the next month
        c.add(Calendar.MONTH, 1)
        val toDateMillis = c.timeInMillis

        viewModelScope.launch(Dispatchers.IO) {
            val calenderDayItems = List<>

            val eventItems =
                repository.readRegistrationByMonth(fromDateMillis, toDateMillis).map { r ->
                    val template = repository.readTemplateById(r.temId)
                    EventItemEntry(
                        id = "${r.id}:${template.id}",
                        name = template.name,
                        date = r.date,
                        iconColor = template.color,
                        type = r.type,
                        eventParameterList = repository
                            .readAllParametersByRegId(r.id)
                            .mapToEventParameterList()
                    )

                }

            // if registration on day x add EventItemEntry to "Day" obj
            // else add empty view "Fill"


            results()

        }


    }

}