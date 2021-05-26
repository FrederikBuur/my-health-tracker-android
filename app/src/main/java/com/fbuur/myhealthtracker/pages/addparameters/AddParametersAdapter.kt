package com.fbuur.myhealthtracker.pages.addparameters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.data.model.ParameterType
import com.fbuur.myhealthtracker.databinding.ItemAddParametersBinding


class AddParametersAdapter(
    context: Context,
    resource: Int,
    private val parameterList: List<AddParameterEntry>,
    private val numberOfItemsSelected: (Int) -> Unit
) : ArrayAdapter<AddParameterEntry>(context, resource, parameterList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val item = getItem(position)

        // this is not going to be recycles and is therefore ignored
        return ItemAddParametersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .apply {
                item?.let {
                    title.text = item.title
                    description.text = item.description
                    icon.background =
                        ContextCompat.getDrawable(parent.context, getParameterDrawable(item.type))
                    container.setOnClickListener {
                        checkbox.isChecked = !item.selected
                    }
                    checkbox.setOnCheckedChangeListener { _, b ->
                        item.selected = b
                        numberOfItemsSelected.invoke(parameterList.filter { p -> p.selected }.size)
                    }
                }

            }.root
    }

    private fun getParameterDrawable(parameterType: ParameterType): Int =
        when (parameterType) {
            ParameterType.NOTE -> {
                R.drawable.ic_notes
            }
            ParameterType.SLIDER -> {
                R.drawable.ic_linear_scale
            }
            ParameterType.NUMBER -> {
                R.drawable.ic_dialpad
            }
//            ParameterType.BINARY -> {
//                R.drawable.ic_thumbs_up_down
//            }
//            ParameterType.LOCATION -> {
//                R.drawable.ic_location
//            }
        }

}