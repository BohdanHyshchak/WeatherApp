package com.example.weather_app.models

data class Wind(
    val deg: Int,
    val speed: Double,
    val gust: Double
) {
    constructor(): this(0, 0.0, 0.0)
}
