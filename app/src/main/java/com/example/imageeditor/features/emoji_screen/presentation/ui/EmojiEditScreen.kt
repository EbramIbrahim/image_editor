package com.example.imageeditor.features.emoji_screen.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.imageeditor.core.utils.Utils
import com.example.imageeditor.features.emoji_screen.presentation.viewmodel.EmojiData
import com.example.imageeditor.features.emoji_screen.presentation.viewmodel.EmojiViewModel
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun EmojiEditScreen(
    currentImage: ImageBitmap,
    onUpdateImageWithEmoji: (ImageBitmap) -> Unit,
    navController: NavController
) {

    val emojiViewModel = viewModel<EmojiViewModel>()
    val emojiState = emojiViewModel.emojiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()

    val sheetState = rememberModalBottomSheetState()

    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

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
                    Icon(
                        imageVector = Icons.Outlined.Face,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                            .clickable {
                                isSheetOpen = true
                            }
                    )

                    Text(
                        "Apply",
                        style = TextStyle(fontSize = 16.sp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                emojiViewModel.unSelectEmojis()
                                scope.launch {
                                    val editedBitmap = captureController.captureAsync().await()
                                    try {
                                        onUpdateImageWithEmoji(editedBitmap)
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
            EmojiControllerCanvasScreen(
                viewModel = emojiViewModel,
                emojiState = emojiState.value,
                modifier = Modifier
                    .capturable(captureController)
                    .fillMaxWidth()
                    .weight(1f),
                editedImage = currentImage
            )
        }

        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    isSheetOpen = false
                }
            ) {
                EmojiPicker(
                    modifier = Modifier.fillMaxSize(),
                    onEmojiPicked = { emoji ->
                        val newEmoji = EmojiData(
                            id = "emoji_${System.currentTimeMillis()}",
                            imageBitmap = Utils.emojiToBitmap(emoji, 250),
                            offset = Offset.Zero,
                            scale = 1f,
                            rotation = 0f,
                            isSelected = false
                        )
                        emojiViewModel.addOnEmojiList(newEmoji)
                        isSheetOpen = false
                    }
                )
            }
        }
    }
}


@Composable
fun EmojiPicker(
    modifier: Modifier = Modifier,
    onEmojiPicked: (String) -> Unit
) {

    Column(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Transparent),
            factory = {
                EmojiPickerView(it)
                    .apply {
                        emojiGridColumns = 6
                        emojiGridRows = 6f

                        setOnEmojiPickedListener { item ->
                            onEmojiPicked(item.emoji)
                        }
                    }
            }
        )
    }
}