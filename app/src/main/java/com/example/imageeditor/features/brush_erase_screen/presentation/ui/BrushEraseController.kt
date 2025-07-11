package com.example.imageeditor.features.brush_erase_screen.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.example.imageeditor.R
import com.example.imageeditor.features.main_edit_screen.presentation.ui.EditIcon
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingAction
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingMode
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingState

@Composable
fun ColumnScope.CanvasControllerItem(
    allColors: List<Color>,
    onAction: (DrawingAction) -> Unit,
    state: DrawingState,
    modifier: Modifier = Modifier
) {
    if (state.drawingType == DrawingMode.BRUSH) {

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp),
            value = state.thickness,
            onValueChange = {
                onAction(DrawingAction.OnBrushThicknessUpdated(it))
            },
            valueRange = 0f..100f
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            allColors.fastForEach { color ->
                val isSelected = state.selectedColor == color
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val scale = if(isSelected) 1.2f else 1f
                            scaleX = scale
                            scaleY = scale
                        }
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 2.dp,
                            color = if(isSelected) {
                                Color.Black
                            } else {
                                Color.Transparent
                            },
                            shape = CircleShape
                        )
                        .clickable {
                            onAction(DrawingAction.OnSelectColor(color))
                        }
                )
            }
        }
    }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(26.dp, Alignment.CenterHorizontally)
        ) {

            EditIcon(
                icon = R.drawable.paint_brush,
                iconTitle = "",
                onIconPressed = {
                    onAction(DrawingAction.OnDrawModeChanged(DrawingMode.BRUSH))
                }
            )

            EditIcon(
                icon = R.drawable.eraser,
                iconTitle = "",
                onIconPressed = {
                    onAction(DrawingAction.OnDrawModeChanged(DrawingMode.ERASER))
                }
            )
        }
    Spacer(modifier = Modifier.height(40.dp))
}













