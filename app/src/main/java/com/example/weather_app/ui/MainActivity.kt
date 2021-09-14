package com.example.weather_app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_settings -> Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}
