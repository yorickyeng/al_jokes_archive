package com.tinkoff.aljokes.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CatRetrofitInstance {
    private const val BASE_URL = "https://api.thecatapi.com/"
    val api: CatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatApiService::class.java)
    }
}