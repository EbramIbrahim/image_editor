package com.example.imageeditor.features.emoji_screen.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap

data class EmojiState(
    val currentEmoji: EmojiData? = null,
    val emojis: List<EmojiData> = emptyList()
)


data class EmojiData(
    val id: String,
    val imageBitmap: ImageBitmap,
    var offset: Offset,
    var scale: Float,
    var rotation: Float,
    var isSelected: Boolean = false
)
