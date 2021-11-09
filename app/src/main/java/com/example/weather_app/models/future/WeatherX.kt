package com.example.weather_app.models.future

data class WeatherX(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
) {
    constructor() : this("", "", 0, "")
}
