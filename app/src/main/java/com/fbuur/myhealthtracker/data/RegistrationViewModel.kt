package com.fbuur.myhealthtracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fbuur.myhealthtracker.data.model.Registration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application): AndroidViewModel(application) {

    private val readAllRegistrations: LiveData<List<Registration>>
    private val repository: RegistrationRepository

    init {
        val registrationDao = TrackingDatabase.getTrackingDatabase(application).registrationDao()
        repository = RegistrationRepository(registrationDao)

        readAllRegistrations = repository.readAllRegistrations
    }

    fun addRegistration(registration: Registration) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRegistration(registration)
        }
    }

}