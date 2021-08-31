package com.example.weather_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather_app.models.WeatherForecastResponse

@Database(
    entities = [WeatherForecastResponse::class],
    version = 1
)

abstract class WeatherForecastDatabase : RoomDatabase() {

    abstract fun getWeatherForecastDao(): WeatherForecastDao

    companion object {
        @Volatile
        private var instance: WeatherForecastDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
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
