package com.fbuur.myhealthtracker.data

import android.app.Application
import androidx.lifecycle.*
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
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
            mapToQuickRegisterEntries(templates)
        }

    private fun mapToEventItemEntities(
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
                    eventParameterList = emptyList() // todo
                )
            } ?: run {
                throw Exception(" test123 cant find template id: ${registration.temId}, for registration id: ${registration.id}")
            }
        }
    }

    private fun mapToQuickRegisterEntries(
        templates: List<Template>
    ): List<QuickRegisterEntry> {

        val noteEntry = QuickRegisterEntry(
            id = QUICK_REGISTER_NOTE,
            temId = -1,
            name = "note",
            color = "",
            templateTypes = emptyList()
        )

        val temList = templates.mapNotNull { t ->
            if (t.id == -1L) {
                null
            } else {
                QuickRegisterEntry(
                    id = "${t.id}${t.name}",
                    temId = t.id,
                    name = t.name,
                    color = t.color,
                    templateTypes = emptyList() // todo
                )
            }
        }

        val list = arrayListOf(noteEntry)
        list.addAll(temList)
        return list
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

    companion object {
        const val QUICK_REGISTER_NOTE = "quick_register_note"
    }

}