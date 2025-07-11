package com.example.imageeditor.features.brush_erase_screen.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingAction
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingState


@Composable
fun CombinedCanvas(
    currentImage: ImageBitmap,
    state: DrawingState,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
            val imageAspectRatio = currentImage.width.toFloat() / currentImage.height.toFloat()
            Image(
                currentImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageAspectRatio),
                contentScale = ContentScale.FillHeight
            )
        DrawingBrushCanvas(
            paths = state.paths,
            currentPath = state.currentPath,
            onAction = onAction,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(imageAspectRatio)

        )
    }
}