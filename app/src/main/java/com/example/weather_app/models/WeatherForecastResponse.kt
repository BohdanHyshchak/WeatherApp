package com.example.weather_app.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val WEATHER_FORECAST_ID = 0
@Entity(
    tableName = "weather_forecast"
)
data class WeatherForecastResponse(
    val base: String,
    @Embedded(prefix = "clouds_")
    val clouds: Clouds,
    val cod: Int,
    @Embedded(prefix = "coord_")
    val coord: Coord,
    val dt: Int,
    @Embedded(prefix = "main_")
    val main: Main,
    val name: String,
    @Embedded(prefix = "sys_")
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    @Embedded(prefix = "weather_")
    val weather: List<Weather>,
    @Embedded(prefix = "wind_")
    val wind: Wind
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = WEATHER_FORECAST_ID
}
