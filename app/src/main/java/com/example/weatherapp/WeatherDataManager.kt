package com.example.weatherapp

import android.content.Context
import com.example.weatherapp.api.WeatherDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WeatherDataManager {

    private val weatherDataProvider = WeatherDataProvider()

    /**
     * Downloads weather data from the OpenWeatherMap API and saves it to a file.
     *
     * @param filename Filename to save the data for.
     * @param query The query to download weather data for.
     * @param context The context to use to save the data.
     * @return A JSONObject containing the weather data, or null if an error occurred.
     */
    private suspend fun downloadAndSaveData(filename: String, query: String, context: Context): JSONObject? {
        val data = weatherDataProvider.downloadWeatherData(query)
        data?.let { saveData(filename, it, context) } // null safe operation (if data is not null)
        return data
    }

    /**
     * Gets weather data for the given query.
     *
     * @param query The query to get weather data for.
     * @param context The context to use to get the data.
     * @return A JSONObject containing the weather data, or null if an error occurred.
     */
    suspend fun getData(query: String, context: Context): JSONObject? = withContext(Dispatchers.IO) {
        val filename = "$query.json"
        if (context.fileList().contains(filename)) {
            readData(filename, context)?.let { data -> // null safe operation (if data is not null)

                /* get timestamp from saved data and current time */
                val timestamp = data.getJSONArray("list").getJSONObject(0).getLong("dt")
                val currentTime = System.currentTimeMillis() / 1000L

                /* if the data is older than 1 hour, download new data */
                if (currentTime - timestamp > 3600) {
                    downloadAndSaveData(filename, query, context)
                } else {
                    data
                }
            }
        } else {
            downloadAndSaveData(filename, query, context)
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

    /**
     * Clears all locally saved data.
     *
     * @param context The context to use to delete the files.
     */
    suspend fun clearLocalData(context: Context) = withContext(Dispatchers.IO) {
        context.fileList().forEach {
            context.deleteFile(it)
        }
    }
}