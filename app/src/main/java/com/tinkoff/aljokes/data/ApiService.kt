package com.tinkoff.aljokes.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
//    https://v2.jokeapi.dev/joke/Dark?amount=10&type=twopart
    @GET("/joke/Any?blacklistFlags=nsfw,religious,political,racist,sexist,explicit")
    suspend fun getJoke(
        @Query("amount") amount: Int = 10,
        @Query("type") type: String = "twopart"
    ): JokeResponse
}