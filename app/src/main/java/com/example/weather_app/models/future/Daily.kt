package com.example.weather_app.models.future

import androidx.room.Embedded
import androidx.room.TypeConverters

data class Daily(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    @Embedded(prefix = "feelslike_")
    val feels_like: FeelsLike,
    val humidity: Int,
    val moon_phase: Double,
    val moonrise: Int,
    val moonset: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Double,
    val snow: Double,
    val sunrise: Int,
    val sunset: Int,
    @Embedded(prefix = "temp_")
    val temp: Temp,
    val uvi: Double,
    @TypeConverters
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
) {
    constructor() : this(
        0, 0.0, 0, FeelsLike(0.0, 0.0, 0.0, 0.0), 0,
        0.0, 0, 0, 0.0, 0, 0.0, 0.0, 0, 0,
        Temp(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), 0.0, listOf(Weather("", "", 0, "")),
        0, 0.0, 0.0
    )
}
