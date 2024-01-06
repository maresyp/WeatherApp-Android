package com.example.weatherapp.api

import android.content.Context
import android.util.Log
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.WeatherDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class WeatherDataProvider {

    @Throws(IOException::class, JSONException::class)
    suspend fun downloadWeatherData(cityName: String, context: Context): JSONObject = withContext(Dispatchers.IO) {
        val apiKey: String = BuildConfig.OpenWeatherMapApiKey
        val units = WeatherDataManager().getUnits(context)

        val url = URL("https://api.openweathermap.org/data/2.5/forecast?q=$cityName&appid=$apiKey&units=$units")
        Log.d("WeatherDataProvider", "Downloading data from $url")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("HTTP error code: $responseCode")
            }

            inputStream.bufferedReader().use {
                it.readText()
            }
        }.let { responseBody -> JSONObject(responseBody) }
    }
}