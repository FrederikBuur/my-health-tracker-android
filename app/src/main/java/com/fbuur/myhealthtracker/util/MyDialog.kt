package com.fbuur.myhealthtracker.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import com.fbuur.myhealthtracker.databinding.DialogRenameTemplateBinding

class MyDialog(
    private val onRenameClicked: (String) -> Unit
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val binding = DialogRenameTemplateBinding.inflate(LayoutInflater.from(activity))

        builder.setView(binding.root)
            .setTitle("Rename event")
            .setNegativeButton("Cancel") { _, _ ->
                // nothing should happen
            }
            .setPositiveButton("Rename") { _, _ ->
                val newName = binding.renameTemplateInput.text?.toString()
                if (newName?.isNotBlank() == true) {
                    onRenameClicked.invoke(newName)
                }
            }
        return builder.create()
    }

}