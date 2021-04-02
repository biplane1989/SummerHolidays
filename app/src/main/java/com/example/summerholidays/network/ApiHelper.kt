package com.example.summerholidays.network

import com.example.rotrofitwithcoroutineexampletow.data.api.RetrofitBuilder
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import okhttp3.ResponseBody

object ApiHelper {
    val apiRespository = ApiRespository()
    suspend fun getListPhoto(page: Int): List<ImageItem>? {
        return apiRespository.getListPhoto(page)
    }

    suspend fun getPhoto(url: String): ResponseBody {
        return apiRespository.getPhoto(url)
    }
}