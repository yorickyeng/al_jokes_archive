package com.tinkoff.aljokes.data

import com.google.gson.annotations.SerializedName

data class JokeResponse(
    @SerializedName("jokes")
    val jokes: List<Joke>,
    @SerializedName("amount")
    val amount: String,

    )

data class Joke(
    @SerializedName("setup")
    val setup: String,
    @SerializedName("delivery")
    val delivery: String,
)