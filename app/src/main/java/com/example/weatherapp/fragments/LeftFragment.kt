package com.example.weatherapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherDataManager

class LeftFragment : Fragment() {

    private val weatherDataManager = WeatherDataManager()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_left, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        Log.d("MainFragment", "onResume called")
        weatherDataManager.getCurrentLocationFromPreferences(requireContext()).let {
            if (it == null) {
                Log.d("MainFragment", "No data to display")
                return@let
            }

            val cityTextView: TextView? = view?.findViewById(R.id.city)
            val windSpeed: TextView? = view?.findViewById(R.id.wind_speed)
            val windDirection: TextView? = view?.findViewById(R.id.wind_direction)
            val humidity: TextView? = view?.findViewById(R.id.humidity)
            val visibility: TextView? = view?.findViewById(R.id.visibility)


            val cityInfo = it.getJSONObject("city").getString("name")
            cityTextView?.text = "City: $cityInfo"

            val windSpeedInfo = it.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getString("speed")
            windSpeed?.text = "Wind speed: $windSpeedInfo"

            val windDirectionInfo = it.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getString("deg")
            windDirection?.text = "Wind direction: $windDirectionInfo"

            val humidityInfo = it.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("humidity")
            humidity?.text = "Humidity: $humidityInfo"

            val visibilityInfo = it.getJSONArray("list").getJSONObject(0).getString("visibility")
            visibility?.text = "Visibility: $visibilityInfo"

        }
    }
}
