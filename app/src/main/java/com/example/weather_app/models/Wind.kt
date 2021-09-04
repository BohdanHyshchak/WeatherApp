package com.example.weather_app.models

data class Wind(
    var deg: Int,
    var speed: Double,
    var gust: Double
) {
    constructor(): this(0, 0.0, 0.0)
}
