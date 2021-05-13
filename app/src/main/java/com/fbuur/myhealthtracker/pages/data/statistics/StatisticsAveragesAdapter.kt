package com.fbuur.myhealthtracker.pages.data.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemStatAverageBinding

class StatisticsAveragesAdapter : RecyclerView.Adapter<StatisticsAverageViewHolder>() {

    private var statAverageEntities = emptyList<StatisticsAverageEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsAverageViewHolder {
        return StatisticsAverageViewHolder(
            ItemStatAverageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StatisticsAverageViewHolder, position: Int) {
        holder.bind(statAverageEntities[position])
    }

    override fun getItemCount() = statAverageEntities.size

    fun setData(list: List<StatisticsAverageEntity>) {
        statAverageEntities = list
        this.notifyDataSetChanged()
    }

}
