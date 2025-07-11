package com.example.imageeditor.features.rotation_screen.services

import android.graphics.Bitmap

interface ImageRotationServices {
    fun rotate(bitmap: Bitmap, degree: Float): Bitmap
}