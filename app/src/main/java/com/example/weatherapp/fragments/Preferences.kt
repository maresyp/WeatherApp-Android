package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.weatherapp.R
import com.example.weatherapp.WeatherDataManager
import kotlinx.coroutines.launch

class Preferences : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<SwitchPreferenceCompat>("units")
            ?.setOnPreferenceChangeListener { _, newValue ->
                Log.d("Preferences", "Notifications enabled: $newValue")
                // TODO : handle change of units
                true // Return true if the event is handled.
            }

        findPreference<Preference>("clearFiles")
            ?.setOnPreferenceClickListener {
                Log.d("Preferences", "clearFiles was clicked")
                val weatherDataManager = WeatherDataManager()
                lifecycleScope.launch {
                    weatherDataManager.clearLocalData(requireContext())
                }
                true // Return true if the click is handled.
            }

    }
}
