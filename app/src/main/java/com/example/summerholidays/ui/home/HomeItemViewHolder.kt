package com.example.summerholidays.ui.home

import android.renderscript.ScriptGroup
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.rotrofitwithcoroutineexampletow.data.model.LoadingStatus
import com.example.summerholidays.R
import com.example.summerholidays.databinding.HomeItemBinding

class HomeItemViewHolder(val binding: HomeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val TAG = "giangtd"
    fun bind(item: ImageItem, listener: HomeAdapter.NetworkListener) {

        if (item.thumb != "") {
            Glide.with(itemView.context).load(item.thumb).into(binding.image)
        }

        when (item.downloaded) {
            LoadingStatus.IDLE -> {
                binding.download.visibility = View.VISIBLE
                binding.progress.visibility = View.INVISIBLE
            }
            LoadingStatus.LOADING -> {
                binding.download.visibility = View.INVISIBLE
                binding.progress.visibility = View.VISIBLE
            }
            LoadingStatus.LOADONE -> {
                binding.download.visibility = View.INVISIBLE
                binding.progress.visibility = View.INVISIBLE
            }
        }

        binding.download.setOnClickListener {
            listener.dowloadImage(item)
            Log.d(TAG, "bind: item click : "+ item.url)
        }

        Log.d(TAG, "bind: item click full")
    }

    fun updateView(item: ImageItem) {
        Log.d(TAG, "bind: item click 2: "+ item.url)
        Log.d(TAG, "updateView: status: "+ item.downloaded)
        when (item.downloaded) {
            LoadingStatus.IDLE -> {
                binding.download.visibility = View.VISIBLE
                binding.progress.visibility = View.INVISIBLE
            }
            LoadingStatus.LOADING -> {
                Log.d(TAG, "updateView: status loading")
                binding.progress.visibility = View.VISIBLE
                binding.download.visibility = View.INVISIBLE
            }
            LoadingStatus.LOADONE -> {
                binding.download.visibility = View.INVISIBLE
                binding.progress.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): HomeItemViewHolder {
            return HomeItemViewHolder(HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}