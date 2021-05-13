package com.fbuur.myhealthtracker.pages.data.statistics

import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemStatAverageBinding

class StatisticsAverageViewHolder(
    private val itemBinding: ItemStatAverageBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(statAverageEntity: StatisticsAverageEntity) {

        itemBinding.apply {
            title.text = statAverageEntity.name
            root.setCardBackgroundColor(statAverageEntity.color)
            subtitle1.text = statAverageEntity.title1
            value1.text = statAverageEntity.value1
            subtitle2.text = statAverageEntity.title2
            value2.text = statAverageEntity.value2
        }

    }

}