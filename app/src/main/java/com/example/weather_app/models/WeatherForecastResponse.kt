package com.example.weather_app.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val WEATHER_FORECAST_ID = 0
@Entity(
    tableName = "weather_forecast"
)
data class WeatherForecastResponse(
    var base: String,
    @Embedded(prefix = "clouds_")
    var clouds: Clouds,
    var cod: Int,
    @Embedded(prefix = "coord_")
    var coord: Coord,
    var dt: Int,
    @Embedded(prefix = "main_")
    var main: Main,
    var name: String,
    @Embedded(prefix = "sys_")
    var sys: Sys,
    var timezone: Int,
    var visibility: Int,
    @Embedded(prefix = "weather_")
    var weather: List<Weather>,
    @Embedded(prefix = "wind_")
    var wind: Wind
) {
    constructor() : this(
        "",
        Clouds(0),
        0,
        Coord(0.0, 0.0),
        0,
        Main(0.0, 0, 0, 0.0, 0.0, 0.0),
        "",
        Sys("", 0, 0, 0, 0),
        0,
        0,
        listOf(Weather("", "", 0, "")),
        Wind(0, 0.0, 0.0)
    )
    @PrimaryKey(autoGenerate = false)
    var id: Int = WEATHER_FORECAST_ID
}
