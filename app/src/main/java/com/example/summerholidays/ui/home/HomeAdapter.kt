package com.example.summerholidays.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem

class HomeAdapter(private val listener: NetworkListener) : ListAdapter<ImageItem, HomeItemViewHolder>(HistoryItemDiffUnit()) {

    val TAG = "giangtd"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItemViewHolder {
        return HomeItemViewHolder.create(parent)
    }

//    override fun onBindViewHolder(holder: HomeItemViewHolder, position: Int, payloads: MutableList<Any>) {
//        val status = payloads.firstOrNull()
//        if (status != null) {
//            // update 1 phan
//            Log.d(TAG, "onBindViewHolder: 1 phan")
//            holder.updateView(getItem(position))
//        } else {
//            Log.d(TAG, "onBindViewHolder: full")
//            // update full
//            onBindViewHolder(holder, position)
//        }
//    }

    override fun onBindViewHolder(holder: HomeItemViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface NetworkListener {
        fun dowloadImage(item: ImageItem)
    }

}

class HistoryItemDiffUnit : DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
//        return false
    }

//    override fun getChangePayload(oldItem: ImageItem, newItem: ImageItem): Any? {
//
//        if (oldItem.downloaded != newItem.downloaded ) return newItem        // muon update cai nao thi check cai do khac, con lai bang nhau
//        return null
//    }
}