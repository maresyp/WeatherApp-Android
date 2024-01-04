package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.example.weatherapp.api.WeatherDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class NetworkNotAvailableException(message: String) : Exception(message)

class WeatherDataManager {

    private val weatherDataProvider = WeatherDataProvider()

    private suspend fun downloadAndSaveData(filename: String, query: String, context: Context): JSONObject {
        val data = weatherDataProvider.downloadWeatherData(query)
        data.let {
            saveData(filename, it, context)
            Log.d("WeatherDataManager", "Saved data to $filename")
        }

        return data
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    @Throws(NetworkNotAvailableException::class, IOException::class, JSONException::class)
    suspend fun getData(query: String, context: Context, forceDownload: Boolean): JSONObject = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable(context)) {
            throw NetworkNotAvailableException("No network connection")
        }

        val filename = "${query.lowercase()}.json"
        if (context.fileList().contains(filename) && !forceDownload) {
            Log.d("WeatherDataManager", "Reading data from $filename")
            readData(filename, context).let { data ->

                /* get timestamp from saved data and current time */
                val timestamp = data.getJSONArray("list").getJSONObject(0).getLong("dt")
                val currentTime = System.currentTimeMillis() / 1000L // convert to seconds

                /* if the data is older than 1 hour, download new data */
                if (currentTime - timestamp > 3600) {
                    Log.d("WeatherDataManager", "Data is older than 1 hour, downloading new data for $query")
                    downloadAndSaveData(filename, query, context)
                } else {
                    data
                }
            }
        } else {
            Log.d("WeatherDataManager", "Downloading data for $query")
            downloadAndSaveData(filename, query, context)
        }
    }.let {
        // save current location to preferences for later use
        saveCurrentLocationToPreferences(it, context)
    }

    @Throws(IOException::class)
    private suspend fun saveData(filename: String, data: JSONObject, context: Context) = withContext(Dispatchers.IO) {
        val byteArray = data.toString().toByteArray()
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fileOutputStream ->
            fileOutputStream.write(byteArray)
        }
    }

    @Throws(IOException::class, JSONException::class)
    private suspend fun readData(filename: String, context: Context): JSONObject = withContext(Dispatchers.IO) {
        context.openFileInput(filename).bufferedReader().use {
            JSONObject(it.readText())
        }
    }

    @Throws(IOException::class)
    suspend fun clearLocalData(context: Context) = withContext(Dispatchers.IO) {
        context.fileList().forEach {
            Log.d("WeatherDataManager", "Deleting file $it")
            if (!context.deleteFile(it)) {
                Log.e("WeatherDataManager", "Error deleting file $it")
                throw IOException("Error deleting file $it")
            }
        }
    }

    private fun saveCurrentLocationToPreferences(data: JSONObject, context: Context): JSONObject {
        val sharedPreferences = context.getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putString("location", data.toString())
            apply()
        }
        return data
    }

    fun getCurrentLocationFromPreferences(context: Context): JSONObject? {
        val sharedPreferences = context.getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
        val currentLocationData = sharedPreferences.getString("location", null)
        return if (currentLocationData != null) {
            JSONObject(currentLocationData)
        } else {
            null
        }
    }

    fun clearCurrentLocationFromPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            remove("location")
            apply()
        }
    }
}