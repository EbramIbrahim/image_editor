package com.example.imageeditor.features.emoji_screen.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EmojiViewModel : ViewModel() {

    private val _emojiState = MutableStateFlow(EmojiState())
    val emojiState = _emojiState.asStateFlow()


    fun addOnEmojiList(emoji: EmojiData) {
        _emojiState.update {
            it.copy(
                emojis = it.emojis + emoji
            )
        }
    }

    fun selectEmoji(emoji: EmojiData) {
        _emojiState.update {
            it.copy(
                currentEmoji = emoji,
                emojis = it.emojis.map {
                    it.copy(
                        isSelected = emoji == it
                    )
                }
            )
        }
    }

    fun unSelectEmojis() {
        _emojiState.update { it.copy(emojis = it.emojis.map { it.copy(isSelected = false) }) }
    }

    fun editEmoji(
        scale: Float,
        rotation: Float,
        offset: Offset
    ) {
        val currentEmoji = _emojiState.value.currentEmoji ?: return
        _emojiState.update {
            it.copy(
                emojis = it.emojis.map { emoji ->
                    if (currentEmoji.id == emoji.id) {
                        emoji.copy(
                            scale = (emoji.scale * scale).coerceIn(0.3f, 4f),
                            rotation = emoji.rotation + (rotation * 0.5f),
                            offset = emoji.offset + offset
                        )
                    } else {
                        emoji
                    }

                }
            )
        }
    }

    fun deleteEmoji(emoji: EmojiData) {
        _emojiState.update {
            it.copy(
                emojis = it.emojis - emoji
            )
        }
    }
}











