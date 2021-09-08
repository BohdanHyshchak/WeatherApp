package com.example.weather_app.ui.CurrentWeather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.models.WeatherForecastResponse
import com.example.weather_app.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    val weatherForecast: MutableLiveData<Resource<WeatherForecastResponse>> = MutableLiveData()

    init {
        getWeatherForecast("Kyiv")
    }

    fun getWeatherForecast(nameOfCity: String) = viewModelScope.launch {
        weatherForecast.postValue(Resource.Loading())
        val response = weatherForecastRepository.getWeatherForecast(nameOfCity)
        weatherForecast.postValue(handleWeatherForecastResponse(response))
    }

    private fun handleWeatherForecastResponse(response: Response<WeatherForecastResponse>): Resource<WeatherForecastResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
}
