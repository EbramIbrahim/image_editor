package com.example.imageeditor.features.brush_erase_screen.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingAction
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingState


@Composable
fun CombinedCanvas(
    state: DrawingState,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier
) {

    Log.d("edited Image", state.editedBitmap.toString())

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        state.editedBitmap?.let {
            val imageAspectRatio = it.width.toFloat() / it.height.toFloat()

            Image(
                it,
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
}