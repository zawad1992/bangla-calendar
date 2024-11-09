package com.errorpoint.banglacalendar.utils

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

    fun getBanglaMonths(): Array<String> {
        return banglaMonths
    }

    private val daysInMonth = arrayOf(31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30, 30)

    fun convertToBanglaDate(gregorianDate: Date): BanglaDate {
        val calendar = Calendar.getInstance()
        calendar.time = gregorianDate

        val gregorianYear = calendar.get(Calendar.YEAR)
        val gregorianMonth = calendar.get(Calendar.MONTH) + 1
        val gregorianDay = calendar.get(Calendar.DAY_OF_MONTH)

        var banglaYear = gregorianYear - 593
        if (gregorianMonth < 4 || (gregorianMonth == 4 && gregorianDay < 14)) {
            banglaYear--
        }

        val (banglaMonth, banglaDay) = when {
            // Baisakh
            gregorianMonth == 4 && gregorianDay >= 14 -> Pair(0, gregorianDay - 13)
            gregorianMonth == 5 && gregorianDay <= 14 -> Pair(0, gregorianDay + 18)

            // Jaistha
            gregorianMonth == 5 && gregorianDay >= 15 -> Pair(1, gregorianDay - 14)
            gregorianMonth == 6 && gregorianDay <= 14 -> Pair(1, gregorianDay + 17)

            else -> calculateBanglaMonthDay(gregorianMonth, gregorianDay)
        }

        return BanglaDate(
            day = banglaDay,
            month = banglaMonths[banglaMonth],
            year = banglaYear
        )
    }

    fun convertToGregorianDate(banglaDate: BanglaDate): Date {
        val banglaMonthIndex = banglaMonths.indexOf(banglaDate.month)
        val calendar = Calendar.getInstance()

        // Calculate Gregorian year
        var gregorianYear = banglaDate.year + 593

        // Set initial day as 1 since we're only concerned with month/year conversion
        val gregorianDate = when (banglaMonthIndex) {
            0 -> { // Baisakh (Mid April to Mid May)
                calendar.set(gregorianYear, Calendar.APRIL, 14)
                calendar.time
            }
            1 -> { // Jaistha (Mid May to Mid June)
                calendar.set(gregorianYear, Calendar.MAY, 15)
                calendar.time
            }
            2 -> { // Ashar (Mid June to Mid July)
                calendar.set(gregorianYear, Calendar.JUNE, 15)
                calendar.time
            }
            3 -> { // Shraban (Mid July to Mid August)
                calendar.set(gregorianYear, Calendar.JULY, 16)
                calendar.time
            }
            4 -> { // Bhadra (Mid August to Mid September)
                calendar.set(gregorianYear, Calendar.AUGUST, 16)
                calendar.time
            }
            5 -> { // Ashwin (Mid September to Mid October)
                calendar.set(gregorianYear, Calendar.SEPTEMBER, 16)
                calendar.time
            }
            6 -> { // Kartik (Mid October to Mid November)
                calendar.set(gregorianYear, Calendar.OCTOBER, 16)
                calendar.time
            }
            7 -> { // Agrahayan (Mid November to Mid December)
                calendar.set(gregorianYear, Calendar.NOVEMBER, 15)
                calendar.time
            }
            8 -> { // Poush (Mid December to Mid January)
                calendar.set(gregorianYear, Calendar.DECEMBER, 15)
                calendar.time
            }
            9 -> { // Magh (Mid January to Mid February)
                calendar.set(gregorianYear + 1, Calendar.JANUARY, 14)
                calendar.time
            }
            10 -> { // Falgun (Mid February to Mid March)
                calendar.set(gregorianYear + 1, Calendar.FEBRUARY, 13)
                calendar.time
            }
            11 -> { // Chaitra (Mid March to Mid April)
                calendar.set(gregorianYear + 1, Calendar.MARCH, 14)
                calendar.time
            }
            else -> calendar.time // Should never happen
        }

        return gregorianDate
    }

    private fun calculateBanglaMonthDay(gregorianMonth: Int, gregorianDay: Int): Pair<Int, Int> {
        val banglaMonth = when {
            gregorianMonth == 4 && gregorianDay >= 14 -> 0  // Baisakh
            gregorianMonth == 5 && gregorianDay < 15 -> 0
            gregorianMonth == 5 && gregorianDay >= 15 -> 1  // Jaistha
            gregorianMonth == 6 && gregorianDay < 15 -> 1
            gregorianMonth == 6 && gregorianDay >= 15 -> 2  // Ashar
            gregorianMonth == 7 && gregorianDay < 16 -> 2
            gregorianMonth == 7 && gregorianDay >= 16 -> 3  // Shraban
            gregorianMonth == 8 && gregorianDay < 16 -> 3
            gregorianMonth == 8 && gregorianDay >= 16 -> 4  // Bhadra
            gregorianMonth == 9 && gregorianDay < 16 -> 4
            gregorianMonth == 9 && gregorianDay >= 16 -> 5  // Ashwin
            gregorianMonth == 10 && gregorianDay < 16 -> 5
            gregorianMonth == 10 && gregorianDay >= 16 -> 6 // Kartik
            gregorianMonth == 11 && gregorianDay < 15 -> 6
            gregorianMonth == 11 && gregorianDay >= 15 -> 7 // Agrahayan
            gregorianMonth == 12 && gregorianDay < 15 -> 7
            gregorianMonth == 12 && gregorianDay >= 15 -> 8 // Poush
            gregorianMonth == 1 && gregorianDay < 14 -> 8
            gregorianMonth == 1 && gregorianDay >= 14 -> 9  // Magh
            gregorianMonth == 2 && gregorianDay < 13 -> 9
            gregorianMonth == 2 && gregorianDay >= 13 -> 10 // Falgun
            gregorianMonth == 3 && gregorianDay < 15 -> 10
            gregorianMonth == 3 && gregorianDay >= 15 -> 11 // Chaitra
            gregorianMonth == 4 && gregorianDay < 14 -> 11
            else -> 0
        }

        val banglaDay = when {
            gregorianDay >= 15 -> gregorianDay - 14
            else -> gregorianDay + 15
        }

        return Pair(banglaMonth, banglaDay)
    }
}