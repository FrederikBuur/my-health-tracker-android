package com.fbuur.myhealthtracker.pages.events.quickregister

import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.databinding.ItemQuickRegisterNoteBinding

class QuickRegisterNoteViewHolder(
    private val itemBinding: ItemQuickRegisterNoteBinding
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(
        onQuickRegisterNoteClicked: () -> Unit
    ) {
        itemBinding.apply {
            container.setOnClickListener {
                onQuickRegisterNoteClicked()
            }
        }
    }
}