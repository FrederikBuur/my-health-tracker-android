package com.fbuur.myhealthtracker.data.registration

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.Registration
import com.fbuur.myhealthtracker.data.model.Template
import java.util.*

@Dao
interface TrackingDAO {

    // insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRegistration(registration: Registration): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTemplate(template: Template): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addParameterNote(parameter: Parameter.Note): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addParameterSlider(parameter: Parameter.Slider): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addParameterNumber(parameter: Parameter.Number): Long

    // read
    @Query("SELECT * FROM registration ORDER BY date DESC")
    fun readAllRegistrationsLD(): LiveData<List<Registration>>
    @Query("SELECT * FROM template ORDER BY lastUsed DESC LIMIT 15")
    fun readAllTemplatesLD(): LiveData<List<Template>>

    @Query("SELECT * FROM registration ORDER BY date DESC")
    suspend fun readAllRegistrations(): List<Registration>

    @Query("SELECT * FROM registration WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun readRegistrationByTime(startDate: Long, endDate: Long): List<Registration>

    @Query("SELECT COUNT(*) FROM registration WHERE temId=:temId AND date BETWEEN :startDate AND :endDate ORDER by date ASC ")
    suspend fun readRegistrationCountByTemplateAndTime(temId: Long, startDate: Long, endDate: Long): Int

    @Query("SELECT * FROM registration WHERE temId=:temId AND date BETWEEN :startDate AND :endDate ORDER by date ASC ")
    suspend fun readRegistrationsByTemplateAndTime(temId: Long, startDate: Long, endDate: Long): List<Registration>

    @Query("SELECT * FROM template ORDER BY lastUsed DESC")
    suspend fun readAllTemplates(): List<Template>

    @Query("SELECT * FROM template WHERE lastUsed BETWEEN :startDate AND :endDate ORDER BY lastUsed DESC")
    suspend fun readAllTemplatesByTime(startDate: Long, endDate: Long): List<Template>

    @Query("SELECT * FROM template WHERE id=:id")
    suspend fun readTemplateById(id: Long): Template

    @Query("SELECT * FROM registration WHERE id=:id")
    suspend fun readRegistrationById(id: Long): Registration

    @Query("SELECT * FROM slider WHERE regId=:regId")
    suspend fun readAllSliderByRegId(regId: Long): List<Parameter.Slider>
    @Query("SELECT * FROM note WHERE regId=:regId")
    suspend fun readAllNoteByRegId(regId: Long): List<Parameter.Note>
    @Query("SELECT * FROM number WHERE regId=:regId")
    suspend fun readAllNumberByRegId(regId: Long): List<Parameter.Number>

    @Query("SELECT * FROM slider WHERE regId=:regId AND title=:name")
    suspend fun readAllSliderByRegIdAndParameterName(regId: Long, name: String): List<Parameter.Slider>
    @Query("SELECT * FROM note WHERE regId=:regId AND title=:name")
    suspend fun readAllNoteByRegIdParameterName(regId: Long, name: String): List<Parameter.Note>
    @Query("SELECT * FROM number WHERE regId=:regId AND title=:name")
    suspend fun readAllNumberByRegIdParameterName(regId: Long, name: String): List<Parameter.Number>

    // update
    @Update
    suspend fun updateTemplate(template: Template)
    @Update
    suspend fun updateRegistration(registration: Registration)

    @Update
    suspend fun updateParameterNote(note: Parameter.Note)
    @Update
    suspend fun updateParameterSlider(slider: Parameter.Slider)
    @Update
    suspend fun updateParameterNumber(number: Parameter.Number)

    // delete
    @Query("DELETE FROM template WHERE id= :id")
    suspend fun deleteTemplateById(id: Long)
    @Query("DELETE FROM registration WHERE id= :id")
    suspend fun deleteRegistrationById(id: Long)

    @Query("DELETE FROM note WHERE id= :id")
    suspend fun deleteParameterNote(id: Long)
    @Query("DELETE FROM slider WHERE id= :id")
    suspend fun deleteParameterSlider(id: Long)
    @Query("DELETE FROM number WHERE id= :id")
    suspend fun deleteParameterNumber(id: Long)

}