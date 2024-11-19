package com.errorpoint.banglacalendar.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.errorpoint.banglacalendar.R
import java.util.*

class CalendarPagerAdapter(
    private var banglaTypeface: Typeface?,
    private val onDateClick: (Date) -> Unit
) : RecyclerView.Adapter<CalendarPagerAdapter.CalendarPageViewHolder>() {

    private val calendar = Calendar.getInstance()
    private val totalPages = 60 // Show 30 months before and after current month
    private val middlePage = totalPages / 2

    inner class CalendarPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.monthRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarPageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_page, parent, false)
        return CalendarPageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarPageViewHolder, position: Int) {
        val monthOffset = position - middlePage
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
        }

        holder.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 7)
            adapter = CalendarAdapter(
                banglaTypeface = banglaTypeface,
                onDateClick = onDateClick
            ).also { adapter ->
                adapter.updateData(getDaysInMonth(calendar))
            }
        }
    }

    private fun getDaysInMonth(calendar: Calendar): List<Date> {
        val dates = ArrayList<Date>()
        val monthCalendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        repeat(42) {
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    override fun getItemCount(): Int = totalPages

    fun getMiddlePage(): Int = middlePage

    fun updateTypeface(typeface: Typeface?) {
        banglaTypeface = typeface
        notifyDataSetChanged()
    }
}
