package com.example.objectdetection.ui.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.objectdetection.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    isSelected: Boolean,
    isMain: Boolean,
    isObjectDetection: Boolean = false,
    onToggleLayout: () -> Unit,
    onSaveImage: () -> Unit,
    onShareImage: () -> Unit,
    onObjectDetection: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        title = {
            if (isMain) {
                Text(
                    text = stringResource(R.string.app_name),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (isMain) {
                IconButton(onClick = onToggleLayout) {
                    Icon(
                        painter = if (isSelected) {
                            painterResource(id = R.drawable.ic_list_24)
                        } else {
                            painterResource(id = R.drawable.ic_grid_24)
                        },
                        contentDescription = "Toggle Layout"
                    )
                }
            } else {
                IconButton(onClick = onSaveImage) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_down_24),
                        contentDescription = "Image down"
                    )
                }
                IconButton(onClick = onShareImage) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share_24),
                        contentDescription = "Image share"
                    )
                }
                if (isObjectDetection) {
                    IconButton(onClick = onObjectDetection) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_object_detection),
                            contentDescription = "Object detection"
                        )
                    }
                }
            }
        }
    )
}