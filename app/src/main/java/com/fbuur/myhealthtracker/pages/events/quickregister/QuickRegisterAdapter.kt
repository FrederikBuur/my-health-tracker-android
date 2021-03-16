package com.fbuur.myhealthtracker.pages.events.quickregister

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemQuickRegisterBinding

class QuickRegisterAdapter : RecyclerView.Adapter<QuickRegisterViewHolder>() {

    private var quickRegistersList = emptyList<QuickRegisterEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickRegisterViewHolder {
        return QuickRegisterViewHolder(
            ItemQuickRegisterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuickRegisterViewHolder, position: Int) {
        val quickRegister = quickRegistersList[position]
        holder.bind(quickRegister)
    }

    override fun getItemCount() = quickRegistersList.size

    fun setData(newQuickRegisters: List<QuickRegisterEntry>) {
        val diffUtil = DiffUtilQuickRegisterItems(this.quickRegistersList, newQuickRegisters)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        diffResults.dispatchUpdatesTo(this)
        quickRegistersList = newQuickRegisters
    }
}

class QuickRegisterViewHolder(
    private val itemBinding: ItemQuickRegisterBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(quickRegisterEntry: QuickRegisterEntry) {
        itemBinding.apply {
            name.text = quickRegisterEntry.name
            container.setCardBackgroundColor(Color.parseColor(quickRegisterEntry.color))
        }
    }

}
