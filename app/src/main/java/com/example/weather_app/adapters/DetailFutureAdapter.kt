package com.example.weather_app.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.databinding.RcvDetailDailyForecastBinding
import com.example.weather_app.models.future.Daily
import com.example.weather_app.utils.Constants.Companion.MOON_IMG_URL
import com.example.weather_app.utils.Constants.Companion.SUN_IMG_URL
import com.example.weather_app.utils.unixTimestampToHoursMinutesTimeString
import java.util.*
import kotlin.math.roundToInt

class DetailFutureAdapter : RecyclerView.Adapter<DetailFutureAdapter.ForecastViewHolder>() {

    val TAG = "Detail Future Adapter"

    @SuppressLint("SetTextI18n")
    inner class ForecastViewHolder(private val binding: RcvDetailDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun updateRCV(response: Daily) {
            bindTopContainer(response)
            bindGeneralContainer(response)
            bindTemperatureBlock(response)
            bindSunriseSunsetBlock(response)
        }

        private fun bindTopContainer(response: Daily) {
            binding.tvNameOfDay.text = getDay(response.dt * 1000L)
            binding.tvDate.text = getDate(response.dt * 1000L)
            Glide.with(binding.root).load("http://openweathermap.org/img/w/${response.weather[0].icon}.png").into(binding.ivIcon)
            binding.tvDescription.text = response.weather[0].description
        }

        private fun bindGeneralContainer(response: Daily) {
            binding.tvWindDescription.text = getWindInfo(response.wind_deg, response.wind_speed)
            binding.tvWindGustDescription.text = "${response.wind_gust} km/h"
            binding.tvCloudsDescription.text = "${response.clouds} %"
            binding.tvHumidityDescription.text = "${response.humidity} %"
            binding.tvProbabilityDescription.text = "${(response.pop * 100).roundToInt()} %"
            binding.tvPrecipitationDescription.text = "${response.rain.roundToInt()} mm"
            binding.tvPressureDescription.text = "${response.pressure} mb"
            binding.tvDewPointDescription.text = "${response.dew_point}°С"
        }

        private fun bindTemperatureBlock(response: Daily) {
            binding.tvMorningTempReal.text = "${response.temp.morn.roundToInt()}°С"
            binding.tvDayTempReal.text = "${response.temp.day.roundToInt()}°С"
            binding.tvEveTempReal.text = "${response.temp.eve.roundToInt()}°С"
            binding.tvNightTempReal.text = "${response.temp.night.roundToInt()}°С"
            binding.tvMorningTempFL.text = "${response.feels_like.morn.roundToInt()}°С"
            binding.tvDayTempFL.text = "${response.feels_like.day.roundToInt()}°С"
            binding.tvEveTempFL.text = "${response.feels_like.eve.roundToInt()}°С"
            binding.tvNightTempFL.text = "${response.feels_like.night.roundToInt()}°С"
        }

        private fun bindSunriseSunsetBlock(response: Daily) {
            Glide.with(binding.root).load(SUN_IMG_URL).into(binding.ivIconSun)
            Glide.with(binding.root).load(MOON_IMG_URL).into(binding.ivIconMoon)
            binding.tvSunRiseTime.text = response.sunrise.unixTimestampToHoursMinutesTimeString()
            binding.tvSunSetTime.text = response.sunset.unixTimestampToHoursMinutesTimeString()
            binding.tvMoonRiseTime.text = response.moonrise.unixTimestampToHoursMinutesTimeString()
            binding.tvMoonSetTime.text = response.moonset.unixTimestampToHoursMinutesTimeString()
            // Log.d(TAG, (response.sunset - response.sunrise).unixTimestampToHoursMinutesTimeString())
            dateMinusDate(response.sunrise, response.sunset)
            binding.tvDurationOfDay.text = dateMinusDate(response.sunrise, response.sunset)
            binding.tvDurationOfNight.text = dateMinusDate(response.moonrise, response.moonset)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailFutureAdapter.ForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RcvDetailDailyForecastBinding.inflate(inflater, parent, false)
        return ForecastViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: DetailFutureAdapter.ForecastViewHolder, position: Int) {
        holder.updateRCV(differ.currentList[position])
    }

    private fun getDay(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            else -> "Saturday"
        }
    }

    private fun getDate(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = calendar.get(Calendar.MONTH).toString()
        return "$day.$month"
    }

    private fun getWindInfo(windDeg: Int, windSpeed: Double): String {
        val listOfDirections = listOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N")
        val number = (windDeg / 22.5).roundToInt() + 1
        return "${listOfDirections[number - 1]} $windSpeed km/h"
    }

    private fun dateMinusDate(firstMillis: Int, secondMillis: Int): String {
        val calendarFirst = Calendar.getInstance()
        calendarFirst.timeInMillis = firstMillis * 1000.toLong()
        val calendarFirstHours = calendarFirst.get(Calendar.HOUR_OF_DAY)
        val calendarFirstMinutes = calendarFirst.get(Calendar.MINUTE)
        Log.d(TAG, "first ${calendarFirst.get(Calendar.HOUR_OF_DAY)}:${calendarFirst.get(Calendar.MINUTE)}")
        val calendarSecond = Calendar.getInstance()
        calendarSecond.timeInMillis = secondMillis * 1000.toLong()
        val calendarSecondHours = calendarSecond.get(Calendar.HOUR_OF_DAY)
        val calendarSecondMinutes = calendarSecond.get(Calendar.MINUTE)
        Log.d(TAG, "second ${calendarFirst.get(Calendar.HOUR_OF_DAY)}:${calendarFirst.get(Calendar.MINUTE)}")
        val result = calendarSecond
        result.set(Calendar.HOUR_OF_DAY, calendarSecondHours - calendarFirstHours)
        result.set(Calendar.MINUTE, calendarSecondMinutes - calendarFirstMinutes)
        Log.d(TAG, "result ${result.get(Calendar.HOUR_OF_DAY)}:${result.get(Calendar.MINUTE)}")
        return "${result.get(Calendar.HOUR_OF_DAY)}h:${result.get(Calendar.MINUTE)}m"
    }
}
