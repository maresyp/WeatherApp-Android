package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.weatherapp.NetworkNotAvailableException
import com.example.weatherapp.R
import com.example.weatherapp.WeatherDataManager
import kotlinx.coroutines.launch

// https://developer.android.com/develop/ui/views/components/settings?hl=en
class Preferences : PreferenceFragmentCompat() {

    private val weatherDataManager = WeatherDataManager()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<SwitchPreferenceCompat>("units")
            ?.setOnPreferenceChangeListener { _, newValue ->
                val unit = if (newValue as Boolean) {
                    "metric"
                } else {
                    "imperial"
                }

                Log.d("Preferences", "Changing units to: $unit")
                weatherDataManager.setUnits(unit, requireContext())
                weatherDataManager.getCurrentLocationFromPreferences(requireContext())?.let {
                    val query = it.getJSONObject("city").getString("name")
                    lifecycleScope.launch {
                        try {
                            weatherDataManager.getData(query, requireContext(), true)
                        } catch (e: NetworkNotAvailableException) {
                            Log.e("Preferences", "No network connection, units wont be updated", e)
                            Toast.makeText(requireContext(), "No network connection, units wont be updated", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e("Preferences", "Unable to update weather data after units change", e)
                            Toast.makeText(requireContext(), "Unable to update weather data after units change", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                true // Return true if the event is handled.
            }

        findPreference<Preference>("clearFiles")
            ?.setOnPreferenceClickListener {
                Log.d("Preferences", "clearFiles was clicked")
                lifecycleScope.launch {
                    weatherDataManager.clearLocalData(requireContext())
                    weatherDataManager.clearCurrentLocationFromPreferences(requireContext())
                }
                true // Return true if the click is handled.
            }
    }
}
