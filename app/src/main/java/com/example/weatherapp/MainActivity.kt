package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.fragments.ViewPagerAdapter
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2
    private var weatherDataManager = WeatherDataManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(this)

        // Start on the main screen
        viewPager.setCurrentItem(2, false)

        /* Set up the SwipeRefreshLayout */
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            val currentLocationData = weatherDataManager.getCurrentLocationFromPreferences(this)
            if (currentLocationData == null) {
                Toast.makeText(this, "No data to refresh", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
                return@setOnRefreshListener
            }

            // Retrieve the city name from the old data
            val query = currentLocationData.getJSONObject("city").getString("name")

            lifecycleScope.launch {
                try {
                    weatherDataManager.getData(query, this@MainActivity, true)
                } catch (e: NetworkNotAvailableException) {
                    Log.e("MainFragment", "No network connection", e)
                    Toast.makeText(this@MainActivity, "No network connection", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("MainFragment", "Unable to update weather data", e)
                    Toast.makeText(this@MainActivity, "Unable to update weather data", Toast.LENGTH_SHORT).show()
                }
            }

            // Update the UI
            onResume()

            // When the refreshing is done, update the UI
            swipeRefreshLayout.isRefreshing = false
        }
    }
}