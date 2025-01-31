package com.example.objectdetection.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.objectdetection.DetailViewPagerAdapter
import com.example.objectdetection.data.Photo
import com.example.objectdetection.databinding.FragmentDetailViewPagerBinding

class DetailViewPagerFragment : Fragment() {
    companion object {
        const val PHOTO_LIST = "photoList"
        const val START_POSITION = "startPosition"

        fun newInstance(photoList: List<Photo>, startPosition: Int) = DetailViewPagerFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PHOTO_LIST, ArrayList(photoList))
                putInt(START_POSITION, startPosition)
            }
        }
    }

    private var _binding: FragmentDetailViewPagerBinding? = null
    private val binding get() = _binding!!
    private var photoList: List<Photo> = emptyList()
    private var startPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(PHOTO_LIST, ArrayList::class.java) as? ArrayList<Photo> ?: arrayListOf()
            } else {
                @Suppress("DEPRECATION")
                arguments?.getSerializable(PHOTO_LIST) as? ArrayList<Photo> ?: arrayListOf()
            }
            startPosition = it.getInt(START_POSITION, 0)
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
        val adapter = DetailViewPagerAdapter(this, photoList)
        viewPager.adapter = adapter

        viewPager.setCurrentItem(startPosition, false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

