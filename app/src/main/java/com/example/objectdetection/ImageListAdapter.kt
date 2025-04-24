package com.example.objectdetection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.objectdetection.data.PhotoUI
import com.example.objectdetection.databinding.ItemRecyclerBinding

class ImageListAdapter(
    private val onItemClick: (List<PhotoUI>, Int) -> Unit
) : ListAdapter<PhotoUI, ImageListAdapter.Holder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            onItemClick(currentList, position)
        }
    }

    class Holder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: PhotoUI) {
            Glide.with(binding.root.context)
                .load(photo.imageUrl)
                .into(binding.image)
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<PhotoUI>() {
        override fun areItemsTheSame(oldItem: PhotoUI, newItem: PhotoUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoUI, newItem: PhotoUI): Boolean {
            return oldItem == newItem
        }
    }
}