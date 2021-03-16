package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template

class RegistrationRepository(private val registrationDAO: RegistrationDAO) {

    val readAllRegistrationsLD: LiveData<List<Registration>> = registrationDAO.readAllRegistrationsLD()

    suspend fun readAllRegistrations() = registrationDAO.readAllRegistrations()
    suspend fun readAllTemplates() = registrationDAO.readAllTemplates()

    suspend fun readTemplateById(id: Long): Template {
        return registrationDAO.readTemplateById(id)
    }

    suspend fun addRegistration(registration: Registration): Long =
        registrationDAO.addRegistration(registration)

    suspend fun addTemplate(template: Template): Long =
        registrationDAO.addTemplate(template)

}