package org.example

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.example.weatherapp.api.WeatherApiService
import org.example.weatherapp.model.FinalDayData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

fun main() = runBlocking {
    // get the API key from an environment variable
    val apiKey = System.getenv("WEATHER_API_KEY") ?: run {
        println("Error: WEATHER_API_KEY environment variable not set.")
        return@runBlocking
    }

    // fetch cities from the file
    val cities = try {
        val fileUrl = object {}.javaClass.getResource("/cities.txt")
        if (fileUrl == null) {
            println("Error: cities.txt file not found.")
            return@runBlocking
        }
        File(fileUrl.toURI()).readLines().map { it.trim() }.filter { it.isNotEmpty() }
    } catch (e: Exception) {
        println("Error reading cities.txt: ${e.message}")
        return@runBlocking
    }

    if (cities.isEmpty()) {
        println("The cities.txt file is empty or could not be read.")
        return@runBlocking
    }

    val allWeatherData = mutableMapOf<String, FinalDayData>()

    // build the Retrofit instance
    val gson = GsonBuilder().setLenient().create()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val service = retrofit.create(WeatherApiService::class.java)

    for (city in cities) {
        try {
            val response = service.getForecast(apiKey, city)
            // get the forecast for the next day (index 1 of the list)
            val nextDayForecast = response.forecast.forecastday.getOrNull(1)

            if (nextDayForecast != null) {
                val maxWindKph = nextDayForecast.day.maxWindKph

                // find the wind direction for the hour with the maximum wind speed
                val windDir = nextDayForecast.hour.firstOrNull { it.windKph == maxWindKph }?.windDir ?: "N/A"

                // output data
                val cityData = FinalDayData(
                    minTempC = nextDayForecast.day.minTempC,
                    maxTempC = nextDayForecast.day.maxTempC,
                    avgHumidity = nextDayForecast.day.avgHumidity,
                    maxWindKph = nextDayForecast.day.maxWindKph,
                    windDir = windDir
                )

                allWeatherData[city] = cityData
            }
        } catch (e: Exception) {
            println("Error fetching data for $city: ${e.message}")
        }
    }

    // format and print the table to STDOUT
    printWeatherTable(allWeatherData)
}

fun printWeatherTable(data: Map<String, FinalDayData>) {
    // determine the longest city name for dynamic column width
    val maxCityNameLength = data.keys.maxOfOrNull { it.length } ?: 15
    val cityColumnWidth = (maxCityNameLength + 2).coerceAtLeast(15)

    // column headers
    val headers = listOf("City", "Min Temp (C)", "Max Temp (C)", "Humidity (%)", "Wind (kph)", "Wind Dir")

    // format string
    val formatString = "| %-${cityColumnWidth}s | %-12s | %-12s | %-12s | %-12s | %-12s |"
    val separator = "+${"-".repeat(cityColumnWidth + 2)}+${"-".repeat(12 + 2)}+${"-".repeat(12 + 2)}+${"-".repeat(12 + 2)}+${"-".repeat(12 + 2)}+${"-".repeat(12 + 2)}+"

    // print table header
    println(separator)
    println(formatString.format(headers[0], headers[1], headers[2], headers[3], headers[4], headers[5]))
    println(separator)

    // print data for each city
    data.forEach { (city, dayData) ->
        println(formatString.format(
            city,
            dayData.minTempC,
            dayData.maxTempC,
            dayData.avgHumidity,
            dayData.maxWindKph,
            dayData.windDir
        ))
    }

    // print table footer
    println(separator)
}