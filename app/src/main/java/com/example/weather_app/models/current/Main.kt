package com.example.weather_app.models.current

data class Main(
    var feels_like: Double,
    var humidity: Int,
    var pressure: Int,
    var temp: Double,
    var temp_max: Double,
    var temp_min: Double
) {
    constructor(): this(0.0, 0, 0, 0.0, 0.0, 0.0)
}