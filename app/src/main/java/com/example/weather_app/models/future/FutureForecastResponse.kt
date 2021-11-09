package com.example.weather_app.models.future

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weather_app.models.current.*
import com.example.weather_app.models.future.Weather

const val FUTURE_WEATHER_FORECAST_ID = 1
@Entity(
    tableName = "future_weather_forecast"
)
data class FutureForecastResponse(
    @TypeConverters
    val daily: List<Daily>,
    @TypeConverters
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
) {
    constructor() : this(
        listOf(
            Daily(
                0, 0.0, 0, FeelsLike(0.0, 0.0, 0.0, 0.0), 0,
                0.0, 0, 0, 0.0, 0, 0.0, 0.0, 0, 0,
                Temp(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), 0.0,
                listOf(Weather("", "", 0, "")), 0, 0.0, 0.0
            )
        ),
        listOf(
            Hourly(
                0, 0.0, 0, 0.0, 0, 0.0, 0, 0.0, 0.0, 0,
                listOf(WeatherX("", "", 0, "")), 0, 0.0, 0.0
            )
        ),
        0.0, 0.0, "", 0
    )
    @PrimaryKey(autoGenerate = false)
    var idOfFutureResponse: Int = FUTURE_WEATHER_FORECAST_ID
}
