package org.example.weatherapp.api

import org.example.weatherapp.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int = 2,  // today and the next day
        @Query("alerts") alerts: String = "no",
        @Query("aqi") aqi: String = "no"
    ): WeatherResponse
}