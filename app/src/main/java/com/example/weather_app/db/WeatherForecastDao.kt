package com.example.weather_app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_app.models.current.WEATHER_FORECAST_ID
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.models.future.FUTURE_WEATHER_FORECAST_ID
import com.example.weather_app.models.future.FutureForecastResponse

@Dao
interface WeatherForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCurrent(weatherForecast: WeatherForecastResponse)

    @Query("select * from weather_forecast where idOfResponse = $WEATHER_FORECAST_ID")
    suspend fun getCurrentWeatherForecast(): WeatherForecastResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFuture(futureForecastResponse: FutureForecastResponse)

    @Query("select * from future_weather_forecast where idOfFutureResponse = $FUTURE_WEATHER_FORECAST_ID")
    suspend fun getFutureWeatherForecast(): FutureForecastResponse
}
