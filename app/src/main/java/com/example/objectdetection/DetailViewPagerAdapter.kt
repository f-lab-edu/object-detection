package com.example.objectdetection

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.objectdetection.data.PhotoUI
import com.example.objectdetection.fragment.DetailFragment

class DetailViewPagerAdapter(
    fragment: Fragment,
    private val photoList: List<PhotoUI>,
    private val isObjectDetection: Boolean
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = photoList.size

    override fun createFragment(position: Int): Fragment {
        return DetailFragment.newInstance(photoList[position].imageUrl, photoList[position].photoName, isObjectDetection)
    }
}
