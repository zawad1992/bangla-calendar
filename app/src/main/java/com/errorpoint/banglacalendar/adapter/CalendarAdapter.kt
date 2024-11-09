package com.errorpoint.banglacalendar.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.errorpoint.banglacalendar.R
import com.errorpoint.banglacalendar.utils.BanglaDateConverter
import java.util.*

class CalendarAdapter(
    private val dates: List<Date>,
    private var banglaTypeface: Typeface?,
    private val onDateClick: (Date) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    fun updateTypeface(typeface: Typeface) {
        banglaTypeface = typeface
        notifyDataSetChanged()
    }

    private val today = Calendar.getInstance()

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val banglaDate: TextView = itemView.findViewById(R.id.cellBanglaDate)
        val englishDate: TextView = itemView.findViewById(R.id.cellDayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        banglaTypeface?.let { typeface ->
            holder.banglaDate.typeface = typeface
        }

        val date = dates[position]
        val calendar = Calendar.getInstance().apply { time = date }
        val displayedMonthCalendar = Calendar.getInstance().apply {
            time = dates[15]
            set(Calendar.DAY_OF_MONTH, 1)
        }

        // Convert to Bangla date
        val banglaDate = BanglaDateConverter.convertToBanglaDate(date)

        // Set dates with Bangla numerals for Bangla date
        holder.banglaDate.text = convertToNumerals(banglaDate.day)
        holder.englishDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        // Check if date is in current month
        val isCurrentMonth = calendar.get(Calendar.MONTH) == displayedMonthCalendar.get(Calendar.MONTH)

        if (!isCurrentMonth) {
            holder.banglaDate.alpha = 0.5f
            holder.englishDate.alpha = 0.3f  // Make English date even lighter when not in current month
        } else {
            holder.banglaDate.alpha = 1.0f
            holder.englishDate.alpha = 0.7f  // Slightly dim English date to emphasize Bangla date
        }

        // Style for today
        if (calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            holder.itemView.setBackgroundResource(R.drawable.today_background)
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.englishDate.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.white_70))  // Slightly transparent white for English date
        } else {
            holder.itemView.setBackgroundResource(R.drawable.calendar_cell_bg)
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            holder.englishDate.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.calendar_secondary_text))
        }

        holder.itemView.setOnClickListener { onDateClick(date) }
    }

    override fun getItemCount(): Int = dates.size

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