package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template

class RegistrationRepository(private val registrationDAO: RegistrationDAO) {

    val readAllRegistrations: LiveData<List<Registration>> = registrationDAO.readAllRegistrations()
    val readAllTemplates: LiveData<List<Template>> = registrationDAO.readAllTemplates()

    suspend fun addRegistration(registration: Registration): Long =
        registrationDAO.addRegistration(registration)

    suspend fun addTemplate(template: Template): Long =
        registrationDAO.addTemplate(template)

}