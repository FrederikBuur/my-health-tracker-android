package com.fbuur.myhealthtracker.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDateString(): String {
    val dateFormat: DateFormat = SimpleDateFormat("dd. MMMM hh:mm", Locale.getDefault())
    return dateFormat.format(this)
}

fun String.getInitials(): String {

    var initials = Character.toUpperCase(this[0]).toString()

    for (i in 1 until this.length - 1) {
        if (this[i] == ' ') {
           initials += Character.toUpperCase(this[i + 1])
        }
    }
    return initials
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}