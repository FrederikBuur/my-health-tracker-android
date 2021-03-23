package com.fbuur.myhealthtracker.pages.events.quickregister

import android.graphics.Color
import android.view.ContextMenu
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.ItemQuickRegisterBinding

class QuickRegisterEventViewHolder(
    private val itemBinding: ItemQuickRegisterBinding
) : RecyclerView.ViewHolder(itemBinding.root), View.OnCreateContextMenuListener {

    private var title = ""

    fun bind(
        quickRegisterEntry: QuickRegisterEntry,
        onQuickRegisterClicked: (Long) -> Unit,
        onQuickRegisterLongClicked: (Long) -> Unit
    ) {
        title = quickRegisterEntry.name

        itemBinding.apply {
            name.text = quickRegisterEntry.name
            container.setCardBackgroundColor(Color.parseColor(quickRegisterEntry.color))
            container.setOnClickListener {
                onQuickRegisterClicked(quickRegisterEntry.temId)
            }
            container.setOnLongClickListener {
                onQuickRegisterLongClicked(quickRegisterEntry.temId)
                false
            }
            root.setOnCreateContextMenuListener(this@QuickRegisterEventViewHolder)

        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu?.setHeaderTitle(this.title)
        menu?.add(Menu.NONE, R.id.rename_template, Menu.NONE, "Rename")
        menu?.add(Menu.NONE, R.id.delete_template, Menu.NONE, "Delete all instances")

    }
}