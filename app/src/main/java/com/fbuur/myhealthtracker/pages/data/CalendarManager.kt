package com.fbuur.myhealthtracker.pages.data

import java.util.*

object CalendarManager {
    private val cal = Calendar.getInstance()

    fun clearCalendarMeta() {
        cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE)
        cal.clear(Calendar.SECOND)
        cal.clear(Calendar.MILLISECOND)
    }

    fun getPreviousMonthAsDate(date: Date): Date {
        cal.time = date
        cal.add(Calendar.MONTH, -1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.time
    }

    fun getNextMonthAsDate(date: Date): Date {
        cal.time = date
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.time
    }

    fun getNextAsDate(date: Date, scope: Int): Date {
        cal.time = date
        cal.add(scope, 1)
        return cal.time
    }

    fun getNextAsDateScoped(date: Date, scope: DataViewModel.DataScope): Date {
        cal.time = date
        when(scope) {
            DataViewModel.DataScope.DAY -> {
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
            DataViewModel.DataScope.WEEK -> {
                cal.add(Calendar.WEEK_OF_MONTH, 1)
            }
            else -> {
                throw Exception("Unsupported scope")
            }
        }
        return cal.time
    }
    fun getPreviousAsDateScoped(date: Date, scope: DataViewModel.DataScope): Date {
        cal.time = date
        when(scope) {
            DataViewModel.DataScope.DAY -> {
                cal.add(Calendar.DAY_OF_MONTH, -1)
            }
            DataViewModel.DataScope.WEEK -> {
                cal.add(Calendar.WEEK_OF_MONTH, -1)
            }
            else -> {
                throw Exception("Unsupported scope")
            }
        }
        return cal.time
    }

    fun getDateAtDay(day: Int): Date {
        clearCalendarMeta()
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.time
    }

    fun isDaySameDateDay(
        day: Int,
        date: Date,
    ): Boolean {
        cal.time = date
        clearCalendarMeta()
        val dateAsWeekDay = cal.get(Calendar.DAY_OF_MONTH)
        return dateAsWeekDay == day
    }
}