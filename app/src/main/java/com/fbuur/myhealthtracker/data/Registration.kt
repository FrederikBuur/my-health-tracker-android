package com.fbuur.myhealthtracker.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "registration_table")
data class Registration(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
//    @TypeConverters(DateTypeConverter::class)
    val date: Date,
    val parameters: List<Parameter>,
    val registrationTemplate: List<ParameterType>
)

sealed class Parameter(
    open val title: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // fixme dont make var

    @Entity
    data class Note(
         @Ignore override val title: String,
         val description: String
    ) : Parameter(title)

    @Entity
    data class Slider(
        @Ignore override val title: String,
        val name: String,
        val lowestValue: Int,
        val highestValue: Int
    ) : Parameter(title)

}

enum class ParameterType {
    NOTE, SLIDER, LOCATION
}