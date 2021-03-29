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
)

sealed class EventItemParameter(
    open val id: Long,
    open val eventId: String,
    open val title: String,
    open val type: ParameterType
) {
    data class Note(
        override val id: Long,
        override val eventId: String,
        override val title: String,
        override val type: ParameterType,
        val description: String
    ) : EventItemParameter(id, eventId, title, type)

    data class Slider(
        override val id: Long,
        override val eventId: String,
        override val title: String,
        override val type: ParameterType,
        val lowest: Int,
        val highest: Int,
        val value: Int
    ) : EventItemParameter(id, eventId, title, type)

}
