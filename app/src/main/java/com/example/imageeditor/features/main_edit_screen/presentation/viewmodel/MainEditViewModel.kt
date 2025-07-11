package com.example.imageeditor.features.main_edit_screen.presentation.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainEditViewModel: ViewModel() {

    private val _imageState = MutableStateFlow<ImageBitmap?>(null)
    val imageState: StateFlow<ImageBitmap?> = _imageState.asStateFlow()

    fun updateImage(newImage: ImageBitmap) {
        _imageState.value = newImage
    }

    fun setInitialImage(image: ImageBitmap) {
        _imageState.value = image
    }
}