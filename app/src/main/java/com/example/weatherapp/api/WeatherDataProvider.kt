package com.example.weatherapp.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherDataProvider {
    suspend fun getWeatherData(cityName: String): JSONObject = withContext(Dispatchers.IO) {
        val apiKey: String = "7deefcd94992e995e527b032d3cfac7f"
        val url = URL("https://api.openweathermap.org/data/2.5/forecast?q=$cityName&appid=$apiKey")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"

            println("\nSending 'GET' request to URL : $url")
            println("Response Code : $responseCode")

            inputStream.bufferedReader().use {
                it.readText()
            }
        }.let { responseBody -> JSONObject(responseBody) }
    }
}