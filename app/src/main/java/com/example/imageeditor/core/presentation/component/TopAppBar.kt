package com.example.imageeditor.core.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditorTopAppBar(
    title: String,
    actionTitle: String,
    onNavigationIconClicked: () -> Unit,
    onActionIconClicked: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            Text(
                title,
                style = TextStyle(fontSize = 16.sp)
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                // navigate back
                onNavigationIconClicked()
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
            }
        },
        actions = {
            Text(
                actionTitle,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(end = 8.dp)
                    .clickable {
                        onActionIconClicked()
                    }
            )
        }
    )
}