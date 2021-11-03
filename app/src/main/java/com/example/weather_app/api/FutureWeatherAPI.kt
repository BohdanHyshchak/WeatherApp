package com.example.weather_app.api

import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FutureWeatherAPI {
    @GET("data/2.5/onecall")
    suspend fun getFutureForecast(
        @Query("lat")
        latitude: Double = 50.4333,
        @Query("lon")
        longitude: Double = 30.5167,
        @Query("units")
        units: String = "metric",
        @Query("exclude")
        exclude: String = "current,minutely,alerts",
        @Query("appid")
        apiKey: String = Constants.API_KEY
    ): Response<FutureForecastResponse>
}