package com.example.weatherapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.data.NetworkNotAvailableException
import com.example.weatherapp.data.SharedViewModel
import com.example.weatherapp.data.WeatherDataManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainFragment : Fragment() {

    private var weatherDataManager = WeatherDataManager()
    // by activityViewModels() is a delegate property that gets the view model associated with the activity
    // https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Set up the SearchView */
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getDataWithErrorHandling(query, requireContext(), false)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Handle search query text change
                // TODO : add autocomplete
                return false
            }
        })

        /* Set up the notification */
        sharedViewModel.refreshEvent.observe(viewLifecycleOwner, Observer {
            Log.d("MainFragment", "Notification received")
            onResume()
        })

        /* Try to refresh data on startup */
        val currentLocationData = weatherDataManager.getCurrentLocationFromPreferences(requireContext())
        if (currentLocationData != null) {
            val query = currentLocationData.getJSONObject("city").getString("name")
            getDataWithErrorHandling(query, requireContext(), false)
        }
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
            val longitudeTextView: TextView? = view?.findViewById(R.id.longitude)
            val latitudeTextView: TextView? = view?.findViewById(R.id.latitude)
            val timeTextView: TextView? = view?.findViewById(R.id.time)
            val temperatureTextView: TextView? = view?.findViewById(R.id.temperature)
            val pressureTextView: TextView? = view?.findViewById(R.id.pressure)
            val descriptionTextView: TextView? = view?.findViewById(R.id.description)
            val weatherIcon: ImageView? = view?.findViewById(R.id.weather_icon)

            val cityInfo = it.getJSONObject("city").getString("name")
            cityTextView?.text = "City: $cityInfo"

            val longitudeInfo = it.getJSONObject("city").getJSONObject("coord").getString("lon")
            longitudeTextView?.text = "Longitude: $longitudeInfo"

            val latitudeInfo = it.getJSONObject("city").getJSONObject("coord").getString("lat")
            latitudeTextView?.text = "Latitude: $latitudeInfo"

            val timeInfo = it.getJSONArray("list").getJSONObject(0).getString("dt")
            val dateTime = Date(timeInfo.toLong() * 1000L)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.format(dateTime)

            timeTextView?.text = "Time: $dateTime"

            val temperatureInfo = it.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp")
            var temperatureUnit = weatherDataManager.getUnits(requireContext())
            temperatureUnit = if (temperatureUnit == "metric") "°C" else "°F"
            temperatureTextView?.text = "Temperature: $temperatureInfo $temperatureUnit"

            val pressureInfo = it.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("pressure")
            pressureTextView?.text = "Pressure: $pressureInfo"

            val descriptionInfo = it.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description")
            descriptionTextView?.text = "Description: $descriptionInfo"

            val iconInfo = it.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon")
            val iconId = resources.getIdentifier("com.example.weatherapp:drawable/ic_$iconInfo", null, null)
            weatherIcon?.setImageResource(iconId)

        }
    }

    private fun getDataWithErrorHandling(query: String, context: Context, forceDownload: Boolean) {
        lifecycleScope.launch {
            try {
                weatherDataManager.getDataWithNotification(query, context, forceDownload, sharedViewModel)
            } catch (e: NetworkNotAvailableException) {
                Log.e("MainFragment", "No network connection", e)
                Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MainFragment", "Unable to update weather data", e)
                Toast.makeText(context, "Unable to update weather data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}