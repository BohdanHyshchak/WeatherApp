package com.example.weather_app.models

data class Sys(
    var country: String,
    var id: Int,
    var sunrise: Int,
    var sunset: Int,
    var type: Int
) {
    constructor(): this("", 0, 0, 0,0)
}