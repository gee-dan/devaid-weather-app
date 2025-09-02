package org.example.weatherapp.model

data class FinalDayData(
    val minTempC: Double,
    val maxTempC: Double,
    val avgHumidity: Double,
    val maxWindKph: Double,
    val windDir: String
)
