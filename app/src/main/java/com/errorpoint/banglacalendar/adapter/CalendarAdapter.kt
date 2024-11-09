package com.errorpoint.banglacalendar.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.errorpoint.banglacalendar.R
import com.errorpoint.banglacalendar.utils.BanglaDate
import com.errorpoint.banglacalendar.utils.BanglaDateConverter
import java.util.*

class CalendarAdapter(
    private var dates: List<Date> = listOf(),
    private var banglaTypeface: Typeface? = null,
    private val onDateClick: (Date) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val dayFormatCache = mutableMapOf<Int, String>()
    private val banglaDateCache = mutableMapOf<Date, BanglaDate>()
    private val displayedMonthCalendar = Calendar.getInstance()
    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val banglaDate: TextView = itemView.findViewById(R.id.cellBanglaDate)
        val englishDate: TextView = itemView.findViewById(R.id.cellDayText)
        val banglaMonth: TextView = itemView.findViewById(R.id.cellBanglaMonth)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(view)
    }

    fun updateData(newDates: List<Date>) {
        this.dates = newDates
        dayFormatCache.clear()
        banglaDateCache.clear()

        if (dates.isNotEmpty()) {
            displayedMonthCalendar.time = dates[15]
            displayedMonthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        }
        notifyDataSetChanged()
    }

    fun updateTypeface(typeface: Typeface) {
        banglaTypeface = typeface
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        if (dates.isEmpty()) return

        banglaTypeface?.let { typeface ->
            holder.banglaDate.typeface = typeface
            holder.banglaMonth.typeface = typeface
        }

        val date = dates[position]
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val banglaDate = getBanglaDateFromCache(date)

        holder.banglaDate.text = getDayFromCache(banglaDate.day)
        holder.englishDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        val showMonth = when {
            position == 0 -> true
            position > 0 -> {
                val previousDate = getBanglaDateFromCache(dates[position - 1])
                previousDate.month != banglaDate.month
            }
            else -> false
        }

        holder.banglaMonth.apply {
            text = if (showMonth) banglaDate.month else ""
            visibility = if (showMonth) View.VISIBLE else View.GONE
        }

        val isCurrentMonth = calendar.get(Calendar.MONTH) == displayedMonthCalendar.get(Calendar.MONTH)

        updateCellVisibility(holder, isCurrentMonth)
        updateCellStyle(holder, calendar, isCurrentMonth)

        holder.itemView.setOnClickListener { onDateClick(date) }
    }

    private fun updateCellVisibility(holder: CalendarViewHolder, isCurrentMonth: Boolean) {
        val alpha = if (isCurrentMonth) {
            Triple(1.0f, 0.7f, 1.0f) // banglaDate, englishDate, banglaMonth
        } else {
            Triple(0.5f, 0.3f, 0.5f)
        }

        holder.banglaDate.alpha = alpha.first
        holder.englishDate.alpha = alpha.second
        holder.banglaMonth.alpha = alpha.third
    }

    private fun updateCellStyle(holder: CalendarViewHolder, calendar: Calendar, isCurrentMonth: Boolean) {
        val isToday = calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                      calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)

        if (isToday) {
            holder.itemView.setBackgroundResource(R.drawable.today_background)
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.englishDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white_70))
            holder.banglaMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.itemView.setBackgroundResource(R.drawable.calendar_cell_bg)
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.calendar_text_primary))
            holder.englishDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.calendar_text_secondary))
            holder.banglaMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.calendar_primary))
        }

        // Add weekend colors if needed
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if ((dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) && !isToday) {
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.weekend_primary))
        }
    }

    private fun getBanglaDateFromCache(date: Date): BanglaDate {
        return banglaDateCache.getOrPut(date) {
            BanglaDateConverter.convertToBanglaDate(date)
        }
    }

    private fun getDayFromCache(day: Int): String {
        return dayFormatCache.getOrPut(day) {
            convertToNumerals(day)
        }
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

    override fun getItemCount(): Int = dates.size
}