package com.example.imageeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.imageeditor.core.navigation.SetupNavHost
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingViewModel
import com.example.imageeditor.ui.theme.ImageEditorTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel by viewModels<DrawingViewModel>()
            val state = viewModel.drawingState.collectAsStateWithLifecycle()
            ImageEditorTheme {
                SetupNavHost(
                    state = state.value,
                    onAction = viewModel::onAction
                )
            }
        }
    }
}
