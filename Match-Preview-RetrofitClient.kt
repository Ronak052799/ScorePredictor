package com.example.match_preview_api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // connection timeout
        .readTimeout(30, TimeUnit.SECONDS)     // socket read timeout
        .writeTimeout(30, TimeUnit.SECONDS)    // socket write timeout
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.172:3000") // Replace with your Mac's IP if different
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val service: MatchPreviewService = retrofit.create(MatchPreviewService::class.java)
}
