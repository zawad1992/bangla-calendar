package com.errorpoint.banglacalendar.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import com.errorpoint.banglacalendar.MainActivity
import com.errorpoint.banglacalendar.R
import com.errorpoint.banglacalendar.utils.BanglaDateConverter
import java.text.SimpleDateFormat
import java.util.*

class BanglaCalendarWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d("BanglaWidget", "onReceive: ${intent?.action}")
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("BanglaWidget", "onUpdate called with ${appWidgetIds.size} widgets")

        for (appWidgetId in appWidgetIds) {
            try {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            } catch (e: Exception) {
                Log.e("BanglaWidget", "Error updating widget", e)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        try {
            // Create RemoteViews
            val views = RemoteViews(context.packageName, R.layout.widget_bangla_calendar)

            // Get current date information
            val currentDate = Calendar.getInstance()
            val banglaDate = BanglaDateConverter.convertToBanglaDate(currentDate.time)

            // Format English date and day
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

            val englishDay = dayFormat.format(currentDate.time)
            val englishDate = dateFormat.format(currentDate.time)

            // Update widget views
            views.setTextViewText(R.id.widget_english_day, englishDay)
            views.setTextViewText(R.id.widget_english_date, englishDate)
            views.setTextViewText(R.id.widget_bangla_date,
                "${convertToNumerals(banglaDate.day)} ${banglaDate.month} ${convertToNumerals(banglaDate.year)}")

            // Create pending intent for widget click
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            }
            views.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d("BanglaWidget", "Widget updated successfully")

        } catch (e: Exception) {
            Log.e("BanglaWidget", "Error in updateAppWidget", e)
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
}