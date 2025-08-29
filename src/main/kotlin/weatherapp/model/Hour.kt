package org.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Hour(
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("wind_dir")
    val windDir: String
)
