package com.example.weather_app.api.repositories

import com.example.weather_app.api.CurrentRetrofitInstance
import com.example.weather_app.db.WeatherForecastDatabase

class WeatherForecastRepository(
    val db: WeatherForecastDatabase
) {

    suspend fun getWeatherForecast(nameOfCity: String) =
        CurrentRetrofitInstance.api.getWeatherForecast(nameOfCity)
}
