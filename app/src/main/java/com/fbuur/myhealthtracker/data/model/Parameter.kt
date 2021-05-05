package com.fbuur.myhealthtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Ignore
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter

sealed class Parameter(
    open val title: String,
    open val regId: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 // fixme dont make var

    @Entity(
        foreignKeys = [ForeignKey(
            entity = Registration::class,
            parentColumns = ["id"],
            childColumns = ["regId"],
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class Note(
        @Ignore override val regId: Long,
        @Ignore override val title: String,
        val description: String
    ) : Parameter(title, regId) {
        override fun toString(): String {
            return "$title: $description"
        }
    }

    @Entity(
        foreignKeys = [ForeignKey(
            entity = Registration::class,
            parentColumns = ["id"],
            childColumns = ["regId"],
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class Slider(
        @Ignore override val regId: Long,
        @Ignore override val title: String,
        val value: Int,
        val lowestValue: Int,
        val highestValue: Int
    ) : Parameter(title, regId) {
        override fun toString(): String {
            return "$title: $value from $lowestValue to $highestValue"
        }
    }

}

fun List<Parameter>.mapToEventParameterList() : List<EventItemParameter> {
    return map { p ->
        when (p) {
            is Parameter.Note -> {
                EventItemParameter.Note(
                    id = p.id,
                    regId = p.regId,
                    title = p.title,
                    type = ParameterType.NOTE,
                    description = p.description
                )
            }
            is Parameter.Slider -> {
                EventItemParameter.Slider(
                    id = p.id,
                    regId = p.regId,
                    title = p.title,
                    type = ParameterType.SLIDER,
                    value = p.value,
                    lowest = p.lowestValue,
                    highest = p.highestValue
                )
            }
            else -> {
                // todo binary and location not implemented yet
                throw NotImplementedError("Binary and Location not implemented yet: $p")
            }
        }
    }
}