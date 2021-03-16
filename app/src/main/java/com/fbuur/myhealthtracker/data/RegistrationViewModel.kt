package com.fbuur.myhealthtracker.data

import android.app.Application
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.pages.events.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    val readAllEventItemEntries: LiveData<List<EventItemEntry>>

//    val readAllQuickRegisterEntries: LiveData<QuickRegisterEntry>

    var newTemplateHasBeenAdded = true
    private var didRegistrationsUpdate = false
    private var didTemplatesUpdate = false

    private val repository: RegistrationRepository

    init {
        val registrationDao = TrackingDatabase.getTrackingDatabase(application).registrationDao()
        repository = RegistrationRepository(registrationDao)

        // map event item entries
        var tempRegistrations = emptyList<Registration>()
        var tempTemplates = emptyList<Template>()


//        readAllEventItemEntries = liveData {
//            val registrations = repository.readAllRegistrations()
//            val templates = repository.readAllTemplates()
//            val result = mapToEventItemEntities(registrations, templates)
//            emit(result)
//        }

//        https://developer.android.com/topic/libraries/architecture/coroutines#livedata
//        https://medium.com/androiddevelopers/livedata-with-coroutines-and-flow-part-ii-launching-coroutines-with-architecture-components-337909f37ae7
        readAllEventItemEntries = repository.readAllRegistrationsLD.switchMap { registrations ->
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                val templates = repository.readAllTemplates()
                emit(mapToEventItemEntities(registrations, templates))
            }
        }

    }

    private fun mapToEventItemEntities(
        registrations: List<Registration>,
        templates: List<Template>
    ): List<EventItemEntry> {
        return registrations.map { registration ->
            templates.firstOrNull { t -> t.id == registration.temId }?.let { t ->
                EventItemEntry(
                    id = "${registration.id}${t.id}",
                    name = t.name,
                    date = registration.date,
                    iconColor = t.color,
                    parameterList = emptyList() // todo
                )
            } ?: run {
                throw Exception(" test123 cant find template id: ${registration.temId}, for registration id: ${registration.id}")
            }
        }
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

//    fun <T, K, R> LiveData<T>.combineWith(
//        liveData: LiveData<K>,
//        block: (T?, K?) -> R
//    ): LiveData<R> {
//        val result = MediatorLiveData<R>()
//        result.addSource(this) {
//            result.value = block(this.value, liveData.value)
//        }
//        result.addSource(liveData) {
//            result.value = block(this.value, liveData.value)
//        }
//        return result
//    }

}