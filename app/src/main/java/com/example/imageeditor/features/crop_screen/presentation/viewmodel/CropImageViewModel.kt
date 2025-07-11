package com.example.imageeditor.features.crop_screen.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import com.example.imageeditor.features.crop_screen.services.CropImageService
import com.example.imageeditor.features.crop_screen.services.CropImageServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CropImageViewModel : ViewModel() {

    private val _editedBitmap = MutableStateFlow<Bitmap?>(null)
    val editedBitmap = _editedBitmap.asStateFlow()


    private val cropImageService: CropImageService = CropImageServiceImpl()

    fun onCrop(cropRect: Rect, bitmap: ImageBitmap) {
        _editedBitmap.value =
            cropImageService.cropImage(bitmap = bitmap.asAndroidBitmap(), cropRect = cropRect)
    }
}