package com.example.weather_app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_app.models.current.WEATHER_FORECAST_ID
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.models.future.FUTURE_WEATHER_FORECAST_ID
import com.example.weather_app.models.future.FutureForecastResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(weatherForecast: WeatherForecastResponse)

    @Query("select * from weather_forecast where idOfResponse = $WEATHER_FORECAST_ID")
    fun getCurrentWeatherForecast(): Flow<WeatherForecastResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuture(futureForecastResponse: FutureForecastResponse)

    @Query("select * from future_weather_forecast where idOfFutureResponse = $FUTURE_WEATHER_FORECAST_ID")
    fun getFutureWeatherForecast(): Flow<FutureForecastResponse>
}
