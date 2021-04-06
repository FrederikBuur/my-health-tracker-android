package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter

class RegistrationRepository(private val registrationDAO: RegistrationDAO) {


    // live data
    val readAllRegistrationsLD: LiveData<List<Registration>> =
        registrationDAO.readAllRegistrationsLD()

    val readAllTemplatesLD: LiveData<List<Template>> =
        registrationDAO.readAllTemplatesLD()

    // suspend read
    suspend fun readAllRegistrations() =
        registrationDAO.readAllRegistrations()

    suspend fun readAllParametersByRegId(regId: Long): List<Parameter> {
        val notes = registrationDAO.readAllNoteByRegId(regId)
        val sliders = registrationDAO.readAllSliderByRegId(regId)

        val parameterList = ArrayList<Parameter>()
        parameterList.addAll(notes)
        parameterList.addAll(sliders)

        return parameterList.toList()
    }

    suspend fun readAllTemplates() =
        registrationDAO.readAllTemplates()

    suspend fun readTemplateById(id: Long) =
        registrationDAO.readTemplateById(id)

    // suspend update
    suspend fun updateTemplate(template: Template) =
        registrationDAO.updateTemplate(template)

    // suspend delete
    suspend fun deleteTemplateById(id: Long) =
        registrationDAO.deleteTemplateById(id)

    suspend fun deleteRegistrationById(id: Long) =
        registrationDAO.deleteRegistrationById(id)

    suspend fun deleteParameterById(id: Long, type: ParameterType) =
        when(type) {
            ParameterType.SLIDER -> registrationDAO.deleteParameterSlider(id)
            ParameterType.NOTE -> registrationDAO.deleteParameterNote(id)
            ParameterType.LOCATION -> throw NotImplementedError("location not added")
            ParameterType.BINARY -> throw NotImplementedError("binary not added")
        }

    // suspend write
    suspend fun addRegistration(registration: Registration) =
        registrationDAO.addRegistration(registration)

    suspend fun addTemplate(template: Template): Long =
        registrationDAO.addTemplate(template)

    suspend fun addParameter(parameter: Parameter): Long {
        return when(parameter) {
            is Parameter.Note -> {
                registrationDAO.addParameterNote(parameter)
            }
            is Parameter.Slider -> {
                registrationDAO.addParameterSlider(parameter)
            }
        }
    }
}