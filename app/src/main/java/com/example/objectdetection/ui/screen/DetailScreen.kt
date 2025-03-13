package com.example.objectdetection.ui.screen

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.objectdetection.MainViewModel
import com.example.objectdetection.R
import com.example.objectdetection.ui.component.TopBar

@Composable
fun DetailScreen(photoUrl: String, photoName: String) {
    val context = LocalContext.current
    val viewModel: MainViewModel = hiltViewModel()
    val imageLoader = remember { ImageLoader(context) }

    val imageShare by viewModel.imageShare.observeAsState()
    val isImageSaved by viewModel.imageSaved.observeAsState()
    val imageWithBoundingBoxes by viewModel.imageWithBoundingBoxes.observeAsState()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val imageRequest = remember(photoUrl) {
        ImageRequest.Builder(context)
            .data(photoUrl)
            .allowHardware(false)
            .build()
    }

    LaunchedEffect(imageRequest) {
        val result = imageLoader.execute(imageRequest)
        if (result is SuccessResult) {
            bitmap = result.drawable.toBitmap()
        }
    }

    LaunchedEffect(imageShare) {
        imageShare?.let { uri ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, "image/jpg")
            }
            context.startActivity(Intent.createChooser(shareIntent, photoName))
        }
    }

    LaunchedEffect(isImageSaved) {
        isImageSaved?.let {
            val message = if (it) {
                context.getString(R.string.toast_image_saved)
            } else {
                context.getString(R.string.toast_fail_image_saved)
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        TopBar(
            isSelected = false,
            isMain = false,
            onToggleLayout = {},
            onSaveImage = { bitmap?.let { viewModel.saveImageToGallery(context, it, photoName) } },
            onShareImage = { bitmap?.let { viewModel.imageShare(context, it, photoName) } },
            onObjectDetection = { bitmap?.let { viewModel.objectDetection(it) } }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = (imageWithBoundingBoxes ?: bitmap)?.asImageBitmap() ?: return@Box,
                contentDescription = photoName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}