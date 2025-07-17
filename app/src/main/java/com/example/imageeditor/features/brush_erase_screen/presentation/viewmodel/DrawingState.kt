package com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawingState(
    val selectedColor: Color = Color.Black,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList(),
    val drawingType: DrawingMode = DrawingMode.BRUSH,
    val thickness: Float = 20f
)

data class PathData(
    val id: String,
    val color: Color,
    val paths: List<Offset>,
    val thickness: Float
)

enum class DrawingMode {
    BRUSH, ERASER
}
