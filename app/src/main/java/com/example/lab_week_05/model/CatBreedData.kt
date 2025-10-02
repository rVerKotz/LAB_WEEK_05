package com.example.lab_week_05.model

import com.squareup.moshi.Json

data class CatBreedData(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    @Json(name = "breeds") val breeds: List<Any>?
)
