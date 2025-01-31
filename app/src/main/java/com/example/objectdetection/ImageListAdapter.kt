package com.example.objectdetection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.objectdetection.data.Photo
import com.example.objectdetection.databinding.ItemRecyclerBinding

class ImageListAdapter(
    private var imageList: List<Photo>,
    private val onItemClick: (List<Photo>, Int) -> Unit
) : RecyclerView.Adapter<ImageListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = imageList[position]
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            onItemClick(imageList, position)
        }
    }

    fun updateData(newList: List<Photo>) {
        imageList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class Holder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            Glide.with(binding.root.context)
                .load(photo.urls?.small ?: "")
                .into(binding.image)
        }
    }
}