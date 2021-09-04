package com.example.weather_app.models

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
) {
    constructor(): this("", 0, 0, 0,0)
}