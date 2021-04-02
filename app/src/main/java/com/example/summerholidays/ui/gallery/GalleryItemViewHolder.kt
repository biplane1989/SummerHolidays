package com.example.summerholidays.ui.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.rotrofitwithcoroutineexampletow.data.model.LoadingStatus
import com.example.summerholidays.databinding.GalleryItemBinding
import com.example.summerholidays.databinding.HomeItemBinding
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.ui.home.HomeAdapter
import com.example.summerholidays.ui.home.HomeItemViewHolder

class GalleryItemViewHolder(val binding: GalleryItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val TAG = "giangtd"
    fun bind(item: ImageFile, listener: GalleryAdapter.GalleryListener) {

        Log.d(TAG, "bind: path: "+ item.path)
        if (item.path != "") {
            Glide.with(itemView.context).load(item.path).into(binding.image)
        }
        binding.root.setOnClickListener {
            listener.onClickedItem(item, adapterPosition)
            Log.d(TAG, "bind: item click : "+ item.url)
        }

        Log.d(TAG, "bind: item click full")
    }

    companion object {
        fun create(parent: ViewGroup): GalleryItemViewHolder {
            return GalleryItemViewHolder(GalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}