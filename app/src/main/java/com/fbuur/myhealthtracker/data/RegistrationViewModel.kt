package com.fbuur.myhealthtracker.data

import android.app.Application
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.pages.events.EventItemEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application): AndroidViewModel(application) {

    private val readAllRegistrations: LiveData<List<Registration>>
    private val readAllTemplates: LiveData<List<Template>>

//    val readAllEventItemEntries: LiveData<List<EventItemEntry>>


    private val repository: RegistrationRepository

    init {
        val registrationDao = TrackingDatabase.getTrackingDatabase(application).registrationDao()
        repository = RegistrationRepository(registrationDao)

        readAllRegistrations = repository.readAllRegistrations
        readAllTemplates = repository.readAllTemplates

//        readAllEventItemEntries = readAllTemplates.switchMap {
            // BRUG SWITCH MAP ELLER MAP TIL AT KOMBINERER REGISTRATIONS OF TEMPLATE SO DER IKKE OPSTÅR RACECONDITIONS
//        }

    }

    fun addRegistration(registration: Registration) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRegistration(registration)
        }
    }

    fun addTemplate(template: Template, templateId: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            templateId(repository.addTemplate(template))
        }
    }

}