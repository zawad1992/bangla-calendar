package com.errorpoint.banglacalendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.errorpoint.banglacalendar.BanglaDateConverter
import com.errorpoint.banglacalendar.R
import java.util.*

class CalendarAdapter(
    private val dates: List<Date>,
    private val onDateClick: (Date) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val today = Calendar.getInstance()

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
        val banglaDate: TextView = itemView.findViewById(R.id.cellBanglaDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = dates[position]
        val calendar = Calendar.getInstance().apply { time = date }

        // Get current month calendar for comparison
        val currentMonthCalendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Get displayed month calendar for comparison
        val displayedMonthCalendar = Calendar.getInstance().apply {
            time = dates[15] // Middle date is always in the displayed month
            set(Calendar.DAY_OF_MONTH, 1)
        }

        holder.dayOfMonth.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        // Convert to Bangla date
        val banglaDate = BanglaDateConverter.convertToBanglaDate(date)
        holder.banglaDate.text = banglaDate.day.toString()

        // Check if date is in current month
        val isCurrentMonth = calendar.get(Calendar.MONTH) == displayedMonthCalendar.get(Calendar.MONTH)

        // Style for dates from previous/next month
        if (!isCurrentMonth) {
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.calendar_secondary_text))
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.calendar_secondary_text))
            holder.itemView.alpha = 0.5f
        } else {
            holder.itemView.alpha = 1.0f
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.calendar_secondary_text))
        }

        // Check if this date is today
        if (calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            holder.itemView.setBackgroundResource(R.drawable.today_background)
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.banglaDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.itemView.setBackgroundResource(R.drawable.calendar_cell_bg)
        }

        // Set weekends text color
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
            if (!isCurrentMonth) {
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.weekend_secondary))
            } else if (calendar.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)) {
                holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.weekend_primary))
            }
        }

        holder.itemView.setOnClickListener { onDateClick(date) }
    }

    override fun getItemCount(): Int = dates.size
}