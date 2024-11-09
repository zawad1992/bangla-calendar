package com.errorpoint.banglacalendar

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.LruCache
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.errorpoint.banglacalendar.adapter.CalendarAdapter
import com.errorpoint.banglacalendar.utils.BanglaDate
import com.errorpoint.banglacalendar.utils.BanglaDateConverter
import com.errorpoint.banglacalendar.utils.BanglaMonthYearPickerDialog
import java.util.*

class MainActivity : AppCompatActivity() {
    private var banglaTypeface: Typeface? = null
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: ImageButton
    private lateinit var nextMonth: ImageButton
    private lateinit var banglaMonthYear: TextView
    private lateinit var currentDateButton: Button
    private lateinit var calendarAdapter: CalendarAdapter

    private var selectedDate = Calendar.getInstance()
    private val dateListCache = LruCache<String, List<Date>>(12) // Cache for last 12 months
    private val calendarCache = mutableMapOf<String, Calendar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        banglaTypeface = ResourcesCompat.getFont(this, R.font.bangla_font)

        initViews()
        initCalendarAdapter()
        setListeners()
        setUpCalendar()
        setupBanglaFonts()
    }

    private fun initCalendarAdapter() {
        calendarAdapter = CalendarAdapter(
            banglaTypeface = banglaTypeface,
            onDateClick = { date ->
                val banglaClickedDate = BanglaDateConverter.convertToBanglaDate(date)
                // Handle date click
            }
        )

        calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
    }

    private fun initViews() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearTV)
        prevMonth = findViewById(R.id.previousButton)
        nextMonth = findViewById(R.id.nextButton)
        banglaMonthYear = findViewById(R.id.banglaMonthYear)
        currentDateButton = findViewById(R.id.currentDateButton)
    }

    private fun setupBanglaFonts() {
        banglaTypeface?.let { typeface ->
            banglaMonthYear.typeface = typeface
            currentDateButton.typeface = typeface

            val weekDaysLayout = findViewById<LinearLayout>(R.id.weekDaysLayout)
            for (i in 0 until weekDaysLayout.childCount) {
                val child = weekDaysLayout.getChildAt(i)
                if (child is TextView) {
                    child.typeface = typeface
                }
            }

            if (::calendarAdapter.isInitialized) {
                calendarAdapter.updateTypeface(typeface)
            }
        }
    }

    private fun setListeners() {
        prevMonth.setOnClickListener {
            selectedDate.add(Calendar.MONTH, -1)
            setUpCalendar()
        }

        nextMonth.setOnClickListener {
            selectedDate.add(Calendar.MONTH, 1)
            setUpCalendar()
        }

        currentDateButton.setOnClickListener {
            selectedDate = Calendar.getInstance()
            setUpCalendar()
        }

        monthYearText.setOnClickListener {
            showEnglishDatePicker()
        }

        banglaMonthYear.setOnClickListener {
            showBanglaMonthPicker()
        }
    }

    private fun setUpCalendar() {
        val monthYearFormatter = android.text.format.DateFormat.format("MMMM yyyy", selectedDate)
        monthYearText.text = monthYearFormatter.toString()

        val currentDate = Calendar.getInstance()
        currentDateButton.visibility = if (
            selectedDate.get(Calendar.MONTH) != currentDate.get(Calendar.MONTH) ||
            selectedDate.get(Calendar.YEAR) != currentDate.get(Calendar.YEAR)
        ) View.VISIBLE else View.GONE

        val key = "${selectedDate.get(Calendar.YEAR)}-${selectedDate.get(Calendar.MONTH)}"
        val banglaDate = getBanglaDateFromCache(key)
        banglaMonthYear.text = "${banglaDate.month} ${convertToNumerals(banglaDate.year)}"

        calendarAdapter.updateData(getDaysInMonth(selectedDate))
    }

    private fun getDaysInMonth(calendar: Calendar): List<Date> {
        val key = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}"

        return dateListCache.get(key) ?: run {
            val dates = generateDaysInMonth(calendar)
            dateListCache.put(key, dates)
            dates
        }
    }

    private fun generateDaysInMonth(calendar: Calendar): List<Date> {
        val dates = ArrayList<Date>(42) // Pre-size ArrayList
        val monthCalendar = getCalendarFromCache(calendar)

        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        repeat(42) {
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    private fun getCalendarFromCache(calendar: Calendar): Calendar {
        val key = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}"

        return calendarCache.getOrPut(key) {
            (calendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }

    private fun getBanglaDateFromCache(key: String): BanglaDate {
        return banglaDateCache.getOrPut(key) {
            BanglaDateConverter.convertToBanglaDate(Date(selectedDate.timeInMillis))
        }
    }

    private fun showEnglishDatePicker() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_english_date_picker)

        val monthPicker = dialog.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.yearPicker)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = months
        monthPicker.value = selectedDate.get(Calendar.MONTH)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear + 100
        yearPicker.value = selectedDate.get(Calendar.YEAR)

        btnOk.setOnClickListener {
            selectedDate.set(Calendar.YEAR, yearPicker.value)
            selectedDate.set(Calendar.MONTH, monthPicker.value)
            setUpCalendar()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBanglaMonthPicker() {
        val currentBanglaDate = BanglaDateConverter.convertToBanglaDate(Date(selectedDate.timeInMillis))
        val dialog = BanglaMonthYearPickerDialog(
            this,
            currentBanglaDate
        ) { year, month ->
            val englishDate = BanglaDateConverter.convertToGregorianDate(
                BanglaDate(1, month, year)
            )
            selectedDate.time = englishDate
            setUpCalendar()
        }
        dialog.show()
    }

    private fun convertToNumerals(number: Int): String {
        return number.toString().map {
            when(it) {
                '0' -> '০'
                '1' -> '১'
                '2' -> '২'
                '3' -> '৩'
                '4' -> '৪'
                '5' -> '৫'
                '6' -> '৬'
                '7' -> '৭'
                '8' -> '৮'
                '9' -> '৯'
                else -> it
            }
        }.joinToString("")
    }

    companion object {
        private val banglaDateCache = mutableMapOf<String, BanglaDate>()
    }
}