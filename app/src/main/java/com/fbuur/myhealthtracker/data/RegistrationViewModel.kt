package com.fbuur.myhealthtracker.data

import android.app.Application
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.model.*
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RegistrationRepository

    val readAllEventItemEntries: LiveData<List<EventItemEntry>>
    val readAllQuickRegisterEntries: LiveData<List<QuickRegisterEntry>>

    init {
        val registrationDao = TrackingDatabase.getTrackingDatabase(application).registrationDao()
        repository = RegistrationRepository(registrationDao)

        readAllEventItemEntries = setupEventItemEntryLiveData()
        readAllQuickRegisterEntries = setupQuickRegisterEntryLiveData()

    }

    //    https://developer.android.com/topic/libraries/architecture/coroutines#livedata
    //    https://medium.com/androiddevelopers/livedata-with-coroutines-and-flow-part-ii-launching-coroutines-with-architecture-components-337909f37ae7
    private fun setupEventItemEntryLiveData() =
        repository.readAllRegistrationsLD.switchMap { registrations ->
            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                val templates = repository.readAllTemplates()
                emit(mapToEventItemEntities(registrations, templates))
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
                    eventParameterList = mapToEventParameterList(
                        repository.readAllParametersByRegId(
                            registration.id
                        )
                    )
                )
            } ?: run {
                throw Exception(" test123 cant find template id: ${registration.temId}, for registration id: ${registration.id}")
            }
        }
    }

    private fun mapToEventParameterList(
        paramList: List<Parameter>
    ): List<EventItemParameter> {
        return paramList.map { p ->
            when (p) {
                is Parameter.Note -> {
                    EventItemParameter.Note(
                        id = p.id,
                        regId =  p.regId,
                        title = p.title,
                        type = ParameterType.NOTE,
                        description = p.description
                    )
                }
                is Parameter.Slider -> {
                    EventItemParameter.Slider(
                        id = p.id,
                        regId = p.regId,
                        title = p.title,
                        type = ParameterType.SLIDER,
                        value = p.value,
                        lowest = p.lowestValue,
                        highest = p.highestValue
                    )
                }
                else -> {
                    // todo binary and location not implemented yet
                    throw NotImplementedError("Binary and Location not implemented yet: $p")
                }
            }
        }
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

    fun deleteParameterById(id: Long, type: ParameterType) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteParameterById(id, type)
        }
    }

    companion object {
        const val QUICK_REGISTER_NOTE = "quick_register_note"
    }

}