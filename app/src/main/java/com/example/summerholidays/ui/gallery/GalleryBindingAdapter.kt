package com.example.summerholidays.ui.gallery

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleIf")
fun View.visibleIf(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleProgress")
fun ProgressBar.visibleIf(visible: Boolean) {
    Log.d("TAG", "visibleIf: stauts : "+ visible)
    visibility = if (visible) View.VISIBLE else View.GONE
}