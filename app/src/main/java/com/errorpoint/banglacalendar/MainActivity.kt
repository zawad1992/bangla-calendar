package com.errorpoint.banglacalendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.ImageButton
import com.errorpoint.banglacalendar.adapter.CalendarAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: ImageButton
    private lateinit var nextMonth: ImageButton
    private lateinit var banglaMonthYear: TextView

    private var selectedDate = Calendar.getInstance()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setListeners()
        setUpCalendar()
    }

    private fun initViews() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearTV)
        prevMonth = findViewById(R.id.previousButton)
        nextMonth = findViewById(R.id.nextButton)
        banglaMonthYear = findViewById(R.id.banglaMonthYear)
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
    }

    private fun setUpCalendar() {
        monthYearText.text = android.text.format.DateFormat.format("MMMM yyyy", selectedDate)

        // Create a Date object from the Calendar
        val currentDate = Date(selectedDate.timeInMillis)
        val banglaDate = BanglaDateConverter.convertToBanglaDate(currentDate)
        banglaMonthYear.text = "${banglaDate.month} ${banglaDate.year}"

        val daysInMonth = getDaysInMonth(selectedDate)
        calendarAdapter = CalendarAdapter(daysInMonth) { date ->
            // Handle date click
            val banglaClickedDate = BanglaDateConverter.convertToBanglaDate(date)
            // You can show details in a bottom sheet or dialog
        }

        calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
    }

    private fun getDaysInMonth(calendar: Calendar): List<Date> {
        val dates = mutableListOf<Date>()

        // Copy the calendar instance to avoid modifying the original
        val monthCalendar = calendar.clone() as Calendar

        // Set to first day of month
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        // Fill in days from previous month
        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        // Add 42 days (6 weeks) to show complete calendar
        while (dates.size < 42) {
            dates.add(Date(monthCalendar.timeInMillis))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }
}