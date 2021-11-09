package com.example.weather_app.api

import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrentWeatherAPI {
    @GET("data/2.5/weather")
    suspend fun getWeatherForecast(
        @Query("q")
        nameOfCityQuery: String,
        @Query("units")
        units: String = "metric",
        @Query("appid")
        apiKey: String = API_KEY
    ): Response<WeatherForecastResponse>
}