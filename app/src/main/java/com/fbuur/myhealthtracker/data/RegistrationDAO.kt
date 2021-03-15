package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template

@Dao
interface RegistrationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRegistration(registration: Registration): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTemplate(template: Template): Long

    @Query("SELECT * FROM registration ORDER BY date DESC")
    fun readAllRegistrations(): LiveData<List<Registration>>

    @Query("SELECT * FROM template ORDER BY lastUsed DESC")
    fun readAllTemplates(): LiveData<List<Template>>

}