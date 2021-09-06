package com.example.weather_app.ui.CurrentWeather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.api.repositories.WeatherForecastRepository

class WeatherViewModelProviderFactory(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherViewModel(weatherForecastRepository) as T
    }
}
