package com.example.weather_app.ui.current

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.api.repositories.WeatherForecastRepository

class WeatherViewModelProviderFactory(
    private val weatherForecastRepository: WeatherForecastRepository,
    private val app: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(weatherForecastRepository, app) as T
    }
}
