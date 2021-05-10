package com.fbuur.myhealthtracker.pages.events

import android.app.Application
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.TrackingDatabase
import com.fbuur.myhealthtracker.data.model.*
import com.fbuur.myhealthtracker.data.registration.TrackingRepository
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import com.fbuur.myhealthtracker.pages.events.eventsentry.toParameter
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrackingRepository

    private var eventList = emptyList<EventItemEntry>()

    val readAllEventItemEntries: LiveData<List<EventItemEntry>>

    //    get() {
//        return _readAllEventItemEntries
//    }
    val readAllQuickRegisterEntries: LiveData<List<QuickRegisterEntry>>

//    private val _readAllEventItemEntries = MutableLiveData<List<EventItemEntry>>()
//    private val _readAllQuickRegisterEntries = MutableLiveData<List<QuickRegisterEntry>>()


    init {
        val registrationDao = TrackingDatabase.getTrackingDatabase(application).trackingDAO()
        repository = TrackingRepository(registrationDao)

//        _readAllEventItemEntries.value = setupEventItemEntryLiveData()
//        _readAllQuickRegisterEntries.value = setupQuickRegisterEntryLiveData()

        readAllEventItemEntries = setupEventItemEntryLiveData()
        readAllQuickRegisterEntries = setupQuickRegisterEntryLiveData()

    }

    //    https://developer.android.com/topic/libraries/architecture/coroutines#livedata
    //    https://medium.com/androiddevelopers/livedata-with-coroutines-and-flow-part-ii-launching-coroutines-with-architecture-components-337909f37ae7
    private fun setupEventItemEntryLiveData() =
        Transformations.switchMap(repository.readAllRegistrationsLD) { registrations ->
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                val templates = repository.readAllTemplates()
                val newList = mapToEventItemEntities(registrations, templates)
                this@RegistrationViewModel.eventList = newList
                emit(newList)
            }
        }

    private fun setupQuickRegisterEntryLiveData() =
        repository.readAllTemplatesLD.map { templates ->
            templates.mapToQuickRegisterEntries()
        }

    private suspend fun mapToEventItemEntities(
        registrations: List<Registration>,
        templates: List<Template>
    ): List<EventItemEntry> {
        return registrations.map { registration ->
            templates.firstOrNull { t -> t.id == registration.temId }?.let { t ->
                EventItemEntry(
                    id = "${registration.id}:${t.id}",
                    name = t.name,
                    date = registration.date,
                    iconColor = t.color,
                    type = registration.type,
                    eventParameterList = repository
                        .readAllParametersByRegId(registration.id)
                        .mapToEventParameterList(),
                    isExpanded = shouldEventBeExpanded("${registration.id}:${t.id}")
                )
            } ?: run {
                throw Exception(" test123 cant find template id: ${registration.temId}, for registration id: ${registration.id}")
            }
        }
    }

    private fun shouldEventBeExpanded(eventId: String) =
        this.eventList.singleOrNull { e -> e.id == eventId }?.isExpanded ?: kotlin.run {
            false
        }

    fun addRegistration(registration: Registration, registrationId: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            registrationId(repository.addRegistration(registration))
        }
    }

    fun addTemplate(template: Template, templateId: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            templateId(repository.addTemplate(template))
        }
    }

    fun addParameter(parameter: Parameter) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addParameter(parameter)
            repository.updateRegistrationLastUsedDate(parameter.regId)
        }
    }

    fun updateTemplateLastUsed(temId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val temp = repository.readTemplateById(temId)
            repository.updateTemplate(
                Template(
                    id = temp.id,
                    name = temp.name,
                    lastUsed = Date(),
                    color = temp.color
                )
            )
        }
    }

    fun updateTemplateName(temId: Long, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val temp = repository.readTemplateById(temId)
            repository.updateTemplate(
                Template(
                    id = temp.id,
                    name = newName,
                    lastUsed = temp.lastUsed,
                    color = temp.color
                )
            )
        }
    }

    fun updateParameter(eventItemParameter: EventItemParameter) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateParameter(
                when (eventItemParameter) {
                    is EventItemParameter.Note -> {
                        eventItemParameter.toParameter()
                    }
                    is EventItemParameter.Slider -> {
                        eventItemParameter.toParameter()
                    }
                    else -> {
                        throw NotImplementedError("parameter not implemented: $eventItemParameter")
                    }
                }
            )
            repository.updateRegistrationLastUsedDate(eventItemParameter.regId)
        }
    }

    fun deleteTemplateById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTemplateById(id)
        }
    }

    fun deleteRegistrationById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRegistrationById(id)
        }
    }

    fun deleteParameterById(id: Long, regId: Long, type: ParameterType) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteParameterById(id, type)
            repository.updateRegistrationLastUsedDate(regId)
        }
    }

    companion object {
        const val QUICK_REGISTER_NOTE = "quick_register_note"
    }

}