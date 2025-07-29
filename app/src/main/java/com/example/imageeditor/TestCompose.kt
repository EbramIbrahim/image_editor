package com.example.imageeditor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

data class AspectRatio(val name: String, val ratio: Float)

@Composable
fun ImageCropScreen(
    imageBitmap: ImageBitmap,
    onCropComplete: (Rect) -> Unit
) {
    var selectedRatio by remember { mutableStateOf(AspectRatio("Free", 0f)) }
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    var imageScale by remember { mutableFloatStateOf(1f) }
    var imageOffset by remember { mutableStateOf(Offset.Zero) }
    var showGrid by remember { mutableStateOf(true) }

    val aspectRatios = listOf(
        AspectRatio("Free", 0f),
        AspectRatio("1:1", 1f),
        AspectRatio("9:16", 9f/16f),
        AspectRatio("16:9", 16f/9f),
        AspectRatio("4:3", 4f/3f),
        AspectRatio("3:4", 3f/4f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { /* Cancel action */ }) {
                Text("Cancel", color = Color.White)
            }

            Switch(
                checked = showGrid,
                onCheckedChange = { showGrid = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Gray
                )
            )

            TextButton(onClick = { onCropComplete(cropRect) }) {
                Text("Done", color = Color.White)
            }
        }

        // Crop canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            ImageCropCanvas(
                imageBitmap = imageBitmap,
                selectedRatio = selectedRatio,
                showGrid = showGrid,
                onCropRectChange = { cropRect = it },
                imageScale = imageScale,
                imageOffset = imageOffset,
                onImageTransform = { scale, offset ->
                    imageScale = scale
                    imageOffset = offset
                }
            )
        }

        // Aspect ratio selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(aspectRatios) { ratio ->
                AspectRatioChip(
                    aspectRatio = ratio,
                    isSelected = selectedRatio == ratio,
                    onClick = { selectedRatio = ratio }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ImageCropCanvas(
    imageBitmap: ImageBitmap,
    selectedRatio: AspectRatio,
    showGrid: Boolean,
    onCropRectChange: (Rect) -> Unit,
    imageScale: Float,
    imageOffset: Offset,
    onImageTransform: (Float, Offset) -> Unit
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var dragHandle by remember { mutableStateOf(DragHandle.NONE) }

    val minCropSize = 100f
    val handleSize = 20f

    LaunchedEffect(canvasSize, selectedRatio) {
        if (canvasSize != Size.Zero) {
            cropRect = calculateInitialCropRect(canvasSize, selectedRatio.ratio)
            onCropRectChange(cropRect)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (!isDragging) {
                        val newScale = (imageScale * zoom).coerceIn(0.5f, 3f)
                        val newOffset = imageOffset + pan
                        onImageTransform(newScale, newOffset)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragHandle = getDragHandle(offset, cropRect, handleSize)
                        isDragging = dragHandle != DragHandle.NONE
                    },
                    onDragEnd = {
                        isDragging = false
                        dragHandle = DragHandle.NONE
                    }
                ) { _, dragAmount ->
                    if (isDragging) {
                        cropRect = updateCropRect(
                            cropRect,
                            dragAmount,
                            dragHandle,
                            canvasSize,
                            selectedRatio.ratio,
                            minCropSize
                        )
                        onCropRectChange(cropRect)
                    }
                }
            }
    ) {
        canvasSize = size

        // Draw image
        drawImage(imageBitmap, imageScale, imageOffset, size)

        // Draw crop overlay
        drawCropOverlay(cropRect, size)

        // Draw grid
        if (showGrid) {
            drawGrid(cropRect)
        }

        // Draw crop handles
        drawCropHandles(cropRect, handleSize)
    }
}

@Composable
fun AspectRatioChip(
    aspectRatio: AspectRatio,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(aspectRatio.name) },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color.White,
            selectedLabelColor = Color.Black,
            containerColor = Color.Transparent,
            labelColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = Color.White,
            selectedBorderColor = Color.White,
            enabled = true,
            selected = false
        )
    )
}

