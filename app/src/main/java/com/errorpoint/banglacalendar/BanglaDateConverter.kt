package com.errorpoint.banglacalendar

import java.util.*

data class BanglaDate(
    val day: Int,
    val month: String,
    val year: Int
)

object BanglaDateConverter {
    private val banglaMonths = arrayOf(
        "বৈশাখ", "জ্যৈষ্ঠ", "আষাঢ়", "শ্রাবণ", "ভাদ্র", "আশ্বিন",
        "কার্তিক", "অগ্রহায়ণ", "পৌষ", "মাঘ", "ফাল্গুন", "চৈত্র"
    )

    private val daysInMonth = arrayOf(31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30, 30)

    fun convertToBanglaDate(gregorianDate: Date): BanglaDate {
        val calendar = Calendar.getInstance()
        calendar.time = gregorianDate

        val gregorianYear = calendar.get(Calendar.YEAR)
        val gregorianMonth = calendar.get(Calendar.MONTH) + 1
        val gregorianDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Calculate Bangla year
        var banglaYear = gregorianYear - 593
        if (gregorianMonth < 4 || (gregorianMonth == 4 && gregorianDay < 14)) {
            banglaYear--
        }

        // Calculate Bangla month and day
        val (banglaMonth, banglaDay) = when {
            // Baisakh
            gregorianMonth == 4 && gregorianDay >= 14 -> Pair(0, gregorianDay - 13)
            gregorianMonth == 5 && gregorianDay <= 14 -> Pair(0, gregorianDay + 18)

            // Jaistha
            gregorianMonth == 5 && gregorianDay >= 15 -> Pair(1, gregorianDay - 14)
            gregorianMonth == 6 && gregorianDay <= 14 -> Pair(1, gregorianDay + 17)

            // Similar patterns for other months...
            else -> calculateBanglaMonthDay(gregorianMonth, gregorianDay)
        }

        return BanglaDate(
            day = banglaDay,
            month = banglaMonths[banglaMonth],
            year = banglaYear
        )
    }

    private fun calculateBanglaMonthDay(gregorianMonth: Int, gregorianDay: Int): Pair<Int, Int> {
        // Default calculation for other months
        val banglaMonth = (gregorianMonth + 7) % 12
        val banglaDay = if (gregorianDay >= 15) {
            gregorianDay - 14
        } else {
            gregorianDay + daysInMonth[banglaMonth] - 14
        }
        return Pair(banglaMonth, banglaDay)
    }
}