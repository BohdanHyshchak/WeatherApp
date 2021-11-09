package com.example.weather_app.models.future

import androidx.room.Embedded
import androidx.room.TypeConverters

data class Hourly(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    @TypeConverters
    val weather: List<WeatherX>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
) {
    constructor() : this(
        0, 0.0, 0, 0.0, 0, 0.0, 0, 0.0, 0.0, 0,
        listOf(WeatherX("", "", 0, "")), 0, 0.0, 0.0
    )
}
