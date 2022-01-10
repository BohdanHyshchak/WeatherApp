package com.example.weather_app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.databinding.RcvDailyForecastBinding
import com.example.weather_app.models.future.Daily
import com.example.weather_app.utils.unixTimestampToDateString
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class FutureWeatherAdapter : ListAdapter<Daily, ForecastViewHolder>(DifferCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RcvDailyForecastBinding.inflate(inflater, parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}

class ForecastViewHolder(private val binding: RcvDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: Daily) {
        binding.apply {
            Glide.with(binding.root).load("http://openweathermap.org/img/w/${response.weather[0].icon}.png").into(ivIcon)
            tvTemperature.text = "${response.temp.day.roundToInt()}°С"
            tvProbability.text = "${(response.pop * 100).roundToInt()}%"
            tvNameOfDay.text = response.dt.unixTimestampToDateString("EEE").removeSuffix(".")
        }
    }
}

private class DifferCallBack : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(
        oldItem: Daily,
        newItem: Daily
    ): Boolean {
        return oldItem.weather == newItem.weather
    }

    override fun areContentsTheSame(
        oldItem: Daily,
        newItem: Daily
    ): Boolean {
        return oldItem == newItem
    }
}
