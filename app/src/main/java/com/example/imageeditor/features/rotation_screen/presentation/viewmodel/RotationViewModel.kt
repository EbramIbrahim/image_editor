package com.example.imageeditor.features.rotation_screen.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.example.imageeditor.features.rotation_screen.services.ImageRotationServices
import com.example.imageeditor.features.rotation_screen.services.ImageRotationServicesImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RotationViewModel: ViewModel() {


    private val _rotationDegree = MutableStateFlow(0f)
    val rotationDegree = _rotationDegree.asStateFlow()

    private val _rotationImage = MutableStateFlow<ImageBitmap?>(null)
    val rotationImage = _rotationImage.asStateFlow()

    private val rotationServices: ImageRotationServices = ImageRotationServicesImpl()

    fun rotateImage(
        bitmap: Bitmap,
        degree: Float
    ): Bitmap {
        return rotationServices.rotate(bitmap, degree)
    }

    fun updateRotationDegree(degree: Float) {
        _rotationDegree.value = degree
    }

    fun updateRotationImage(imageBitmap: ImageBitmap) {
        _rotationImage.value = imageBitmap
    }
}