package com.example.objectdetection.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.objectdetection.DetailViewPagerAdapter
import com.example.objectdetection.activity.LauncherActivity.Companion.IS_OBJECT_DETECTION
import com.example.objectdetection.data.PhotoUI
import com.example.objectdetection.databinding.FragmentDetailViewPagerBinding

class DetailViewPagerFragment : Fragment() {
    companion object {
        const val PHOTO_LIST = "photoList"
        const val START_POSITION = "startPosition"

        fun newInstance(photoList: List<PhotoUI>, startPosition: Int, isObjectDetection: Boolean) = DetailViewPagerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PHOTO_LIST, ArrayList(photoList))
                putInt(START_POSITION, startPosition)
                putBoolean(IS_OBJECT_DETECTION, isObjectDetection)
            }
        }
    }

    private var _binding: FragmentDetailViewPagerBinding? = null
    private val binding get() = _binding!!
    private var photoList: List<PhotoUI> = emptyList()
    private var startPosition: Int = 0
    private var isObjectDetection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(PHOTO_LIST, ArrayList::class.java) as? ArrayList<PhotoUI> ?: arrayListOf()
            } else {
                @Suppress("DEPRECATION")
                arguments?.getSerializable(PHOTO_LIST) as? ArrayList<PhotoUI> ?: arrayListOf()
            }
            startPosition = it.getInt(START_POSITION, 0)
            isObjectDetection = it.getBoolean(IS_OBJECT_DETECTION, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager
        val adapter = DetailViewPagerAdapter(this, photoList, isObjectDetection)
        viewPager.adapter = adapter

        viewPager.setCurrentItem(startPosition, false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

