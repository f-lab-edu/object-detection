package com.example.objectdetection

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.objectdetection.data.PhotoUI
import com.example.objectdetection.data.toUIModel
import com.example.objectdetection.repository.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val unsplashRepository: UnsplashRepository
) : ViewModel() {
    private val _photos = MutableLiveData<List<PhotoUI>?>()
    val photos: LiveData<List<PhotoUI>?> = _photos

    private val _imageSaved = MutableLiveData<Boolean>()
    val imageSaved: LiveData<Boolean> get() = _imageSaved

    private val _apiError = MutableLiveData<String?>()
    val apiError: LiveData<String?> = _apiError

    fun searchPhotos(query: String) {
        viewModelScope.launch {
            try {
                val result = unsplashRepository.searchPhotos(query)
                _photos.value = result.map { it.toUIModel(query) }
            } catch (exception: Exception) {
                _apiError.value = exception.message
            }
        }
    }

    fun saveImageToGallery(context: Context, bitmap: Bitmap, photoName: String) {
        viewModelScope.launch {
            val fileName = "${photoName}_${System.currentTimeMillis()}.jpg"
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveImageForAndroidOver10(context, bitmap, fileName)
                } else {
                    saveImageForAndroidUnder10(context, bitmap, fileName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ObjectDetection", "image save error ${e.message}")
                _imageSaved.value = false
            }
        }
    }

    private fun saveImageForAndroidOver10(context: Context, bitmap: Bitmap, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            _imageSaved.value = true
        } ?: {
            _imageSaved.value = false
        }
    }

    private fun saveImageForAndroidUnder10(context: Context, bitmap: Bitmap, fileName: String) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(directory, fileName)

        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/jpeg"), null)

        _imageSaved.value = true
    }
}