enum class DragHandle {
    NONE, MOVE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
    TOP, BOTTOM, LEFT, RIGHT
}

fun calculateInitialCropRect(canvasSize: Size, aspectRatio: Float): Rect {
    val centerX = canvasSize.width / 2f
    val centerY = canvasSize.height / 2f

    val size = minOf(canvasSize.width, canvasSize.height) * 0.8f

    return if (aspectRatio <= 0f) {
        // Free aspect ratio - square by default
        val halfSize = size / 2f
        Rect(
            left = centerX - halfSize,
            top = centerY - halfSize,
            right = centerX + halfSize,
            bottom = centerY + halfSize
        )
    } else {
        // Fixed aspect ratio
        val width: Float
        val height: Float

        if (aspectRatio > 1f) {
            // Landscape
            width = size
            height = size / aspectRatio
        } else {
            // Portrait
            height = size
            width = size * aspectRatio
        }

        Rect(
            left = centerX - width / 2f,
            top = centerY - height / 2f,
            right = centerX + width / 2f,
            bottom = centerY + height / 2f
        )
    }
}

fun getDragHandle(touchPoint: Offset, cropRect: Rect, handleSize: Float): DragHandle {
    val handles = listOf(
        DragHandle.TOP_LEFT to Offset(cropRect.left, cropRect.top),
        DragHandle.TOP_RIGHT to Offset(cropRect.right, cropRect.top),
        DragHandle.BOTTOM_LEFT to Offset(cropRect.left, cropRect.bottom),
        DragHandle.BOTTOM_RIGHT to Offset(cropRect.right, cropRect.bottom),
        DragHandle.TOP to Offset(cropRect.center.x, cropRect.top),
        DragHandle.BOTTOM to Offset(cropRect.center.x, cropRect.bottom),
        DragHandle.LEFT to Offset(cropRect.left, cropRect.center.y),
        DragHandle.RIGHT to Offset(cropRect.right, cropRect.center.y)
    )

    for ((handle, position) in handles) {
        if ((touchPoint - position).getDistance() <= handleSize) {
            return handle
        }
    }

    return if (cropRect.contains(touchPoint)) DragHandle.MOVE else DragHandle.NONE
}

fun updateCropRect(
    currentRect: Rect,
    dragAmount: Offset,
    handle: DragHandle,
    canvasSize: Size,
    aspectRatio: Float,
    minSize: Float
): Rect {
    var newRect = currentRect

    when (handle) {
        DragHandle.MOVE -> {
            newRect = currentRect.translate(dragAmount)
            // Keep within bounds
            newRect = Rect(
                left = newRect.left.coerceIn(0f, canvasSize.width - newRect.width),
                top = newRect.top.coerceIn(0f, canvasSize.height - newRect.height),
                right = newRect.left + newRect.width,
                bottom = newRect.top + newRect.height
            )
        }

        DragHandle.TOP_LEFT -> {
            newRect = Rect(
                left = (currentRect.left + dragAmount.x).coerceAtMost(currentRect.right - minSize),
                top = (currentRect.top + dragAmount.y).coerceAtMost(currentRect.bottom - minSize),
                right = currentRect.right,
                bottom = currentRect.bottom
            )
        }

        DragHandle.TOP_RIGHT -> {
            newRect = Rect(
                left = currentRect.left,
                top = (currentRect.top + dragAmount.y).coerceAtMost(currentRect.bottom - minSize),
                right = (currentRect.right + dragAmount.x).coerceAtLeast(currentRect.left + minSize),
                bottom = currentRect.bottom
            )
        }

        DragHandle.BOTTOM_LEFT -> {
            newRect = Rect(
                left = (currentRect.left + dragAmount.x).coerceAtMost(currentRect.right - minSize),
                top = currentRect.top,
                right = currentRect.right,
                bottom = (currentRect.bottom + dragAmount.y).coerceAtLeast(currentRect.top + minSize)
            )
        }

        DragHandle.BOTTOM_RIGHT -> {
            newRect = Rect(
                left = currentRect.left,
                top = currentRect.top,
                right = (currentRect.right + dragAmount.x).coerceAtLeast(currentRect.left + minSize),
                bottom = (currentRect.bottom + dragAmount.y).coerceAtLeast(currentRect.top + minSize)
            )
        }

        DragHandle.TOP -> {
            newRect = Rect(
                left = currentRect.left,
                top = (currentRect.top + dragAmount.y).coerceAtMost(currentRect.bottom - minSize),
                right = currentRect.right,
                bottom = currentRect.bottom
            )
        }

        DragHandle.BOTTOM -> {
            newRect = Rect(
                left = currentRect.left,
                top = currentRect.top,
                right = currentRect.right,
                bottom = (currentRect.bottom + dragAmount.y).coerceAtLeast(currentRect.top + minSize)
            )
        }

        DragHandle.LEFT -> {
            newRect = Rect(
                left = (currentRect.left + dragAmount.x).coerceAtMost(currentRect.right - minSize),
                top = currentRect.top,
                right = currentRect.right,
                bottom = currentRect.bottom
            )
        }

        DragHandle.RIGHT -> {
            newRect = Rect(
                left = currentRect.left,
                top = currentRect.top,
                right = (currentRect.right + dragAmount.x).coerceAtLeast(currentRect.left + minSize),
                bottom = currentRect.bottom
            )
        }

        else -> return currentRect
    }

    // Apply aspect ratio constraint if needed
    if (aspectRatio > 0f) {
        newRect = applyAspectRatio(newRect, aspectRatio, handle)
    }

    // Keep within canvas bounds
    newRect = constrainToCanvas(newRect, canvasSize)

    return newRect
}

