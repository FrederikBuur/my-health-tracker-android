package com.fbuur.myhealthtracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template

@Dao
interface RegistrationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRegistration(registration: Registration): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTemplate(template: Template): Long

    @Query("SELECT * FROM registration ORDER BY date DESC")
    fun readAllRegistrationsLD(): LiveData<List<Registration>>
    @Query("SELECT * FROM template ORDER BY lastUsed DESC LIMIT 15")
    fun readAllTemplatesLD(): LiveData<List<Template>>

    @Query("SELECT * FROM registration ORDER BY date DESC")
    suspend fun readAllRegistrations(): List<Registration>
    @Query("SELECT * FROM template ORDER BY lastUsed DESC")
    suspend fun readAllTemplates(): List<Template>
    @Query("SELECT * FROM template WHERE id=:id")
    suspend fun readTemplateById(id: Long): Template

    @Update
    suspend fun updateTemplate(template: Template)

}