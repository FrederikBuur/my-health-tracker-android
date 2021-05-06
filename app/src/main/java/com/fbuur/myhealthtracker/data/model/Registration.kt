package com.fbuur.myhealthtracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
            entity = Template::class,
            parentColumns = ["id"],
            childColumns = ["temId"],
            onDelete = ForeignKey.CASCADE)]
)
data class Registration(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "temId", index = true)
    val temId: Long,
    val date: Date,
    val type: RegistrationType,
    val lastModified: Date
)

enum class RegistrationType {
    NOTE, EVENT
}

enum class ParameterType {
    NOTE, SLIDER, LOCATION, BINARY
}