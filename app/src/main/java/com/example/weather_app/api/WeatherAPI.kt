package com.example.weather_app.api

import com.example.weather_app.models.WeatherForecastResponse
import com.example.weather_app.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("/weather")
    suspend fun getWeatherForecast(
        @Query("q")
        nameOfCityQuery: String,
        @Query("appid")
        apiKey: String = API_KEY
    ): Response<WeatherForecastResponse>
}