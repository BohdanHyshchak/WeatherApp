package com.example.weather_app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.R
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.db.WeatherForecastDatabase
import com.example.weather_app.ui.CurrentWeather.WeatherViewModel
import com.example.weather_app.ui.CurrentWeather.WeatherViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: WeatherViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




    }
}
