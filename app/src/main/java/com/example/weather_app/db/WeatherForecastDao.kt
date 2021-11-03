package com.example.weather_app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_app.models.current.WEATHER_FORECAST_ID
import com.example.weather_app.models.current.WeatherForecastResponse

@Dao
interface WeatherForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(weatherForecast: WeatherForecastResponse)

    @Query("select * from weather_forecast where idOfResponse = $WEATHER_FORECAST_ID")
    suspend fun getWeatherForecast(): WeatherForecastResponse
}
