package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.api.WeatherDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherDataProvider = WeatherDataProvider()
        CoroutineScope(Dispatchers.Main).launch {
            val weatherData = weatherDataProvider.getWeatherData("London")
            println(weatherData)
        }
    }
}