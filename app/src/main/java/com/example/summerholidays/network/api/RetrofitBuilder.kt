package com.example.rotrofitwithcoroutineexampletow.data.api

import com.example.summerholidays.utils.Constance
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(logger).build()

    fun getRetrofit(): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create()).baseUrl(Constance.BASE_URL)
        .client(client).build()

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}