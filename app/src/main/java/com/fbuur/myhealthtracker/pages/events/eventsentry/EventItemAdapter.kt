package com.fbuur.myhealthtracker.pages.events.eventsentry

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.fbuur.myhealthtracker.databinding.ItemParameterNoteBinding
import com.fbuur.myhealthtracker.databinding.ItemParameterSliderBinding

class EventItemAdapter(
    context: Context,
    resource: Int,
    private val parameterList: List<EventItemParameter>
) : ArrayAdapter<EventItemParameter>(context, resource, parameterList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        return when (val item = parameterList[position]) {
            is EventItemParameter.Note -> {
                setupAsNote(item, parent)
            }
            is EventItemParameter.Slider -> {
                setupAsSlider(item, parent)
            }
        }

    }

    private fun setupAsNote(
        note: EventItemParameter.Note,
        parent: ViewGroup
    ): View {
        return ItemParameterNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .apply {
                parameterHeader.text = note.title
                parameterNoteText.setText(note.description)
            }.root
    }

    private fun setupAsSlider(
        slider: EventItemParameter.Slider,
        parent: ViewGroup
    ): View {
        return ItemParameterSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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