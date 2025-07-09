package com.example.imageeditor.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imageeditor.features.MainEditScreen
import com.example.imageeditor.features.brush_erase_screen.presentation.ui.BrushEraseScreen
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingAction
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingState


@Composable
fun SetupNavHost(
    state: DrawingState,
    onAction:(DrawingAction) -> Unit
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MainEditScreen
    ) {
        composable<Screen.MainEditScreen> {
            MainEditScreen(
                image = state.editedBitmap,
                navController = navController,
                onAction = onAction
            )
        }
        composable<Screen.BrushEraseScreen> {
            BrushEraseScreen(
                state = state,
                onAction = onAction,
                navController = navController
            )
        }

    }

}