fun applyAspectRatio(rect: Rect, aspectRatio: Float, handle: DragHandle): Rect {
    when (handle) {
        DragHandle.TOP_LEFT, DragHandle.TOP_RIGHT,
        DragHandle.BOTTOM_LEFT, DragHandle.BOTTOM_RIGHT -> {
            val currentRatio = rect.width / rect.height
            if (abs(currentRatio - aspectRatio) > 0.01f) {
                val centerX = rect.center.x
                val centerY = rect.center.y

                val newWidth: Float
                val newHeight: Float

                if (currentRatio > aspectRatio) {
                    newHeight = rect.height
                    newWidth = newHeight * aspectRatio
                } else {
                    newWidth = rect.width
                    newHeight = newWidth / aspectRatio
                }

                return Rect(
                    left = centerX - newWidth / 2f,
                    top = centerY - newHeight / 2f,
                    right = centerX + newWidth / 2f,
                    bottom = centerY + newHeight / 2f
                )
            }
        }
        else -> {
            // For edge handles, maintain aspect ratio
            if (handle == DragHandle.TOP || handle == DragHandle.BOTTOM) {
                val newWidth = rect.height * aspectRatio
                val centerX = rect.center.x
                return Rect(
                    left = centerX - newWidth / 2f,
                    top = rect.top,
                    right = centerX + newWidth / 2f,
                    bottom = rect.bottom
                )
            } else if (handle == DragHandle.LEFT || handle == DragHandle.RIGHT) {
                val newHeight = rect.width / aspectRatio
                val centerY = rect.center.y
                return Rect(
                    left = rect.left,
                    top = centerY - newHeight / 2f,
                    right = rect.right,
                    bottom = centerY + newHeight / 2f
                )
            }
        }
    }
    return rect
}

fun constrainToCanvas(rect: Rect, canvasSize: Size): Rect {
    val constrainedLeft = rect.left.coerceIn(0f, canvasSize.width - rect.width)
    val constrainedTop = rect.top.coerceIn(0f, canvasSize.height - rect.height)

    return Rect(
        left = constrainedLeft,
        top = constrainedTop,
        right = constrainedLeft + rect.width,
        bottom = constrainedTop + rect.height
    )
}

