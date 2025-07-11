package com.example.imageeditor.features.main_edit_screen.presentation.ui

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.imageeditor.R
import com.example.imageeditor.core.navigation.Screen
import com.example.imageeditor.core.utils.Utils
import java.io.FileDescriptor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainEditScreen(
    onImageSelected: (ImageBitmap) -> Unit,
    imageState: ImageBitmap?,
    navController: NavController
) {

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
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                    }
                },
                actions = {
                    Text(
                        "Done",
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                // navigate back with edited image
                            }
                    )
                }
            )
        }
    ) { padding ->

        val context = LocalContext.current


        val pickVisualMediaLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { contentUri ->
            contentUri?.let { uri ->
                context.contentResolver.openFileDescriptor(uri, "r").use {
                    it?.let { parcelFileDescriptor ->
                        val fd: FileDescriptor = parcelFileDescriptor.fileDescriptor
                        val bitmap: Bitmap = BitmapFactory.decodeFileDescriptor(fd)
                        onImageSelected(bitmap.asImageBitmap())
                    }
                }
            }
        }

        val permissionRequestLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                pickVisualMediaLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                if (imageState != null) {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = imageState,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        EditIcon(
                            icon = R.drawable.paint,
                            iconTitle = "Brush",
                            onIconPressed = {
                                navController.navigate(Screen.BrushEraseScreen)
                            }
                        )
                        EditIcon(
                            icon = R.drawable.crop,
                            iconTitle = "Crop",
                            onIconPressed = {
                                navController.navigate(Screen.CropImageScreen)
                            }
                        )
                        EditIcon(
                            icon = R.drawable.rotate,
                            iconTitle = "Rotate",
                            onIconPressed = {
                                // update rotation state with image parameter
                            }
                        )
                    }


                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        if (Utils.readPermissionsGranted(context)) {
                            pickVisualMediaLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionRequestLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                permissionRequestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    }
                ) {
                    Text(text = "Pick Image")
                }
            }


        }
    }
}

@Composable
fun EditIcon(
    icon: Int,
    iconTitle: String,
    onIconPressed: () -> Unit
) {

    Column(
        modifier = Modifier.clickable {
            onIconPressed()
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(34.dp)
        )

        Text(iconTitle, style = TextStyle(fontSize = 16.sp))
    }

}





