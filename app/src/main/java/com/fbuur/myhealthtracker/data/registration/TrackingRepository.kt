package com.fbuur.myhealthtracker.data.registration

import androidx.lifecycle.LiveData
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import java.util.*
import kotlin.collections.ArrayList

class TrackingRepository(private val trackingDAO: TrackingDAO) {


    // live data
    val readAllRegistrationsLD: LiveData<List<Registration>> =
        trackingDAO.readAllRegistrationsLD()

    val readAllTemplatesLD: LiveData<List<Template>> =
        trackingDAO.readAllTemplatesLD()

    // suspend read
    suspend fun readAllRegistrations() =
        trackingDAO.readAllRegistrations()

    suspend fun readRegistrationByTime(fromDate: Long, toDate: Long) =
        trackingDAO.readRegistrationByTime(fromDate, toDate)

    suspend fun readAllRegistrationCountByTemplateAndTime(temId: Long, fromDate: Long, toDate: Long) =
        trackingDAO.readRegistrationCountByTemplateAndTime(temId, fromDate, toDate)

    suspend fun readAllRegistrationsByTemplateAndTime(temId: Long, fromDate: Long, toDate: Long) =
        trackingDAO.readRegistrationsByTemplateAndTime(temId, fromDate, toDate)

    suspend fun readAllParametersByRegId(regId: Long): List<Parameter> {
        val notes = trackingDAO.readAllNoteByRegId(regId)
        val sliders = trackingDAO.readAllSliderByRegId(regId)

        val parameterList = ArrayList<Parameter>()
        parameterList.addAll(sliders)
        parameterList.addAll(notes)

        return parameterList.toList()
    }

    suspend fun readAllParametersByRegIdAndParameterName(regId: Long, name: String): List<Parameter> {
        val notes = trackingDAO.readAllNoteByRegIdParameterName(regId, name)
        val sliders = trackingDAO.readAllSliderByRegIdAndParameterName(regId, name)

        val parameterList = ArrayList<Parameter>()
        parameterList.addAll(sliders)
        parameterList.addAll(notes)

        return parameterList
    }

    suspend fun readAllTemplates() =
        trackingDAO.readAllTemplates()

    suspend fun readAllTemplatesByTime(fromDate: Long, toDate: Long) =
        trackingDAO.readAllTemplatesByTime(fromDate, toDate)

    suspend fun readTemplateById(id: Long) =
        trackingDAO.readTemplateById(id)

    // suspend update
    suspend fun updateTemplate(template: Template) =
        trackingDAO.updateTemplate(template)

    suspend fun updateRegistrationLastUsedDate(regId: Long) =
        trackingDAO.readRegistrationById(regId).let {
            trackingDAO.updateRegistration(
                Registration(
                    id = it.id,
                    temId = it.temId,
                    date = it.date,
                    type = it.type,
                    lastModified = Date()
                )
            )
        }

    suspend fun updateParameter(parameter: Parameter) =
        when (parameter) {
            is Parameter.Note -> {
                trackingDAO.updateParameterNote(parameter)
            }
            is Parameter.Slider -> {
                trackingDAO.updateParameterSlider(parameter)
            }
            else -> {
                throw NotImplementedError("parameter type not implemented: $parameter")
            }
        }

    // suspend delete
    suspend fun deleteTemplateById(id: Long) =
        trackingDAO.deleteTemplateById(id)

    suspend fun deleteRegistrationById(id: Long) =
        trackingDAO.deleteRegistrationById(id)

    suspend fun deleteParameterById(id: Long, type: ParameterType) =
        when(type) {
            ParameterType.SLIDER -> trackingDAO.deleteParameterSlider(id)
            ParameterType.NOTE -> trackingDAO.deleteParameterNote(id)
            ParameterType.LOCATION -> throw NotImplementedError("location not added")
            ParameterType.BINARY -> throw NotImplementedError("binary not added")
        }

    // suspend write
    suspend fun addRegistration(registration: Registration) =
        trackingDAO.addRegistration(registration)

    suspend fun addTemplate(template: Template): Long =
        trackingDAO.addTemplate(template)

    suspend fun addParameter(parameter: Parameter): Long {
        return when(parameter) {
            is Parameter.Note -> {
                trackingDAO.addParameterNote(parameter)
            }
            is Parameter.Slider -> {
                trackingDAO.addParameterSlider(parameter)
            }
        }
    }
}