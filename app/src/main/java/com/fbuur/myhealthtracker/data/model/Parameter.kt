package com.fbuur.myhealthtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Ignore

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
    ) : Parameter(title, regId)

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
    ) : Parameter(title, regId)

}