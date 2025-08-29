package org.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,
    @SerializedName("mintemp_c")
    val minTempC: Double,
    @SerializedName("avghumidity")
    val avgHumidity: Double,
    @SerializedName("maxwind_kph")
    val maxWindKph: Double,
    @SerializedName("wind_dir")
    val windDir: String
)
