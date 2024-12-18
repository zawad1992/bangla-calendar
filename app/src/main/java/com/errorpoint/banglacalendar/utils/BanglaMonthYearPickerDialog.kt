package com.errorpoint.banglacalendar.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Button
import android.view.Window
import android.widget.LinearLayout
import com.errorpoint.banglacalendar.R

class BanglaMonthYearPickerDialog(
    context: Context,
    private val currentDate: BanglaDate,
    private val onDateSelected: (year: Int, month: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_bangla_date_picker)

        val monthPicker = findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = findViewById<NumberPicker>(R.id.yearPicker)
        val btnOk = findViewById<Button>(R.id.btnOk)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // Set up month picker
        val months = BanglaDateConverter.getBanglaMonths()
        monthPicker.minValue = 0
        monthPicker.maxValue = months.size - 1
        monthPicker.displayedValues = months
        monthPicker.value = months.indexOf(currentDate.month)

        // Set up year picker
        val currentYear = currentDate.year
        val yearRange = 100
        yearPicker.minValue = currentDate.year - 100
        yearPicker.maxValue = currentDate.year + 100
        yearPicker.value = currentDate.year

        // Convert years to Bangla numerals
        val banglaYears = Array(yearRange * 2 + 1) { index ->
            convertToBanglaNumeral(yearPicker.minValue + index)
        }
        yearPicker.displayedValues = banglaYears


        btnOk.setOnClickListener {
            onDateSelected(yearPicker.value, months[monthPicker.value])
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
    private fun convertToBanglaNumeral(number: Int): String {
        val banglaNumerals = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return number.toString().map { digit ->
            if (digit.isDigit()) {
                banglaNumerals[digit.toString().toInt()]
            } else {
                digit
            }
        }.joinToString("")
    }
}