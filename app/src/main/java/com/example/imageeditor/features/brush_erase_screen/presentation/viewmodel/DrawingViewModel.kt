package com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DrawingViewModel : ViewModel() {

    private val _drawingState = MutableStateFlow(DrawingState())
    val drawingState = _drawingState.asStateFlow()

    fun onAction(action: DrawingAction) {
        when (action) {
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
            is DrawingAction.OnDrawModeChanged -> onDrawModeChanged(action.mode)
            is DrawingAction.OnBrushThicknessUpdated -> updateBrushThickness(action.thickness)
            DrawingAction.OnCanvasCleared -> onClearCanvasClick()
        }
    }

    private fun updateBrushThickness(thickness: Float) {
        _drawingState.update { it.copy(thickness = thickness) }
    }


    private fun onDrawModeChanged(mode: DrawingMode) {
        _drawingState.update { it.copy(drawingType = mode) }
    }

    private fun onSelectColor(color: Color) {
        _drawingState.update { it.copy(selectedColor = color) }
    }

    private fun onPathEnd() {
        val currentPath = _drawingState.value.currentPath ?: return
        _drawingState.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPath
            )
        }
    }

    private fun onNewPathStart() {
        _drawingState.update {
            it.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis().toString(),
                    color = if (it.drawingType == DrawingMode.ERASER) Color.Transparent else it.selectedColor,
                    paths = emptyList(),
                    thickness = it.thickness
                )
            )
        }
    }

    private fun onDraw(offset: Offset) {
        val currentPath = _drawingState.value.currentPath ?: return
        _drawingState.update {
            it.copy(
                currentPath = currentPath.copy(
                    paths = currentPath.paths + offset
                )
            )
        }
    }

    private fun onClearCanvasClick() {
        _drawingState.update {
            it.copy(
                currentPath = null,
                paths = emptyList()
            )
        }
    }
}