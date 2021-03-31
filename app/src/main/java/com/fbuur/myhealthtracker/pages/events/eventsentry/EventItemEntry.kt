package com.fbuur.myhealthtracker.pages.events.eventsentry

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

}
