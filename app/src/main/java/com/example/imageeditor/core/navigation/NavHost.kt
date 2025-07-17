package com.example.imageeditor.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imageeditor.features.main_edit_screen.presentation.ui.MainEditScreen
import com.example.imageeditor.features.brush_erase_screen.presentation.ui.BrushEraseScreen
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingAction
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingState
import com.example.imageeditor.features.crop_screen.presentation.ui.CropImageScreen
import com.example.imageeditor.features.emoji_screen.presentation.ui.EmojiEditScreen
import com.example.imageeditor.features.main_edit_screen.presentation.viewmodel.MainEditViewModel
import com.example.imageeditor.features.rotation_screen.presentation.ui.ImageRotationScreen


@Composable
fun SetupNavHost(
    state: DrawingState,
    onAction:(DrawingAction) -> Unit
) {

    val navController = rememberNavController()

    val viewModel = viewModel<MainEditViewModel>()
    val imageState = viewModel.imageState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.MainEditScreen
    ) {
        composable<Screen.MainEditScreen> {
            MainEditScreen(
                navController = navController,
                onImageSelected = {
                    viewModel.setInitialImage(it)
                },
                imageState = imageState.value,
            )
        }
        composable<Screen.BrushEraseScreen> {
            BrushEraseScreen(
                state = state,
                onAction = onAction,
                navController = navController,
                currentImage = imageState.value!!,
                onImageEdited = {
                    viewModel.updateImage(it)
                }
            )
        }

        composable<Screen.CropImageScreen> {
            CropImageScreen(
                currentImage = imageState.value!!,
                onUpdateCroppedImage = {
                    viewModel.updateImage(it)
                },
                navController = navController
            )
        }

        composable<Screen.ImageRotationScreen> {
            ImageRotationScreen(
                currentImage = imageState.value!!,
                onUpdateCroppedImage = {
                    viewModel.updateImage(it)
                },
                navController = navController
            )
        }

        composable<Screen.EmojiEditScreen> {
            EmojiEditScreen(
                currentImage = imageState.value!!,
                onUpdateImageWithEmoji = {
                    viewModel.updateImage(it)
                },
                navController = navController
            )
        }

    }

}




