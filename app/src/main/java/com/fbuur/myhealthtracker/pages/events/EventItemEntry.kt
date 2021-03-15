package com.fbuur.myhealthtracker.pages.events

import com.fbuur.myhealthtracker.data.model.Parameter
import java.util.*

data class EventItemEntry(
    val id: String,
    val name: String,
    val date: Date,
    val iconColor: String,
    val parameterList: List<Parameter>
)
