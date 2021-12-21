package com.example.weather_app.models.current

data class Weather(
    var description: String,
    var icon: String,
    var id: Int,
    var main: String
) {
    constructor() : this("", "", 0, "")
}
