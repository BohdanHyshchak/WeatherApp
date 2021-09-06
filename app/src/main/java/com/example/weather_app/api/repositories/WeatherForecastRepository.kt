package com.example.weather_app.api.repositories

import com.example.weather_app.api.RetrofitInstance
import com.example.weather_app.db.WeatherForecastDatabase

class WeatherForecastRepository(
    val db: WeatherForecastDatabase
) {

    suspend fun getWeatherForecast(nameOfCity: String) =
        RetrofitInstance.api.getWeatherForecast(nameOfCity)
}
