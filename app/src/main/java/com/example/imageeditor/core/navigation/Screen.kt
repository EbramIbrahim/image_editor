package com.example.imageeditor.core.navigation

import kotlinx.serialization.Serializable

interface Screen {

    @Serializable
    data object MainEditScreen: Screen

    @Serializable
    data object BrushEraseScreen: Screen

    @Serializable
    data object CropImageScreen: Screen

    @Serializable
    data object ImageRotationScreen: Screen
}