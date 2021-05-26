package com.fbuur.myhealthtracker.pages.events.eventsentry

import com.fbuur.myhealthtracker.data.model.Parameter
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.data.model.RegistrationType
import java.util.*

data class EventItemEntry(
    val id: String,
    val name: String,
    val date: Date,
    val iconColor: String,
    val type: RegistrationType,
    val eventParameterList: List<EventItemParameter>,
    var isExpanded: Boolean = false
)

sealed class EventItemParameter(
    open val id: Long,
    open val regId: Long,
    open val title: String,
    open val type: ParameterType
) {
    data class Note(
        override val id: Long,
        override val regId: Long,
        override val title: String,
        override val type: ParameterType,
        val description: String
    ) : EventItemParameter(id, regId, title, type)

    data class Slider(
        override val id: Long,
        override val regId: Long,
        override val title: String,
        override val type: ParameterType,
        val lowest: Int,
        val highest: Int,
        val value: Int
    ) : EventItemParameter(id, regId, title, type)

    data class Number(
        override val id: Long,
        override val regId: Long,
        override val title: String,
        override val type: ParameterType,
        val value: Int
    ) : EventItemParameter(id, regId, title, type)

}

fun EventItemParameter.Note.toParameter() : Parameter.Note =
    Parameter.Note(
        regId = regId,
        title = title,
        description = description
    ).also {
        it.id = id
    }

fun EventItemParameter.Slider.toParameter() : Parameter.Slider =
    Parameter.Slider(
        regId = regId,
        title = title,
        value = value,
        lowestValue = lowest,
        highestValue = highest
    ).also {
        it.id = id
    }

fun EventItemParameter.Number.toParameter() : Parameter.Number =
    Parameter.Number(
        regId = regId,
        title = title,
        value = value
    ).also {
        it.id = id
    }