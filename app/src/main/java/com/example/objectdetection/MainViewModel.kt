package com.example.objectdetection

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.objectdetection.data.PhotoUI
import com.example.objectdetection.data.toUIModel
import com.example.objectdetection.repository.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val unsplashRepository: UnsplashRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    companion object {
        private const val MODEL_PATH = "object_detection_model.tflite"
        private const val CONFIDENCE_THRESHOLD = 0.5F
        private const val MAX_RESULT = 2
    }

    private val _photos = MutableLiveData<List<PhotoUI>?>()
    val photos: LiveData<List<PhotoUI>?> = _photos

    private val _imageSaved = MutableLiveData<Boolean>()
    val imageSaved: LiveData<Boolean> get() = _imageSaved

    private val _imageShare = MutableLiveData<Uri>()
    val imageShare: LiveData<Uri> get() = _imageShare

    private val _apiError = MutableLiveData<String?>()
    val apiError: LiveData<String?> = _apiError

    private val _imageWithBoundingBoxes = MutableLiveData<Bitmap>()
    val imageWithBoundingBoxes: LiveData<Bitmap> get() = _imageWithBoundingBoxes

    private val objectDetector: ObjectDetector

    init {
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(MAX_RESULT)
            .setScoreThreshold(CONFIDENCE_THRESHOLD)
            .build()

        objectDetector = ObjectDetector.createFromFileAndOptions(context, MODEL_PATH, options)
    }

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

    fun imageShare(context: Context, image: Bitmap, photoName: String) {
        val fileName = "${photoName}_${System.currentTimeMillis()}.jpg"
        val file = File(context.getExternalFilesDir(null), fileName)

        file.outputStream().use { outputStream ->
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        val imageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileProvider",
            file
        )

        _imageShare.postValue(imageUri)
    }

    fun objectDetection(image: Bitmap) {
        val tensorImage = TensorImage.fromBitmap(image)
        val results = objectDetector.detect(tensorImage)
        drawBoundingBoxes(image, results)
    }

    private fun drawBoundingBoxes(image: Bitmap, results: List<Detection>) {
        val mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        results.forEach { detection ->
            val boundingBox = detection.boundingBox
            val category = detection.categories.firstOrNull()
            val label = category?.label ?: "Unknown"
            val score = category?.score ?: 0.0f
            val index = category?.index ?: -1
            val color = getColorForCategory(index)

            val paint = Paint().apply {
                this.color = color
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }

            val textPaint = Paint().apply {
                this.color = color
                textSize = 20f
                style = Paint.Style.FILL
            }

            canvas.drawRect(boundingBox, paint)
            canvas.drawText("$label: ${(score * 100).toInt()}%", boundingBox.left, boundingBox.top - 10, textPaint)
        }

        _imageWithBoundingBoxes.postValue(mutableBitmap)
    }

    private fun getColorForCategory(index: Int): Int {
        return when (index) {
            0 -> Color.RED
            1 -> Color.BLUE
            2 -> Color.GREEN
            3 -> Color.YELLOW
            else -> Color.BLACK
        }
    }
}