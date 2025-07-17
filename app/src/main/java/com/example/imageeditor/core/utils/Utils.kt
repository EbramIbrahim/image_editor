package com.example.imageeditor.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

object Utils {

    val allColors = listOf(
        Color.Black,
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
    )

    fun readPermissionsGranted(context: Context): Boolean {
        val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return ContextCompat.checkSelfPermission(
            context,
            readPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun emojiToBitmap(emoji: String, size: Int): ImageBitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size.toFloat()
            textAlign = Paint.Align.LEFT
        }

        val bounds = Rect()
        paint.getTextBounds(emoji, 0, emoji.length, bounds)

        val bitmap = createBitmap(bounds.width(), bounds.height())

        val canvas = Canvas(bitmap)
        canvas.drawText(emoji, 0f, -bounds.top.toFloat(), paint)

        return bitmap.asImageBitmap()
    }
}