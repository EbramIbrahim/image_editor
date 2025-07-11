package com.example.imageeditor.features.brush_erase_screen.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.imageeditor.core.utils.Utils.allColors
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingAction
import com.example.imageeditor.features.brush_erase_screen.presentation.viewmodel.DrawingState
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class
)
@Composable
fun BrushEraseScreen(
    state: DrawingState,
    onAction: (DrawingAction) -> Unit,
    navController: NavController
) {

    LaunchedEffect(state.editedBitmap) {
        onAction(DrawingAction.OnCanvasCleared)
    }
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        "Edit Image",
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // navigate back
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                actions = {
                    Text(
                        "Apply",
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                scope.launch {
                                    val editedBitmap = captureController.captureAsync().await()
                                    try {
                                        onAction(DrawingAction.OnUpdatedBitmap(editedBitmap))
                                        navController.navigateUp()
                                    } catch (e: Exception) {
                                        // handle error
                                        e.printStackTrace()
                                    }
                                }
                            }
                    )
                }
            )
        }
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CombinedCanvas(
                state = state,
                onAction = onAction,
                modifier = Modifier
                    .capturable(captureController)
            )

            CanvasControllerItem(
                state = state,
                allColors = allColors,
                onAction = onAction,
            )

        }
    }

}






