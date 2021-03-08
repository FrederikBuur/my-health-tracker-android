package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData

class RegistrationRepository(private val registrationDAO: RegistrationDAO) {

    val readAllRegistrations: LiveData<List<Registration>> = registrationDAO.readAllRegistrations()

    suspend fun addRegistration(registration: Registration) {
        registrationDAO.addRegistration(registration)
    }

}