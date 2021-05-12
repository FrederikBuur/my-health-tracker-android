package com.fbuur.myhealthtracker.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val Number.dpToPx: Float get() = (toFloat() * Resources.getSystem().displayMetrics.density)

fun Date.toDateString(): String {
    val dateFormat: DateFormat = SimpleDateFormat("dd. MMMM HH:mm", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.toHourMinString(): String {
    val dateFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.toMonthYearString(): String {
    val dateFormat: DateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return dateFormat.format(this).capitalize(Locale.getDefault())
}

fun Date.toDayMonthYearString(): String {
    val dateFormat: DateFormat = SimpleDateFormat("EEE dd. MMMM yy", Locale.getDefault())
    return dateFormat.format(this).capitalize(Locale.getDefault())
}

fun Date.toWeekMonthYear(): String {
    val dateFormat: DateFormat = SimpleDateFormat("W. MMMM yy", Locale.getDefault())
    return "Week:${dateFormat.format(this)}"
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