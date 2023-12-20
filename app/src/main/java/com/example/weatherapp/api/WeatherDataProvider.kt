package com.example.weatherapp.api

import com.example.weatherapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class WeatherDataProvider {
    suspend fun downloadWeatherData(cityName: String): JSONObject? = withContext(Dispatchers.IO) {
        val apiKey: String = BuildConfig.OpenWeatherMapApiKey
        val url = URL("https://api.openweathermap.org/data/2.5/forecast?q=$cityName&appid=$apiKey")

        try {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }

                inputStream.bufferedReader().use {
                    it.readText()
                }
            }.let { responseBody -> JSONObject(responseBody) }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }
}