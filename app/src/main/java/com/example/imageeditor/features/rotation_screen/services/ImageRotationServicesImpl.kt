package com.example.imageeditor.features.rotation_screen.services

import android.graphics.Bitmap
import android.graphics.Matrix

class ImageRotationServicesImpl: ImageRotationServices {
    override fun rotate(
        bitmap: Bitmap,
        degree: Float
    ): Bitmap {
        val matrix = Matrix().apply { postRotate(degree) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}