package com.fbuur.myhealthtracker.pages.events

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemEventBinding
import com.fbuur.myhealthtracker.databinding.ItemParameterNoteBinding
import com.fbuur.myhealthtracker.databinding.ItemParameterSliderBinding
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemEntry
import com.fbuur.myhealthtracker.pages.events.eventsentry.EventItemParameter
import com.fbuur.myhealthtracker.util.getInitials
import com.fbuur.myhealthtracker.util.toDateString

class EventViewHolder(
    private val itemBinding: ItemEventBinding,
    private val parameterNoteBinding: ItemParameterNoteBinding,
    private val parameterSliderBinding: ItemParameterSliderBinding
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
            eventItemContainer.setOnClickListener {
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
                        setupAsNote(p, this@EventViewHolder.parameterNoteBinding)
                    }
                    is EventItemParameter.Slider -> {
                        setupAsSlider(p, this@EventViewHolder.parameterSliderBinding)
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
//                parameterSlider.addOnChangeListener { slider, value, fromUser ->
//                    if (fromUser) {
//                        // update slider value in DB
//                        value
//                    }
//                }
            }.root
    }

}