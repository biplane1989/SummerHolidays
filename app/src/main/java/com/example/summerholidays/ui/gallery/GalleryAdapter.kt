package com.example.summerholidays.ui.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.ui.home.HistoryItemDiffUnit

class GalleryAdapter(private val listener: GalleryListener) : ListAdapter<ImageFile, GalleryItemViewHolder>(GalleryItemDiffUnit()) {

    val TAG = "giangtd"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        return GalleryItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface GalleryListener {
        fun onClickedItem(item: ImageFile, position: Int)
    }
}

class GalleryItemDiffUnit : DiffUtil.ItemCallback<ImageFile>() {
    override fun areItemsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {
        return oldItem == newItem
    }
}