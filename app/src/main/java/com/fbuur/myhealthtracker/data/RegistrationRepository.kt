package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData
import com.fbuur.myhealthtracker.data.model.Registration

class RegistrationRepository(private val registrationDAO: RegistrationDAO) {

    val readAllRegistrations: LiveData<List<Registration>> = registrationDAO.readAllRegistrations()

    suspend fun addRegistration(registration: Registration) {
        registrationDAO.addRegistration(registration)
    }

}