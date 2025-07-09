package com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

sealed interface DrawingAction {
    data object OnNewPathStart : DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction
    data class OnSelectColor(val color: Color) : DrawingAction
    data class OnDrawModeChanged(val mode: DrawingMode) : DrawingAction
    data class OnUpdatedBitmap(val bitmap: ImageBitmap?) : DrawingAction
    data class OnBrushThicknessUpdated(val thickness: Float) : DrawingAction
    data object OnCanvasCleared: DrawingAction

}