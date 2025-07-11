package com.example.imageeditor.features.crop_screen.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageeditor.features.crop_screen.services.CropImageService
import com.example.imageeditor.features.crop_screen.services.CropImageServiceImpl
import kotlinx.coroutines.launch

class CropImageViewModel : ViewModel() {

    private val cropImageService: CropImageService = CropImageServiceImpl()
    fun onCrop(cropRect: Rect, bitmap: ImageBitmap): Bitmap {
        return cropImageService.cropImage(
            bitmap = bitmap.asAndroidBitmap(),
            cropRect = cropRect
        )
    }
}