package com.example.objectdetection.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.objectdetection.MainViewModel
import com.example.objectdetection.R
import com.example.objectdetection.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import java.io.File


@AndroidEntryPoint
class DetailFragment : Fragment() {
    companion object {
        private const val PHOTO_URL = "photoUrl"
        private const val PHOTO_NAME = "photoName"
        private const val MODEL_PATH = "object_detection_model.tflite"
        private const val CONFIDENCE_THRESHOLD = 0.5F
        private const val MAX_RESULT = 2

        fun newInstance(photoUrl: String?, photoName: String?) = DetailFragment().apply {
            arguments = Bundle().apply {
                putString(PHOTO_URL, photoUrl)
                putString(PHOTO_NAME, photoName)
            }
        }
    }

    private var photoUrl: String? = null
    private var photoName: String? = null
    private var imgBitmap: Bitmap? = null
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context
    private lateinit var objectDetector: ObjectDetector
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            photoUrl = it.getString(PHOTO_URL) ?: getString(R.string.unknown)
            photoName = it.getString(PHOTO_NAME) ?: getString(R.string.unknown)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater)
        context = binding.root.context

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = ObjectDetectorOptions.builder()
            .setMaxResults(MAX_RESULT)
            .setScoreThreshold(CONFIDENCE_THRESHOLD)
            .build()
        objectDetector = ObjectDetector.createFromFileAndOptions(context, MODEL_PATH, options)

        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .into(
                object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                    ) {
                        imgBitmap = resource
                        binding.ivDetail.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        binding.toolbar.ivShare.setOnClickListener {
            imgBitmap?.let { image ->
                val file = File(context.getExternalFilesDir(null), "$photoName.jpg")

                file.outputStream().use { outputStream ->
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                val imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileProvider",
                    file
                )

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }.setDataAndType(imageUri, "image/jpg")

                startActivity(Intent.createChooser(shareIntent, "$photoName"))
            }
        }

        binding.toolbar.ivDown.setOnClickListener {
            imgBitmap?.let { image ->
                viewModel.saveImageToGallery(context, image, photoName!!)
            }
        }

        binding.toolbar.ivObjectDetection.setOnClickListener {
            imgBitmap?.let { image ->
                val results = objectDetection(image)
                drawBoundingBoxes(image, results)
            }
        }

        viewModel.imageSaved.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                Toast.makeText(context, getString(R.string.toast_image_saved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.toast_fail_image_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun objectDetection(image: Bitmap): List<Detection> {
        val tensorImage = TensorImage.fromBitmap(image)
        return objectDetector.detect(tensorImage)
    }

    private fun drawBoundingBoxes(image: Bitmap, results: List<Detection>) {
        val mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        results.forEach { detection ->
            val boundingBox = detection.boundingBox
            val category = detection.categories.firstOrNull()
            val label = category?.label ?: R.string.unknown
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

            binding.ivDetail.setImageBitmap(mutableBitmap)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}