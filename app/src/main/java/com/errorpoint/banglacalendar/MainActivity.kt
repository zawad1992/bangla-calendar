package com.errorpoint.banglacalendar

import android.app.Dialog
import android.os.Bundle
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
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: ImageButton
    private lateinit var nextMonth: ImageButton
    private lateinit var banglaMonthYear: TextView
    private lateinit var currentDateButton: Button
    private lateinit var calendarAdapter: CalendarAdapter

    private var selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setListeners()
        setUpCalendar()
        setupBanglaFonts()
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
        val banglaTypeface = ResourcesCompat.getFont(this, R.font.bangla_font)
        banglaTypeface?.let { typeface ->
            banglaMonthYear.typeface = typeface
            currentDateButton.typeface = typeface

            // Set Bangla typeface for weekday headers
            val weekDaysLayout = findViewById<LinearLayout>(R.id.weekDaysLayout)
            for (i in 0 until weekDaysLayout.childCount) {
                val child = weekDaysLayout.getChildAt(i)
                if (child is TextView) {
                    child.typeface = typeface
                }
            }

            // Update adapter with new typeface
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


    private fun showEnglishDatePicker() {
        // Create custom dialog for month-year picker
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_month_year_picker)

        val monthPicker = dialog.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.yearPicker)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        // Set up month picker
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = months
        monthPicker.value = selectedDate.get(Calendar.MONTH)

        // Set up year picker
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear + 100
        yearPicker.value = selectedDate.get(Calendar.YEAR)

        // Handle button clicks
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
            // Convert selected Bangla date to English
            val englishDate = BanglaDateConverter.convertToGregorianDate(
                BanglaDate(1, month, year)
            )
            selectedDate.time = englishDate
            setUpCalendar()
        }
        dialog.show()
    }


     private fun setUpCalendar() {
        monthYearText.text = android.text.format.DateFormat.format("MMMM yyyy", selectedDate)

        val currentDate = Calendar.getInstance()
        currentDateButton.visibility = if (
            selectedDate.get(Calendar.MONTH) != currentDate.get(Calendar.MONTH) ||
            selectedDate.get(Calendar.YEAR) != currentDate.get(Calendar.YEAR)
        ) View.VISIBLE else View.GONE

        val banglaDate = BanglaDateConverter.convertToBanglaDate(Date(selectedDate.timeInMillis))
        banglaMonthYear.text = "${banglaDate.month} ${convertToNumerals(banglaDate.year)}"

        val daysInMonth = getDaysInMonth(selectedDate)
        val banglaTypeface = ResourcesCompat.getFont(this, R.font.bangla_font)
        calendarAdapter = CalendarAdapter(
            dates = daysInMonth,
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

    private fun getDaysInMonth(calendar: Calendar): List<Date> {
        val dates = mutableListOf<Date>()

        // Clone the calendar to avoid modifying the original
        val monthCalendar = calendar.clone() as Calendar

        // Set to first day of month
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        // Calculate the offset for the first day of month
        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        // Add 42 days (6 weeks) to show complete calendar
        while (dates.size < 42) {
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
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
}