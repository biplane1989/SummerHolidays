package com.example.summerholidays.network

import com.example.rotrofitwithcoroutineexampletow.data.api.RetrofitBuilder
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import okhttp3.ResponseBody

class ApiRespository : IApiRepository {
    override suspend fun getListPhoto(page: Int): List<ImageItem>? {
        return try {
            val response = RetrofitBuilder.apiService.getPhotos(page)
            if (response.isSuccessful) {
                val listUnsplashPhoto = response.body()
                if (listUnsplashPhoto != null) {
                    val newList = listUnsplashPhoto.map {
                        ImageItem(it.id, it.urls.thumb ?: "", it.urls.raw ?: "")
                    }
                    newList
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPhoto(url: String): ResponseBody {
        val newUrl = "$url&w=1080&dpi=1"
        return RetrofitBuilder.apiService.downloadPhoto(newUrl)
    }
}