package com.example.weather_app.models.current

data class Coord(
    var lat: Double,
    var lon: Double
) {
    constructor(): this(0.0, 0.0)
}