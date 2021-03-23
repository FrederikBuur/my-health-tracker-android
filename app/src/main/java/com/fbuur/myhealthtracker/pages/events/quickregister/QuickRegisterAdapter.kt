package com.fbuur.myhealthtracker.pages.events.quickregister

import android.graphics.Color
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.ItemQuickRegisterBinding
import com.fbuur.myhealthtracker.databinding.ItemQuickRegisterNoteBinding

class QuickRegisterAdapter(
    private val onQuickRegisterClicked: (Long) -> Unit,
    private val onQuickRegisterNoteClicked: () -> Unit,
    private val onQuickRegisterLongClicked: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var quickRegistersList = emptyList<QuickRegisterEntry>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.NOTE.ordinal -> QuickRegisterNoteViewHolder(
                ItemQuickRegisterNoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> QuickRegisterEventViewHolder(
                ItemQuickRegisterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val quickRegister = quickRegistersList[position]

        when (holder.itemViewType) {
            ViewType.NOTE.ordinal -> {
                (holder as QuickRegisterNoteViewHolder).bind(
                    onQuickRegisterNoteClicked
                )
            }
            ViewType.EVENT.ordinal -> {
                (holder as QuickRegisterEventViewHolder).bind(
                    quickRegister,
                    onQuickRegisterClicked,
                    onQuickRegisterLongClicked
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ViewType.NOTE.ordinal else ViewType.EVENT.ordinal
    }

    override fun getItemCount() = quickRegistersList.size

    fun setData(newQuickRegisters: List<QuickRegisterEntry>) {
        val diffUtil = DiffUtilQuickRegisterItems(this.quickRegistersList, newQuickRegisters)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        diffResults.dispatchUpdatesTo(this)
        quickRegistersList = newQuickRegisters
    }

    enum class ViewType { NOTE, EVENT }

}
