package com.example.weather_app.di

import android.content.Context
import androidx.room.Room
import com.example.weather_app.db.WeatherForecastDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideForecastDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        WeatherForecastDatabase::class.java,
        "weather_forecast_db"
    ).build()

    @Singleton
    @Provides
    fun provideForecastDao(db: WeatherForecastDatabase) = db.getWeatherForecastDao()
}