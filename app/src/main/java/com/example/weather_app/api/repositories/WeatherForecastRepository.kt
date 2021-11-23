package com.example.weather_app.api.repositories

import com.example.weather_app.api.CurrentRetrofitInstance
import com.example.weather_app.db.WeatherForecastDao
import javax.inject.Inject

class WeatherForecastRepository @Inject constructor(
    val weatherDao: WeatherForecastDao
) {

    suspend fun getWeatherForecast(nameOfCity: String) =
        CurrentRetrofitInstance.api.getWeatherForecast(nameOfCity)
}
