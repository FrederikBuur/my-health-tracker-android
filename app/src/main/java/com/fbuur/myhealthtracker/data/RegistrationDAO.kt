package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegistrationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRegistration(registration: Registration)

    @Query("SELECT * FROM registration_table ORDER BY date DESC")
    fun readAllRegistrations(): LiveData<List<Registration>>

}