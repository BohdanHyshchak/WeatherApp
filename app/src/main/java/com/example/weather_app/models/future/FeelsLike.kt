package com.example.weather_app.models.future

data class FeelsLike(
    val day: Double,
    val eve: Double,
    val morn: Double,
    val night: Double
) {
    constructor() : this(0.0, 0.0, 0.0, 0.0)
}
