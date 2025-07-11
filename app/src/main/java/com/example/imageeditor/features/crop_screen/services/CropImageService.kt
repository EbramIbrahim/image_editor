package com.example.imageeditor.features.crop_screen.services

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect

interface CropImageService {
    fun cropImage(bitmap: Bitmap, cropRect: Rect): Bitmap
}