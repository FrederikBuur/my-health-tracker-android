package com.fbuur.myhealthtracker.pages.events

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.databinding.ItemEventBinding
import com.fbuur.myhealthtracker.databinding.ItemParameterNoteBinding
import com.fbuur.myhealthtracker.databinding.ItemParameterSliderBinding
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import com.fbuur.myhealthtracker.util.getInitials
import com.fbuur.myhealthtracker.util.toDateString

class EventViewHolder(
    private val itemBinding: ItemEventBinding,
    private val onCreateParameterNoteBinding: () -> ItemParameterNoteBinding,
    private val onCreateParameterSliderBinding: () ->ItemParameterSliderBinding,
    private val onRemoveParameterClicked: (Long, Long, ParameterType) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(
        eventItemEntry: EventItemEntry,
        onAddParameterClicked: (Long, Long) -> Unit
    ) {
        itemBinding.apply {
            expansionCollapseView(eventItemEntry.isExpanded)
            eventName.text = eventItemEntry.name
            eventDate.text = eventItemEntry.date.toDateString()
            eventIcon.setCardBackgroundColor(Color.parseColor(eventItemEntry.iconColor))
            eventIconInitials.text = eventItemEntry.name.getInitials()

            collapsedContainer.setOnClickListener {
                eventItemEntry.isExpanded = !eventItemEntry.isExpanded
                expansionCollapseView(eventItemEntry.isExpanded)
            }
            addParametersButton.setOnClickListener {
                val ids = eventItemEntry.id.split(':')
                onAddParameterClicked.invoke(ids[0].toLong(), ids[1].toLong())
            }

            // make sure parameter view is clear when reusing
            itemBinding.eventParameters.removeAllViews()

            //add parameter views to linear layout
            eventItemEntry.eventParameterList.forEach { p ->

                val view = when (p) {
                    is EventItemParameter.Note -> {
                        setupAsNote(p, this@EventViewHolder.onCreateParameterNoteBinding())
                    }
                    is EventItemParameter.Slider -> {
                        setupAsSlider(p, this@EventViewHolder.onCreateParameterSliderBinding())
                    }
                }
                itemBinding.eventParameters.addView(view)

            }

        }
    }

    private fun expansionCollapseView(isExpanded: Boolean) {
        if (isExpanded) {
            itemBinding.eventItemContainer.transitionToEnd()
        } else {
            itemBinding.eventItemContainer.transitionToStart()
        }
    }

    private fun setupAsNote(
        note: EventItemParameter.Note,
        binding: ItemParameterNoteBinding
    ): View {
        return binding
            .apply {
                parameterHeader.text = note.title
                parameterNoteText.setText(note.description)
                removeParameterIcon.setOnClickListener {
                    onRemoveParameterClicked(note.id, note.regId, note.type)
                }
            }.root
    }

    private fun setupAsSlider(
        slider: EventItemParameter.Slider,
        binding: ItemParameterSliderBinding
    ): View {
        return binding
            .apply {
                parameterHeader.text = slider.title
                parameterSlider.value = slider.value.toFloat()
                parameterSlider.valueFrom = slider.lowest.toFloat()
                parameterSlider.valueTo = slider.highest.toFloat()
                removeParameterIcon.setOnClickListener {
                    onRemoveParameterClicked(slider.id, slider.regId, slider.type)
                }
//                parameterSlider.addOnChangeListener { slider, value, fromUser ->
//                    if (fromUser) {
//                        // update slider value in DB
//                        value
//                    }
//                }
            }.root
    }

}