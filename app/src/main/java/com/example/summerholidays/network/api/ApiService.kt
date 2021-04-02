package com.example.rotrofitwithcoroutineexampletow.data.api

import com.example.rotrofitwithcoroutineexampletow.data.model.UnsplashPhoto
import com.example.summerholidays.utils.Constance
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("/photos/")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("client_id") clientID: String = Constance.ID_API): Response<List<UnsplashPhoto>>

    @GET
    suspend fun downloadPhoto(@Url url: String): ResponseBody
}