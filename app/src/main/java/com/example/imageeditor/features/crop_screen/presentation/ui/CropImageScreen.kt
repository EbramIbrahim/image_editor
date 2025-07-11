package com.example.imageeditor.features.crop_screen.presentation.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.imageeditor.core.presentation.component.ImageEditorTopAppBar
import com.example.imageeditor.features.crop_screen.presentation.component.CropImageComponent
import com.example.imageeditor.features.crop_screen.presentation.component.CropImageLayout
import com.example.imageeditor.features.crop_screen.presentation.component.ImageCropperView

@Composable
fun CropImageScreen(
    editedImageState: ImageBitmap?,
    inputImageState: ImageBitmap?,
    onCropImage: (Rect, ImageBitmap) -> Unit,
    onUpdateCroppedImage:(ImageBitmap) -> Unit,
    navController: NavController
) {

    var cropMarkerTopLeft by remember { mutableStateOf(Offset.Unspecified) }
    var cropMarkerBottomRight by remember { mutableStateOf(Offset.Unspecified) }



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
                    onCropImage(cropRect, inputImageState!!)

                    if (editedImageState != null) {
                        Log.d("Cropped Image ......", editedImageState.width.toString())
                        onUpdateCroppedImage(editedImageState)
                        navController.navigateUp()
                    }
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
            inputImageState?.let {
                CropImageLayout {
                    Image(bitmap = it, contentDescription = null)

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
}