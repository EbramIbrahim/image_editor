package com.example.imageeditor.features.rotation_screen.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.imageeditor.R
import com.example.imageeditor.core.presentation.component.ImageEditorTopAppBar
import com.example.imageeditor.features.main_edit_screen.presentation.ui.EditIcon
import com.example.imageeditor.features.rotation_screen.presentation.viewmodel.RotationViewModel

@Composable
fun ImageRotationScreen(
    currentImage: ImageBitmap,
    onUpdateCroppedImage: (ImageBitmap) -> Unit,
    navController: NavController

) {

    val viewModel = viewModel<RotationViewModel>()
    val rotationDegreeState = viewModel.rotationDegree.collectAsStateWithLifecycle()
    val rotationImageState = viewModel.rotationImage.collectAsStateWithLifecycle()

    LaunchedEffect(currentImage) {
        viewModel.updateRotationImage(currentImage)
    }

    Scaffold(
        topBar = {
            ImageEditorTopAppBar(
                title = "Edit Image",
                actionTitle = "Apply",
                onNavigationIconClicked = {
                    navController.navigateUp()
                },
                onActionIconClicked = {
                    onUpdateCroppedImage(rotationImageState.value!!)
                    navController.navigateUp()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rotationImageState.value?.let { imageBitmap ->

                Image(bitmap = imageBitmap, contentDescription = null)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    EditIcon(
                        icon = R.drawable.rotate_left,
                        iconTitle = "",
                        onIconPressed = {
                            viewModel.updateRotationDegree((rotationDegreeState.value - 90f) % 360f)
                            val rotatedBitmap = viewModel.rotateImage(
                                currentImage.asAndroidBitmap(),
                                rotationDegreeState.value
                            )
                            viewModel.updateRotationImage(rotatedBitmap.asImageBitmap())
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    EditIcon(
                        icon = R.drawable.rotate_right,
                        iconTitle = "",
                        onIconPressed = {
                            viewModel.updateRotationDegree((rotationDegreeState.value + 90f) % 360f)
                            val rotatedBitmap = viewModel.rotateImage(
                                currentImage.asAndroidBitmap(),
                                rotationDegreeState.value
                            )
                            viewModel.updateRotationImage(rotatedBitmap.asImageBitmap())
                        }
                    )
                }
            }
        }
    }


}