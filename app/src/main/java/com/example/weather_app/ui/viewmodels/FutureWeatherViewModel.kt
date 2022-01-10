package com.example.weather_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.utils.Resource
import com.example.weather_app.utils.unixTimestampToDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@HiltViewModel
class FutureWeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository,
) : ViewModel() {

    private val listOfDayForButtons = mutableListOf<String>()

    private val _isProgressBarShown = MutableLiveData<Boolean>()

    private val _nextButtonTitle = MutableLiveData<String?>()
    private val _backButtonTitle = MutableLiveData<String?>()
    val nextButtonTitle: LiveData<String?> = _nextButtonTitle
    val backButtonTitle: LiveData<String?> = _backButtonTitle

    private val _futureWeatherForecast = weatherForecastRepository.getFutureWeatherForecastFromDB()
    val futureWeatherForecast = _futureWeatherForecast.mapNotNull {
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
                saveDatesForButtons(it.data!!)
                it.data
            }
        }
    }.asLiveData(Dispatchers.IO)

    private fun saveDatesForButtons(response: FutureForecastResponse) {
        listOfDayForButtons.clear()
        for (i in response.daily) {
            listOfDayForButtons.add(i.dt.unixTimestampToDateString("EEEE"))
        }
    }

    fun sendViewPagerPosition(currentPosition: Int) {
        _nextButtonTitle.postValue(listOfDayForButtons.getOrNull(currentPosition + 1))
        _backButtonTitle.postValue(listOfDayForButtons.getOrNull(currentPosition - 1))
    }
}
