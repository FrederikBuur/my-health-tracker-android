package com.fbuur.myhealthtracker.pages.addparameters

import com.fbuur.myhealthtracker.data.model.ParameterType

class AddParameterEntry(
    val title: String,
    val description: String,
    val type: ParameterType,
    var selected: Boolean = false
)