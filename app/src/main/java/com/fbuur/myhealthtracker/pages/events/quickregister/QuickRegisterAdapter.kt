package com.fbuur.myhealthtracker.pages.events.quickregister

import android.graphics.Color
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.ItemQuickRegisterBinding

class QuickRegisterAdapter(
    private val onQuickRegisterClicked: (Long) -> Unit,
    private val onQuickRegisterLongClicked: (Long) -> Unit
) : RecyclerView.Adapter<QuickRegisterViewHolder>() {

    private var quickRegistersList = emptyList<QuickRegisterEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickRegisterViewHolder {
        return QuickRegisterViewHolder(
            ItemQuickRegisterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuickRegisterViewHolder, position: Int) {
        val quickRegister = quickRegistersList[position]
        holder.bind(quickRegister, onQuickRegisterClicked, onQuickRegisterLongClicked)
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
            root.setOnCreateContextMenuListener(this@QuickRegisterViewHolder)

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
