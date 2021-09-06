package com.example.weather_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather_app.models.WeatherForecastResponse
import com.example.weather_app.ui.CurrentWeather.WeatherFragment

@Database(
    entities = [WeatherForecastResponse::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)

abstract class WeatherForecastDatabase : RoomDatabase() {

    abstract fun getWeatherForecastDao(): WeatherForecastDao

    companion object {
        @Volatile
        private var instance: WeatherForecastDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: WeatherFragment) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WeatherForecastDatabase::class.java,
                "weather_forecast_db.db"
            ).build()
    }
}
