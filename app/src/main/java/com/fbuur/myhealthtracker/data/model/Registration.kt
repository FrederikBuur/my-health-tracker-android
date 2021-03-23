package com.fbuur.myhealthtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    val temId: Long,
    val date: Date,
    val type: RegistrationType
)

enum class RegistrationType {
    NOTE, EVENT
}

enum class ParameterType {
    NOTE, SLIDER, LOCATION, BINARY
}