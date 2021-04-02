package com.example.rotrofitwithcoroutineexampletow.data.model

enum class LoadingStatus {
    IDLE, LOADING, LOADONE
}

data class ImageItem(var url: String, var thumb: String, var raw: String, var downloaded: LoadingStatus = LoadingStatus.IDLE, var clicked: Boolean = false)