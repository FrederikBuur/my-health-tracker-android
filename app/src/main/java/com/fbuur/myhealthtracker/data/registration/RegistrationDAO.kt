package com.fbuur.myhealthtracker.data.registration

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import java.util.*

@Dao
interface RegistrationDAO {

    // insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRegistration(registration: Registration): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTemplate(template: Template): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addParameterNote(parameter: Parameter.Note): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addParameterSlider(parameter: Parameter.Slider): Long

    // read
    @Query("SELECT * FROM registration ORDER BY date DESC")
    fun readAllRegistrationsLD(): LiveData<List<Registration>>
    @Query("SELECT * FROM template ORDER BY lastUsed DESC LIMIT 15")
    fun readAllTemplatesLD(): LiveData<List<Template>>

    @Query("SELECT * FROM registration ORDER BY date DESC")
    suspend fun readAllRegistrations(): List<Registration>

//    @Query("SELECT * FROM registration WHERE datetime(date/1000,'unixepoch','start of month') = datetime(:dateOfMonth/1000,'unixepoch','start of month')")
//    suspend fun getRegistrationByMonth(dateOfMonth: Date) : List<Registration>

    @Query("SELECT * FROM registration WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun readRegistrationByMonth(startDate: Long, endDate: Long): List<Registration>

    @Query("SELECT * FROM template ORDER BY lastUsed DESC")
    suspend fun readAllTemplates(): List<Template>

    @Query("SELECT * FROM template WHERE id=:id")
    suspend fun readTemplateById(id: Long): Template
    @Query("SELECT * FROM registration WHERE id=:id")
    suspend fun readRegistrationById(id: Long): Registration

    @Query("SELECT * FROM slider WHERE regId=:regId")
    suspend fun readAllSliderByRegId(regId: Long): List<Parameter.Slider>
    @Query("SELECT * FROM note WHERE regId=:regId")
    suspend fun readAllNoteByRegId(regId: Long): List<Parameter.Note>

    // update
    @Update
    suspend fun updateTemplate(template: Template)
    @Update
    suspend fun updateRegistration(registration: Registration)

    @Update
    suspend fun updateParameterNote(note: Parameter.Note)
    @Update
    suspend fun updateParameterSlider(slider: Parameter.Slider)

    // delete
    @Query("DELETE FROM template WHERE id= :id")
    suspend fun deleteTemplateById(id: Long)
    @Query("DELETE FROM registration WHERE id= :id")
    suspend fun deleteRegistrationById(id: Long)

    @Query("DELETE FROM note WHERE id= :id")
    suspend fun deleteParameterNote(id: Long)
    @Query("DELETE FROM slider WHERE id= :id")
    suspend fun deleteParameterSlider(id: Long)

}