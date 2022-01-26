package com.example.weather_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.weather_app.R
import com.example.weather_app.models.future.Daily
import com.example.weather_app.utils.unixTimestampToDateString

class ListAdapter(private val isLargeHeight: Boolean, private val isLargeWeight: Boolean, futureWeatherList: List<Daily>) : BaseAdapter() {

    private val weatherList = futureWeatherList
    override fun getCount(): Int {
        return weatherList.size
    }

    override fun getItem(p0: Int): Any {
        return weatherList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1
        if (convertView == null) {
            convertView = LayoutInflater.from(p2?.context).inflate(R.layout.list_view_item, p2, false)
        }
        val tvNameOfCity = convertView?.findViewById<TextView>(R.id.tvNameOfDayListView)
        val tvHumidity = convertView?.findViewById<TextView>(R.id.tvTemperatureListView)
        val ivIcon = convertView?.findViewById<ImageView>(R.id.ivIconListView)

        tvNameOfCity?.text = weatherList[p0].dt.unixTimestampToDateString("EEEE")
        tvHumidity?.text = "${weatherList[p0].humidity} %"
        if(ivIcon != null) {
            Glide.with(convertView!!.context).load("http://openweathermap.org/img/w/${weatherList[p0].weather[0].icon}.png").into(ivIcon)
        }

        return convertView!!
    }
}
