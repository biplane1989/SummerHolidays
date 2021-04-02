package com.example.summerholidays.ui.details

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.summerholidays.databinding.DetailItemBinding
import com.example.summerholidays.databinding.GalleryItemBinding
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.ui.gallery.GalleryAdapter
import com.example.summerholidays.ui.gallery.GalleryItemViewHolder


class DetailItemViewHolder(val binding: DetailItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val TAG = "giangtd"
    fun bind(item: ImageFile, listener: DetailAdapter.DetailListener) {

        if (item.path != "") {
            Glide.with(itemView.context).load(item.path).into(binding.image)
        }
        binding.root.setOnClickListener {
            listener.onClickedItem(item)
        }
    }

    companion object {
        fun create(parent: ViewGroup): DetailItemViewHolder {
            return DetailItemViewHolder(DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}