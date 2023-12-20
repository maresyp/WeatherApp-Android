package com.example.weatherapp

import android.content.Context
import com.example.weatherapp.api.WeatherDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WeatherDataManager {

    private val weatherDataProvider = WeatherDataProvider()
    /**
     * Provides weather data for a given city. If the data is already saved locally, it will be
     * read from a file. Otherwise, it will be downloaded from the OpenWeatherMap API.
     *
     * @param query The name of the city to download weather data for.
     * @param context The context to use to save the data.
     * @return A JSONObject containing the weather data, or null if an error occurred.
     */
    suspend fun getData(query: String, context: Context): JSONObject? = withContext(Dispatchers.IO) {
        // check if file with given query already exists
        val filename = query.plus(".json")
        // TODO: get universal city name
        if (context.fileList().contains(filename)) {
            println("File with name $filename already exists. Reading data from file.")
            readData(filename, context)
            // TODO: check if data is up to date
        } else {
            println("File with name $filename does not exist. Downloading data from API.")
            val data = weatherDataProvider.downloadWeatherData(query)
            if (data != null) {
                saveData(filename, data, context)
            }
            data
        }
    }

    /**
     * Saves the given data to a file.
     *
     * @param filename Filename to save the data for.
     * @param data The data to save.
     * @param context The context to use to save the data.
     * @return bytes written if the data was saved successfully, 0 otherwise.
     */
    private suspend fun saveData(filename: String, data: JSONObject, context: Context) : Int = withContext(Dispatchers.IO) {
        try {
            val byteArray = data.toString().toByteArray()
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { fileOutputStream ->
                fileOutputStream.write(byteArray)
            }
            /* return the number of bytes written */
            byteArray.size
        } catch (e: Exception) {
            println("Error: ${e.message}")
            0
        }
    }

    /**
     * Opens a file and reads its contents into a JSONObject.
     *
     * @param filename The name of the file to read.
     * @param context The context to use to open the file.
     * @return A JSONObject containing the weather data, or null if an error occurred.
     */
    private suspend fun readData(filename: String, context: Context): JSONObject? = withContext(Dispatchers.IO) {
        try {
            context.openFileInput(filename).bufferedReader().use {
                JSONObject(it.readText())
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }
}