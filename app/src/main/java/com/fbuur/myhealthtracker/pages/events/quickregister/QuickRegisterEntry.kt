package com.fbuur.myhealthtracker.pages.events.quickregister

import com.fbuur.myhealthtracker.data.model.TemplateType

data class QuickRegisterEntry(
    val id: String,
    val temId: Long,
    val name: String,
    val color: String,
    val templateTypes: List<TemplateType>
)
