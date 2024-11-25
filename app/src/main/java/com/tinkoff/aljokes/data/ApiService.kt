package com.tinkoff.aljokes.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/joke/Dark")
    suspend fun getJoke(
        @Query("amount") amount: Int = 10,
        @Query("type") type: String = "twopart"
    ): JokeResponse
}