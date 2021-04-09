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
import com.google.android.material.slider.Slider

class EventViewHolder(
    private val itemBinding: ItemEventBinding,
    private val onCreateParameterNoteBinding: () -> ItemParameterNoteBinding,
    private val onCreateParameterSliderBinding: () -> ItemParameterSliderBinding,
    private val onRemoveParameterClicked: (Long, Long, ParameterType) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(
        eventItemEntry: EventItemEntry,
        onAddParameterClicked: (Long, Long) -> Unit,
        onParameterChanged: (EventItemParameter) -> Unit,
        onParameterNoteClicked: (EventItemParameter.Note) -> Unit,
        onParameterTitleRenameClicked: (EventItemParameter) -> Unit
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
                        setupAsNote(
                            p,
                            this@EventViewHolder.onCreateParameterNoteBinding(),
                            onParameterNoteClicked,
                            onParameterTitleRenameClicked
                        )
                    }
                    is EventItemParameter.Slider -> {
                        setupAsSlider(
                            p,
                            this@EventViewHolder.onCreateParameterSliderBinding(),
                            onParameterChanged,
                            onParameterTitleRenameClicked
                        )
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
        binding: ItemParameterNoteBinding,
        onParameterNoteClicked: (EventItemParameter.Note) -> Unit,
        onParameterTitleRenameClicked: (EventItemParameter) -> Unit
    ): View {
//        val b = ItemParameterNoteBinding.inflate( todo user this inflater instead of a lambda
//            this.itemBinding.root.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
//                    as LayoutInflater
//        )
        return binding.apply {
            parameterHeader.text = note.title
            parameterNoteText.text = note.description
            removeParameterIcon.setOnClickListener {
                onRemoveParameterClicked(note.id, note.regId, note.type)
            }
            parameterNoteText.setOnClickListener {
                onParameterNoteClicked(note)
            }
            editParameterTitleIcon.setOnClickListener {
                onParameterTitleRenameClicked(note)
            }
        }.root
    }

    private fun setupAsSlider(
        slider: EventItemParameter.Slider,
        binding: ItemParameterSliderBinding,
        onParameterChanged: (EventItemParameter) -> Unit,
        onParameterTitleRenameClicked: (EventItemParameter) -> Unit
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
                editParameterTitleIcon.setOnClickListener {
                    onParameterTitleRenameClicked(slider)
                }
                parameterSlider.addOnSliderTouchListener(
                    object : Slider.OnSliderTouchListener {
                        override fun onStartTrackingTouch(s: Slider) {
                            // nothing should happen
                            s
                        }

                        override fun onStopTrackingTouch(s: Slider) {
                            val temp = slider.copy(value = s.value.toInt())
                            onParameterChanged(temp)
                        }

                    }

                )

            }.root
    }

}