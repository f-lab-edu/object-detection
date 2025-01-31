package com.example.objectdetection.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import java.io.File


class DetailFragment : Fragment() {
    companion object {
        private const val PHOTO_URL = "photoUrl"
        private const val PHOTO_NAME = "photoName"

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

        viewModel.imageSaved.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                Toast.makeText(context, getString(R.string.toast_image_saved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.toast_fail_image_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}