package com.fbuur.myhealthtracker.pages.events.quickregister

import com.fbuur.myhealthtracker.data.model.TemplateType

data class QuickRegisterEntry(
    val id: Long,
    val name: String,
    val templateTypes: List<TemplateType>
)
