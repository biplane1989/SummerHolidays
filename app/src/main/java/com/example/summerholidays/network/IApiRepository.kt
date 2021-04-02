package com.example.summerholidays.network

import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import okhttp3.ResponseBody

interface IApiRepository {
    suspend fun getListPhoto(page: Int): List<ImageItem>?
    suspend fun getPhoto(url: String): ResponseBody
}