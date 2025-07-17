package com.example.imageeditor.features.emoji_screen.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.imageeditor.features.emoji_screen.presentation.viewmodel.EmojiData
import com.example.imageeditor.features.emoji_screen.presentation.viewmodel.EmojiState
import com.example.imageeditor.features.emoji_screen.presentation.viewmodel.EmojiViewModel
import kotlin.math.sqrt

@Composable
fun EmojiControllerCanvasScreen(
    viewModel: EmojiViewModel,
    emojiState: EmojiState,
    editedImage: ImageBitmap,
    modifier: Modifier = Modifier
) {

    val aspectRatio = editedImage.width.toFloat() / editedImage.height.toFloat()
    Canvas(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clipToBounds()
            .pointerInput(emojiState.emojis) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        var emojiTapped = false
                        for (emoji in emojiState.emojis.reversed()) {
                            if (isTapInsideEmojiBound(tapOffset, emoji, size)) {
                                emojiTapped = true
                                viewModel.selectEmoji(emoji)
                                break
                            }
                        }

                        // Tap on empty space - deselect all
                        if (!emojiTapped) {
                            viewModel.unSelectEmojis()
                        }
                    }
                )
            }
            .pointerInput(true) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    viewModel.editEmoji(
                        scale = zoom,
                        rotation = rotation,
                        offset = pan
                    )
                }
            }

    ) {
        drawImage(
            image = editedImage,
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )
        drawEmoji(emojiState.emojis)
    }

}


private fun DrawScope.drawEmoji(emojis: List<EmojiData>) {
    emojis.forEach { sticker ->
        val centerX = size.width / 2f + sticker.offset.x
        val centerY = size.height / 2f + sticker.offset.y

        rotate(
            degrees = sticker.rotation,
            pivot = Offset(centerX, centerY)
        ) {
            scale(
                scaleX = sticker.scale,
                scaleY = sticker.scale,
                pivot = Offset(centerX, centerY)
            ) {
                drawImage(
                    image = sticker.imageBitmap,
                    dstOffset = IntOffset(
                        (centerX - 30f).toInt(),
                        (centerY - 30f).toInt()
                    ),
                    dstSize = IntSize(60, 60)
                )
            }
        }

        // Draw selection indicator
        if (sticker.isSelected) {
            drawCircle(
                color = Color.Cyan,
                radius = 40f * sticker.scale,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
        }
    }
}

private fun isTapInsideEmojiBound(
    tapOffset: Offset,
    emoji: EmojiData,
    canvasSize: IntSize
): Boolean {
    val centerX = canvasSize.width / 2f + emoji.offset.x
    val centerY = canvasSize.height / 2f + emoji.offset.y
    val emojiRadius = 30f * emoji.scale

    val distance = sqrt(
        ((tapOffset.x - centerX) * (tapOffset.x - centerX) +
                (tapOffset.y - centerY) * (tapOffset.y - centerY)).toDouble()
    ).toFloat()

    return distance <= emojiRadius
}