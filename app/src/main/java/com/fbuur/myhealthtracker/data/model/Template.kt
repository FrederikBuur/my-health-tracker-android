package com.fbuur.myhealthtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fbuur.myhealthtracker.pages.events.RegistrationViewModel
import com.fbuur.myhealthtracker.pages.events.quickregister.QuickRegisterEntry
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

fun List<Template>.mapToQuickRegisterEntries(
): List<QuickRegisterEntry> {

    val noteEntry = QuickRegisterEntry(
        id = RegistrationViewModel.QUICK_REGISTER_NOTE,
        temId = -1,
        name = "note",
        color = "",
        templateTypes = emptyList()
    )

    val temList = this.mapNotNull { t ->
        if (t.id == -1L) {
            null
        } else {
            QuickRegisterEntry(
                id = "${t.id}${t.name}",
                temId = t.id,
                name = t.name,
                color = t.color,
                templateTypes = emptyList() // todo get attached template types
            )
        }
    }

    val list = arrayListOf(noteEntry)
    list.addAll(temList)
    return list
}