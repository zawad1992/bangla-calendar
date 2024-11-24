package com.errorpoint.banglacalendar.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.res.ResourcesCompat
import com.errorpoint.banglacalendar.R

class WidgetPreviewGenerator(private val context: Context) {
    fun generatePreview(): Bitmap {
        // Create a bitmap with the desired preview size (200x200 pixels is a good size for most devices)
        val width = 200
        val height = 200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw background
        val paint = Paint().apply {
            color = Color.argb(204, 0, 0, 0) // 80% opacity black
            isAntiAlias = true
        }
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, 32f, 32f, paint)

        // Get Bangla font
        val banglaTypeface = ResourcesCompat.getFont(context, R.font.bangla_font)

        // Draw date text
        paint.apply {
            color = Color.WHITE
            textSize = 36f
            typeface = banglaTypeface
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("২৫ বৈশাখ", width / 2f, height / 2f - 10f, paint)

        // Draw year
        paint.apply {
            textSize = 28f
        }
        canvas.drawText("১৪৩০", width / 2f, height / 2f + 30f, paint)

        return bitmap
    }
}