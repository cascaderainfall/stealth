package com.cosmos.unreddit.util

import android.content.Context
import com.cosmos.unreddit.R
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtil {
    enum class Unit(val value: Long) {
        SECOND(1000),
        MINUTE(SECOND.value * 60),
        HOURS(MINUTE.value * 60),
        DAY(HOURS.value * 24),
        YEAR(DAY.value * 365)
    }

    @JvmStatic
    fun getTimeDifference(context: Context, timeInMillis: Long): String { // TODO: USe real date, not time difference
        val elapsedTime = System.currentTimeMillis() - timeInMillis
        return when {
            elapsedTime < Unit.MINUTE.value -> context.getString(R.string.time_difference_now)
            elapsedTime < Unit.HOURS.value -> {
                context.getString(R.string.time_difference_minute, TimeUnit.MILLISECONDS.toMinutes(elapsedTime))
            }
            elapsedTime < Unit.DAY.value -> {
                context.getString(R.string.time_difference_hour, TimeUnit.MILLISECONDS.toHours(elapsedTime))
            }
            elapsedTime < Unit.YEAR.value -> {
                context.getString(R.string.time_difference_day, TimeUnit.MILLISECONDS.toDays(elapsedTime))
            }
            else -> {
                context.getString(R.string.time_difference_year, elapsedTime.div(Unit.YEAR.value))
            }
        }
    }

    fun getFormattedDate(timeInMillis: Long): String {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
            .format(Date(timeInMillis))
    }
}
