package com.example.imageeditor.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

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
}