package com.example.summerholidays.ui.details

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.ui.gallery.GalleryAdapter
import com.example.summerholidays.ui.gallery.GalleryItemViewHolder

class DetailAdapter(private val listener: DetailListener) : ListAdapter<ImageFile, DetailItemViewHolder>(DetailItemDiffUnit()) {

    val TAG = "giangtd"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailItemViewHolder {
        return DetailItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DetailItemViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
        listener.selectPosition()
    }

    interface DetailListener {
        fun onClickedItem(item: ImageFile)
        fun selectPosition()
    }
}

class DetailItemDiffUnit : DiffUtil.ItemCallback<ImageFile>() {
    override fun areItemsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {
        return oldItem == newItem
    }
}