package com.fbuur.myhealthtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val lastUsed: Date,
    val color: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Template::class,
        parentColumns = ["id"],
        childColumns = ["temId"],
        onDelete = ForeignKey.CASCADE)]

)
data class TemplateType(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val temId: Long,
    val type: ParameterType
)