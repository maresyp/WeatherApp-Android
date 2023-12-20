package com.example.weatherapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapp.R
import com.example.weatherapp.WeatherDataManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainFragment : Fragment() {

    var weatherDataManager = WeatherDataManager()

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
                // TODO : add code to display weather data for the given query
                lifecycleScope.launch {
                    weatherDataManager.getData(query, requireContext())
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Handle search query text change
                return false
            }
        })

        /* Set up the SwipeRefreshLayout */
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            val weatherDataManager = WeatherDataManager()
            var data: JSONObject?
            lifecycleScope.launch {

                // TODO : this should force reload the data from the API
                data = weatherDataManager.getData("London", requireContext())
                println(data)
            }
            // When the refreshing is done, update the UI
            swipeRefreshLayout.isRefreshing = false
        }
    }
}