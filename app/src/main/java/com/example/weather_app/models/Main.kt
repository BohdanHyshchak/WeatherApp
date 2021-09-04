package com.example.weather_app.models

data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
) {
    constructor(): this(0.0, 0, 0, 0.0, 0.0, 0.0)
}