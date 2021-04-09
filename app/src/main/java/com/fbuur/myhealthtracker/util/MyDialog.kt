package com.fbuur.myhealthtracker.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.databinding.DialogTextInputBinding

class MyDialog(
    private val text: String? = null,
    private val dialogStyle: DialogStyle,
    private val onPositiveClicked: (String) -> Unit
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val binding = DialogTextInputBinding.inflate(LayoutInflater.from(activity))

        binding.apply {
            if (text?.isNotBlank() == true) {
                textInput.setText(text)
            }
            textInput.hint = dialogStyle.editTextHint
            if (dialogStyle == DialogStyle.NOTE_PARAMETER) {
                textInput.height = 200.dpToPx.toInt()
                textInput.background = ContextCompat.getDrawable(this.root.context, R.drawable.background_input_border_dark)
                textInput.gravity = Gravity.TOP and Gravity.START
            }
        }
        builder.setView(binding.root)
            .setTitle(dialogStyle.title)
            .setNegativeButton("Cancel") { _, _ ->
                // nothing should happen
            }
            .setPositiveButton(dialogStyle.positiveButtonText) { _, _ ->
                val outputText = binding.textInput.text?.toString()
                if (outputText?.isNotBlank() == true) {
                    onPositiveClicked.invoke(outputText)
                }
            }
        return builder.create()
    }

}

enum class DialogStyle(
    val title: String,
    val editTextHint: String,
    val positiveButtonText: String,
) {
    RENAME("Rename", "New name...", "Rename"),
    NOTE_PARAMETER("Change Note", "Write a note...", "Save")
}