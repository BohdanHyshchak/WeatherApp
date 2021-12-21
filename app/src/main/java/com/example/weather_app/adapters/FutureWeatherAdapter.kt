package com.example.weather_app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.databinding.RcvDailyForecastBinding
import com.example.weather_app.models.future.Daily
import java.util.*
import kotlin.math.roundToInt

class FutureWeatherAdapter : RecyclerView.Adapter<FutureWeatherAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(val binding: RcvDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(response: Daily) {
            binding.apply {
                Log.d("Future Adapter", "${response.weather.size}")
                Glide.with(binding.root).load("http://openweathermap.org/img/w/${response.weather[0].icon}.png").into(ivIcon)
                tvTemperature.text = "${response.temp.day.roundToInt()}°С"
                tvProbability.text = "${(response.pop * 100).roundToInt()}%"
                tvNameOfDay.text = "${getDay(response.dt * 1000L)}"
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Daily>() {
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

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RcvDailyForecastBinding.inflate(inflater, parent, false)
        return ForecastViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
//        holder.itemView.apply {
//        }
    }

    private fun getDay(millis: Long): String {
        // ANALOG METHOD
//        val test = System.currentTimeMillis()
//        Log.d("Date", "currentMillis is $test")
//        val currentDate = Date(millis.toLong())
//        Log.d("Date", "millis is $millis")
//        Log.d("Date", "currentDate is $currentDate")

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            else -> "Sat"
        }
    }
}
