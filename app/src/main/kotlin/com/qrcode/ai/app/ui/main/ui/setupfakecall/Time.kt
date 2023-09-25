package com.qrcode.ai.app.ui.main.ui.setupfakecall

import android.content.Context
import com.qrcode.ai.app.R

data class Time(
    val timeInMillis: Long
) {
    fun toString(context: Context): String {
        return if (timeInMillis == 0L) {
            context.getString(R.string.now)
        } else if (timeInMillis / 3600000 >= 1) {
            context.getString(R.string.time_hour_later, timeInMillis / 3600000)
        } else if (timeInMillis / 60000 >= 1) {
            context.getString(R.string.time_minute_later, timeInMillis / 60000)
        } else {
            context.getString(R.string.time_second_later, timeInMillis / 1000)
        }
    }
}
