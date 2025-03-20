package com.example.objectdetection.ui.screen

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.objectdetection.MainViewModel
import com.example.objectdetection.R
import com.example.objectdetection.data.PhotoUI
import com.example.objectdetection.ui.component.TopBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(navController: NavHostController, isObjectDetection: Boolean) {
    val context = LocalContext.current
    val viewModel: MainViewModel = hiltViewModel()
    val imageLoader = remember { ImageLoader(context) }

    val photos = navController.previousBackStackEntry?.savedStateHandle?.get<List<PhotoUI>>("photos") ?: listOf()
    val selectedIndex = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("selectedIndex") ?: 0
    val photoName = navController.previousBackStackEntry?.savedStateHandle?.get<String>("photoName") ?: context.getString(R.string.unknown)
    val pageCount = photos.size
    val pagerState = rememberPagerState(initialPage = selectedIndex, pageCount = { pageCount })

    val imageShare by viewModel.imageShare.observeAsState()
    val isImageSaved by viewModel.imageSaved.observeAsState()
    val imageWithBoundingBoxes by viewModel.imageWithBoundingBoxes.observeAsState()

    val bitmaps = remember { mutableStateListOf<Bitmap?>().apply { repeat(photos.size) { add(null) } } }

    LaunchedEffect(photos) {
        photos.forEachIndexed { index, photo ->
            val request = ImageRequest.Builder(context)
                .data(photo.imageUrl).allowHardware(false)
                .build()
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                bitmaps[index] = result.drawable.toBitmap()
            }
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
            isObjectDetection = isObjectDetection,
            onToggleLayout = {},
            onSaveImage = { bitmaps[pagerState.currentPage]?.let { viewModel.saveImageToGallery(context, it, photoName) } },
            onShareImage = { bitmaps[pagerState.currentPage]?.let { viewModel.imageShare(context, it, photoName) } },
            onObjectDetection = { bitmaps[pagerState.currentPage]?.let { viewModel.objectDetection(it) } }
        )

        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                bitmaps[page]?.let { bitmap ->
                    Image(
                        bitmap = (imageWithBoundingBoxes ?: bitmap).asImageBitmap(),
                        contentDescription = photoName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}