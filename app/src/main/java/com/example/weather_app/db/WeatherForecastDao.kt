package com.example.weather_app.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_app.models.WEATHER_FORECAST_ID
import com.example.weather_app.models.WeatherForecastResponse

@Dao
interface WeatherForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(weatherForecast: WeatherForecastResponse)

    @Query("select * from weather_forecast where idOfResponse = $WEATHER_FORECAST_ID")
    fun getWeatherForecast(): LiveData<WeatherForecastResponse>
}
