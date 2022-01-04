package com.example.weather_app.ui.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.utils.Constants
import com.example.weather_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val nameOfCity = MutableStateFlow(prefs.getString(Constants.PREFS_FOR_CITY, null) ?: "Kyiv")
    fun setNameOfCity(nameOfCity: String) {
        this.nameOfCity.tryEmit(nameOfCity)
        prefs.edit().putString(Constants.PREFS_FOR_CITY, nameOfCity).apply()
    }

    private val _weatherForecast = nameOfCity.flatMapLatest { nameOfCity ->
        weatherForecastRepository.getWeatherForecast(nameOfCity).mapNotNull {
            when (it) {
                is Resource.Error -> {
                    _isProgressBarShown.postValue(false)
                    null
                }
                is Resource.Loading -> {
                    _isProgressBarShown.postValue(true)
                    null
                }
                is Resource.Success -> {
                    _isProgressBarShown.postValue(false)
                    it.data!!
                }
            }
        }
    }
    val weatherForecast = _weatherForecast.asLiveData(Dispatchers.IO)

    val futureWeatherForecast = _weatherForecast.flatMapLatest { weatherForecastResponse ->
        weatherForecastRepository.getFutureWeatherForecast(
            weatherForecastResponse.coord.lat,
            weatherForecastResponse.coord.lon
        ).mapNotNull {
            when (it) {
                is Resource.Error -> {
                    _isProgressBarShown.postValue(false)
                    null
                }
                is Resource.Loading -> {
                    _isProgressBarShown.postValue(true)
                    null
                }
                is Resource.Success -> {
                    _isProgressBarShown.postValue(false)
                    it.data!!
                }
            }
        }
    }.asLiveData(Dispatchers.IO)

    private val _isProgressBarShown = MutableLiveData<Boolean>()
    val isProgressBarShown: LiveData<Boolean> = _isProgressBarShown
}
