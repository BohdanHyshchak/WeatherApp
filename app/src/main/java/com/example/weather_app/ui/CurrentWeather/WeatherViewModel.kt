package com.example.weather_app.ui.CurrentWeather

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_app.WeatherForecastApplication
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.models.WeatherForecastResponse
import com.example.weather_app.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(
    private val weatherForecastRepository: WeatherForecastRepository,
    app: Application
) : AndroidViewModel(app) {

    val weatherForecast: MutableLiveData<Resource<WeatherForecastResponse>> = MutableLiveData()
    val internetConnection: MutableLiveData<Boolean> = MutableLiveData()

    init {
        // getWeatherForecast("Kyiv")
        safeWeatherForecastResponse("Kyiv")
    }

    private suspend fun insertData(response: WeatherForecastResponse) {
        weatherForecastRepository.db.getWeatherForecastDao().upsert(response)
    }

    private suspend fun getWeatherForecastFromAPI(nameOfCity: String) {
        weatherForecast.postValue(Resource.Loading())
        val response = weatherForecastRepository.getWeatherForecast(nameOfCity)
        weatherForecast.postValue(handleWeatherForecastResponse(response))
    }

    private suspend fun getWeatherForecastFromDB() {
        val dbResponse = weatherForecastRepository.db.getWeatherForecastDao().getWeatherForecast()
        weatherForecast.postValue(handleWeatherForecastFromDB(dbResponse))
    }

    private suspend fun handleWeatherForecastResponse(response: Response<WeatherForecastResponse>): Resource<WeatherForecastResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                insertData(it)
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleWeatherForecastFromDB(response: WeatherForecastResponse): Resource<WeatherForecastResponse> {
        return Resource.Success(response)
        // return Resource.Error("Database is empty :(")
    }

    private fun safeWeatherForecastResponse(nameOfCity: String) = viewModelScope.launch {
        if (hasInternetConnection())
            getWeatherForecastFromAPI(nameOfCity)
        else
            getWeatherForecastFromDB()
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<WeatherForecastApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

//    fun checkForConnection() = viewModelScope.launch {
//        while (true) {
//            val isInternetConnection = hasInternetConnection()
//            internetConnection.postValue(isInternetConnection)
//            delay(5000L)
//        }
//    }
}
