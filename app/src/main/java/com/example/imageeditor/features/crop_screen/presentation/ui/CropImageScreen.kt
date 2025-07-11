package com.example.imageeditor.features.crop_screen.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.imageeditor.core.presentation.component.ImageEditorTopAppBar
import com.example.imageeditor.features.crop_screen.presentation.component.CropImageLayout
import com.example.imageeditor.features.crop_screen.presentation.component.ImageCropperView
import com.example.imageeditor.features.crop_screen.presentation.viewmodel.CropImageViewModel

@Composable
fun CropImageScreen(
    currentImage: ImageBitmap,
    onUpdateCroppedImage: (ImageBitmap) -> Unit,
    navController: NavController
) {

    var cropMarkerTopLeft by remember { mutableStateOf(Offset.Unspecified) }
    var cropMarkerBottomRight by remember { mutableStateOf(Offset.Unspecified) }

    val viewModel = viewModel<CropImageViewModel>()

    Scaffold(
        topBar = {
            ImageEditorTopAppBar(
                title = "Edit Image",
                actionTitle = "Apply",
                onNavigationIconClicked = {
                    navController.navigateUp()
                },
                onActionIconClicked = {
                    if (cropMarkerTopLeft == Offset.Unspecified || cropMarkerBottomRight == Offset.Unspecified)
                        return@ImageEditorTopAppBar
                    // Crop incoming bitmap with the following Rect.
                    val cropRect = Rect(cropMarkerTopLeft, cropMarkerBottomRight)
                    val croppedImage = viewModel.onCrop(cropRect, currentImage).asImageBitmap()

                    Log.d("Cropped Image ......", croppedImage.width.toString())
                    onUpdateCroppedImage(croppedImage)
                    navController.navigateUp()

                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CropImageLayout {
                Image(bitmap = currentImage, contentDescription = null)

                ImageCropperView(
                    onCropMarkerChanged = {
                        cropMarkerTopLeft = it.first
                        cropMarkerBottomRight = it.second
                    }
                )
            }

        }
    }
}