fun DrawScope.drawImage(
    imageBitmap: ImageBitmap,
    scale: Float,
    offset: Offset,
    canvasSize: Size
) {
    val imageAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
    val canvasAspectRatio = canvasSize.width / canvasSize.height

    val scaleFactor = if (imageAspectRatio > canvasAspectRatio) {
        canvasSize.width / imageBitmap.width
    } else {
        canvasSize.height / imageBitmap.height
    }

    val scaledWidth = imageBitmap.width * scaleFactor * scale
    val scaledHeight = imageBitmap.height * scaleFactor * scale

    val centerX = canvasSize.width / 2f
    val centerY = canvasSize.height / 2f

    val imageOffset = Offset(
        centerX - scaledWidth / 2f + offset.x,
        centerY - scaledHeight / 2f + offset.y
    )

    drawImage(
        image = imageBitmap,
        dstOffset = IntOffset(imageOffset.x.toInt(), imageOffset.y.toInt()),
        dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt())
    )
}

fun DrawScope.drawCropOverlay(cropRect: Rect, canvasSize: Size) {
    // Draw semi-transparent overlay outside crop area
    val overlayColor = Color.Black.copy(alpha = 0.5f)

    // Top
    drawRect(
        color = overlayColor,
        topLeft = Offset.Zero,
        size = Size(canvasSize.width, cropRect.top)
    )

    // Bottom
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.bottom),
        size = Size(canvasSize.width, canvasSize.height - cropRect.bottom)
    )

    // Left
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.top),
        size = Size(cropRect.left, cropRect.height)
    )

    // Right
    drawRect(
        color = overlayColor,
        topLeft = Offset(cropRect.right, cropRect.top),
        size = Size(canvasSize.width - cropRect.right, cropRect.height)
    )

    // Draw crop rectangle border
    drawRect(
        color = Color.White,
        topLeft = cropRect.topLeft,
        size = cropRect.size,
        style = Stroke(width = 2.dp.toPx())
    )
}

fun DrawScope.drawGrid(cropRect: Rect) {
    val gridColor = Color.White.copy(alpha = 0.7f)
    val strokeWidth = 1.dp.toPx()

    // Rule of thirds - vertical lines
    val verticalStep = cropRect.width / 3f
    for (i in 1..2) {
        val x = cropRect.left + verticalStep * i
        drawLine(
            color = gridColor,
            start = Offset(x, cropRect.top),
            end = Offset(x, cropRect.bottom),
            strokeWidth = strokeWidth
        )
    }

    // Rule of thirds - horizontal lines
    val horizontalStep = cropRect.height / 3f
    for (i in 1..2) {
        val y = cropRect.top + horizontalStep * i
        drawLine(
            color = gridColor,
            start = Offset(cropRect.left, y),
            end = Offset(cropRect.right, y),
            strokeWidth = strokeWidth
        )
    }
}

fun DrawScope.drawCropHandles(cropRect: Rect, handleSize: Float) {
    val handleColor = Color.White
    val handleRadius = handleSize / 2f

    val handles = listOf(
        Offset(cropRect.left, cropRect.top),           // Top-left
        Offset(cropRect.right, cropRect.top),          // Top-right
        Offset(cropRect.left, cropRect.bottom),        // Bottom-left
        Offset(cropRect.right, cropRect.bottom),       // Bottom-right
        Offset(cropRect.center.x, cropRect.top),       // Top-center
        Offset(cropRect.center.x, cropRect.bottom),    // Bottom-center
        Offset(cropRect.left, cropRect.center.y),      // Left-center
        Offset(cropRect.right, cropRect.center.y)      // Right-center
    )

    handles.forEach { position ->
        drawCircle(
            color = handleColor,
            radius = handleRadius,
            center = position
        )
        drawCircle(
            color = Color.Black,
            radius = handleRadius,
            center = position,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun YourScreen() {
    val imageBitmap = ImageBitmap.imageResource(R.drawable.emoji)

    ImageCropScreen(
        imageBitmap = imageBitmap,
        onCropComplete = { cropRect ->
            // Handle the crop rectangle
            // cropRect contains the final crop area coordinates
            println("Crop area: $cropRect")
        }
    )
}