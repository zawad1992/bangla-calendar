package com.errorpoint.banglacalendar

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.LruCache
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.errorpoint.banglacalendar.adapter.CalendarPagerAdapter
import com.errorpoint.banglacalendar.utils.BanglaDate
import com.errorpoint.banglacalendar.utils.BanglaDateConverter
import com.errorpoint.banglacalendar.utils.BanglaMonthYearPickerDialog
import com.errorpoint.banglacalendar.widget.WidgetPreviewProvider
import java.util.*

class MainActivity : AppCompatActivity() {
    private var banglaTypeface: Typeface? = null
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: ImageButton
    private lateinit var nextMonth: ImageButton
    private lateinit var banglaMonthYear: TextView
    private lateinit var currentDateButton: Button
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: CalendarPagerAdapter

    private var selectedDate = Calendar.getInstance()
    private val dateListCache = LruCache<String, List<Date>>(12) // Cache for last 12 months
    private val calendarCache = mutableMapOf<String, Calendar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Generate widget preview if needed
        WidgetPreviewProvider(this).getOrCreatePreview()

        banglaTypeface = ResourcesCompat.getFont(this, R.font.bangla_font)

        initViews()
        initViewPager()
        setListeners()
        setupBanglaFonts()
        updateMonthYearDisplay()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.calendarViewPager)
        monthYearText = findViewById(R.id.monthYearTV)
        prevMonth = findViewById(R.id.previousButton)
        nextMonth = findViewById(R.id.nextButton)
        banglaMonthYear = findViewById(R.id.banglaMonthYear)
        currentDateButton = findViewById(R.id.currentDateButton)
    }

    private fun initViewPager() {
        pagerAdapter = CalendarPagerAdapter(
            banglaTypeface = banglaTypeface,
            onDateClick = { date ->
                val banglaClickedDate = BanglaDateConverter.convertToBanglaDate(date)
                // Handle date click
            }
        )

        viewPager.apply {
            adapter = pagerAdapter
            setCurrentItem(pagerAdapter.getMiddlePage(), false)

            // Reduce swipe sensitivity
            (getChildAt(0) as? RecyclerView)?.let { recyclerView ->
                recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
                val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
                recyclerView.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            recyclerView.parent.requestDisallowInterceptTouchEvent(true)
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (event.pointerCount > 1) {
                                recyclerView.parent.requestDisallowInterceptTouchEvent(false)
                            }
                        }
                    }
                    false
                }
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val monthOffset = position - pagerAdapter.getMiddlePage()
                    selectedDate.apply {
                        time = Calendar.getInstance().time
                        add(Calendar.MONTH, monthOffset)
                    }
                    updateMonthYearDisplay()
                }
            })
        }
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
        }
    }

    private fun setListeners() {
        prevMonth.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
        }

        nextMonth.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem + 1
        }

        currentDateButton.setOnClickListener {
            viewPager.setCurrentItem(pagerAdapter.getMiddlePage(), true)
            selectedDate = Calendar.getInstance()
            updateMonthYearDisplay()
        }

        monthYearText.setOnClickListener {
            showEnglishDatePicker()
        }

        banglaMonthYear.setOnClickListener {
            showBanglaMonthPicker()
        }
    }

    private fun updateMonthYearDisplay() {
        val monthYearFormatter = android.text.format.DateFormat.format("MMMM yyyy", selectedDate)
        monthYearText.text = monthYearFormatter.toString()

        val currentDate = Calendar.getInstance()
        currentDateButton.visibility = if (
            selectedDate.get(Calendar.MONTH) != currentDate.get(Calendar.MONTH) ||
            selectedDate.get(Calendar.YEAR) != currentDate.get(Calendar.YEAR)
        ) View.VISIBLE else View.GONE

        // Update Bangla date display
        val firstDate = Calendar.getInstance().apply {
            time = selectedDate.time
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val lastDate = Calendar.getInstance().apply {
            time = selectedDate.time
            set(Calendar.DAY_OF_MONTH, selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        }

        val firstBanglaDate = BanglaDateConverter.convertToBanglaDate(firstDate.time)
        val lastBanglaDate = BanglaDateConverter.convertToBanglaDate(lastDate.time)

        banglaMonthYear.text = if (firstBanglaDate.month != lastBanglaDate.month) {
            "${firstBanglaDate.month} - ${lastBanglaDate.month} ${convertToNumerals(firstBanglaDate.year)}"
        } else {
            "${firstBanglaDate.month} ${convertToNumerals(firstBanglaDate.year)}"
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
            // Calculate the month difference to determine ViewPager position
            val newDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, yearPicker.value)
                set(Calendar.MONTH, monthPicker.value)
                set(Calendar.DAY_OF_MONTH, 1)
            }

            val currentPageDate = Calendar.getInstance().apply {
                add(Calendar.MONTH, viewPager.currentItem - pagerAdapter.getMiddlePage())
                set(Calendar.DAY_OF_MONTH, 1)
            }

            val monthDifference = ((newDate.get(Calendar.YEAR) - currentPageDate.get(Calendar.YEAR)) * 12 +
                    (newDate.get(Calendar.MONTH) - currentPageDate.get(Calendar.MONTH)))

            selectedDate.set(Calendar.YEAR, yearPicker.value)
            selectedDate.set(Calendar.MONTH, monthPicker.value)

            viewPager.setCurrentItem(viewPager.currentItem + monthDifference, true)
            updateMonthYearDisplay()
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
            val newDate = BanglaDateConverter.convertToGregorianDate(
                BanglaDate(1, month, year)
            )

            val newCal = Calendar.getInstance().apply {
                time = newDate
            }

            val currentPageDate = Calendar.getInstance().apply {
                add(Calendar.MONTH, viewPager.currentItem - pagerAdapter.getMiddlePage())
                set(Calendar.DAY_OF_MONTH, 1)
            }

            val monthDifference = ((newCal.get(Calendar.YEAR) - currentPageDate.get(Calendar.YEAR)) * 12 +
                    (newCal.get(Calendar.MONTH) - currentPageDate.get(Calendar.MONTH)))

            selectedDate.time = newDate
            viewPager.setCurrentItem(viewPager.currentItem + monthDifference, true)
            updateMonthYearDisplay()
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