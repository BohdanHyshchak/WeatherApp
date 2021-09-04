package com.example.weather_app.models

data class Coord(
    val lat: Double,
    val lon: Double
) {
    constructor(): this(0.0, 0.0)
}