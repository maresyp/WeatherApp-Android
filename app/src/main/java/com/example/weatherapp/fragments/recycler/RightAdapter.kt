package com.example.weatherapp.fragments.recycler
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherDataManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// https://developer.android.com/develop/ui/views/layout/recyclerview

class RightAdapter : RecyclerView.Adapter<RightAdapter.ViewHolder>() {

    private val weatherDataManager = WeatherDataManager()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTime: TextView
        val itemTemp: TextView
        val itemPressure: TextView
        val itemDescription: TextView
        init {
            itemTime = view.findViewById(R.id.item_time_text_view)
            itemTemp = view.findViewById(R.id.item_temperature_text_view)
            itemPressure = view.findViewById(R.id.item_pressure_text_view)
            itemDescription = view.findViewById(R.id.item_element_desc_text_view)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        Log.d("RightAdapter", "onBindViewHolder called for $position position")

        weatherDataManager.getCurrentLocationFromPreferences(viewHolder.itemTime.context)?.let {
            val timeInfo = it.getJSONArray("list").getJSONObject(position).getString("dt")
            val dateTime = Date(timeInfo.toLong() * 1000L)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.format(dateTime)

            viewHolder.itemTime.text = "Time: $dateTime"

            val temperatureInfo = it.getJSONArray("list").getJSONObject(position).getJSONObject("main").getString("temp")
            viewHolder.itemTemp.text = "Temperature: $temperatureInfo"

            val pressureInfo = it.getJSONArray("list").getJSONObject(position).getJSONObject("main").getString("pressure")
            viewHolder.itemPressure.text = "Pressure: $pressureInfo"

            val descriptionInfo = it.getJSONArray("list").getJSONObject(position).getJSONArray("weather").getJSONObject(0).getString("description")
            viewHolder.itemDescription.text = "Description: $descriptionInfo"
        }
    }
    override fun getItemCount() = 40
}
