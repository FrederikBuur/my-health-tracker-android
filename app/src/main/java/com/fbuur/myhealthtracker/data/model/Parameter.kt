package com.fbuur.myhealthtracker.data.model

import androidx.room.*
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter

sealed class Parameter(
    open val title: String,
    open val regId: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 // fixme dont make var

    abstract fun getValueOfInterest(): Int?

    @Entity(
        foreignKeys = [ForeignKey(
            entity = Registration::class,
            parentColumns = ["id"],
            childColumns = ["regId"],
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class Note(
        @ColumnInfo(name = "regId", index = true)
        override val regId: Long,
        override val title: String,
        val description: String
    ) : Parameter(title, regId) {
        override fun getValueOfInterest(): Int? = null

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
        @ColumnInfo(name = "regId", index = true)
        override val regId: Long,
        override val title: String,
        val value: Int,
        val lowestValue: Int,
        val highestValue: Int
    ) : Parameter(title, regId) {

        constructor() : this(0L, "", 0, 0, 0)

        override fun getValueOfInterest() = value

        override fun toString(): String {
            return "$title: $value from $lowestValue to $highestValue"
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
    data class Number(
        @ColumnInfo(name = "regId", index = true)
        override val regId: Long,
        override val title: String,
        val value: Int
    ) : Parameter(title, regId) {

        constructor() : this(0L, "", 0)

        override fun getValueOfInterest() = value

        override fun toString(): String {
            return "$title: $value"
        }
    }

}

fun List<Parameter>.mapToEventParameterList(): List<EventItemParameter> {
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
            is Parameter.Number -> {
                EventItemParameter.Number(
                    id = p.id,
                    regId = p.regId,
                    title = p.title,
                    type = ParameterType.NUMBER,
                    value = p.value
                )
            }
        }
    }
}