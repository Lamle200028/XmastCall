package com.qrcode.ai.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {
    private val _calendar = Calendar.getInstance()

    enum class TimeFormat(val string: String) {
        TimeFormat1("MMM dd HH:mm"),
        TimeFormat2("YYYYMMdd'T'hhmmss'Z'"),
        TimeFormat3("MMM dd, yyyy"),
        TimeFormat4("MM/dd/yyyy HH:mm:ss"),
        TimeFormat5("MM/dd/yyyy")
    }

    fun getCurrentDay(): Int {
        return _calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentMonth(): Int {
        return _calendar.get(Calendar.MONTH)
    }

    fun getCurrentYear(): Int {
        return _calendar.get(Calendar.YEAR)
    }

    fun getTime(timeFormat: TimeFormat, time: Date): String {
        val dateFormatter = SimpleDateFormat(timeFormat.string, Locale.getDefault())
        return dateFormatter.format(time)
    }

    fun getDate(timeFormat: TimeFormat, time: String): Date {
        val dateFormatter = SimpleDateFormat(timeFormat.string, Locale.ENGLISH)
        return dateFormatter.parse(time) ?: Date()
    }
}