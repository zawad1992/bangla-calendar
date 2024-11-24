package com.errorpoint.banglacalendar.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.errorpoint.banglacalendar.R
import java.io.File
import java.io.FileOutputStream

class WidgetPreviewProvider(private val context: Context) {
    private val previewFileName = "widget_preview.png"

    fun getOrCreatePreview(): Int {
        // Check if preview resource exists
        val resourceId = R.drawable.widget_preview
        if (resourceExists(resourceId)) {
            return resourceId
        }

        // Generate and save preview
        val preview = WidgetPreviewGenerator(context).generatePreview()
        savePreview(preview)
        return resourceId
    }

    private fun resourceExists(resourceId: Int): Boolean {
        return try {
            ContextCompat.getDrawable(context, resourceId) != null
        } catch (e: Exception) {
            false
        }
    }

    private fun savePreview(bitmap: Bitmap) {
        try {
            // Save to app's internal storage
            val previewFile = File(context.filesDir, previewFileName)
            FileOutputStream(previewFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Copy to res/drawable
            val drawableDir = File(context.filesDir.parent ?: "", "res/drawable")
            if (!drawableDir.exists()) {
                drawableDir.mkdirs()
            }
            val destFile = File(drawableDir, previewFileName)
            previewFile.copyTo(destFile, overwrite = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}