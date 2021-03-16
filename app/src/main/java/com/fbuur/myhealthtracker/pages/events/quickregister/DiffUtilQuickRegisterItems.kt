package com.fbuur.myhealthtracker.pages.events.quickregister

import androidx.recyclerview.widget.DiffUtil

class DiffUtilQuickRegisterItems(
    private val oldList: List<QuickRegisterEntry>,
    private val newList: List<QuickRegisterEntry>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name &&
                oldList[oldItemPosition].color == newList[newItemPosition].color &&
                oldList[oldItemPosition].templateTypes == newList[newItemPosition].templateTypes
    }

}
