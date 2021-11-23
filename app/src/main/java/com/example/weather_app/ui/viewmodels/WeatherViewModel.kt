package com.example.weather_app.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_app.WeatherForecastApplication
import com.example.weather_app.api.FutureRetrofitInstance
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository,
    app: Application
) : AndroidViewModel(app) {

    val weatherForecast: MutableLiveData<Resource<WeatherForecastResponse>> = MutableLiveData()
    val futureWeatherForecast: MutableLiveData<Resource<FutureForecastResponse>> = MutableLiveData()
    val internetConnection: MutableLiveData<Boolean> = MutableLiveData()

    val prefs = app.getSharedPreferences("Main View Model", Context.MODE_PRIVATE)
    val HAS_DB_CREATED = "hasDbCreated"
    var isDbCreated = false

    init {
        // getWeatherForecast("Kyiv")
        isDbCreated = prefs.getBoolean(HAS_DB_CREATED, false) // checking if db has created already
        safeWeatherForecastResponse()
        testingLifecycle()
    }
    private fun testingLifecycle() = viewModelScope.launch {
        var count = 1
        Log.d("KEK", "KEK")
        while (true) {
            Log.d("WeatherViewModel", count.toString())
            count++
            delay(1000L)
        }
    }

    private suspend fun insertCurrentData(currentResponse: WeatherForecastResponse) {
        weatherForecastRepository.weatherDao.upsertCurrent(currentResponse)
    }

    private suspend fun insertFutureData(futureResponse: FutureForecastResponse) {
        weatherForecastRepository.weatherDao.upsertFuture(futureResponse)
    }

    suspend fun getWeatherForecastFromAPI(nameOfCity: String) {
        weatherForecast.postValue(Resource.Loading())
        val currentResponse = weatherForecastRepository.getWeatherForecast(nameOfCity)
        weatherForecast.postValue(handleWeatherForecastResponse(currentResponse))
        if (currentResponse.isSuccessful) {
            getFutureForecastResponse(currentResponse)
        }
    }

    private suspend fun getWeatherForecastFromDB() {
        val dbResponse = weatherForecastRepository.weatherDao.getCurrentWeatherForecast()
        weatherForecast.postValue(handleWeatherForecastFromDB(dbResponse))
    }

    private suspend fun handleWeatherForecastResponse(response: Response<WeatherForecastResponse>): Resource<WeatherForecastResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                insertCurrentData(it)
                prefs.edit().putBoolean(HAS_DB_CREATED, true).apply()
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleWeatherForecastFromDB(response: WeatherForecastResponse): Resource<WeatherForecastResponse> {
        return Resource.Success(response)
        // return Resource.Error("Database is empty :(")
    }

    fun safeWeatherForecastResponse() = viewModelScope.launch {
        if (hasInternetConnection()) {
            if (isDbCreated == true) {
                val nameOfCityFromDB = weatherForecastRepository.weatherDao.getCurrentWeatherForecast().name
                getWeatherForecastFromAPI(nameOfCityFromDB)
            } else {
                getWeatherForecastFromAPI("Kyiv")
            }
        } else {
            getWeatherForecastFromDB()
            getFutureForecastFromDB()
        }
    }

    private suspend fun getFutureForecastResponse(currentResponse: Response<WeatherForecastResponse>) {
        val latitude = currentResponse.body()!!.coord.lat
        val longitude = currentResponse.body()!!.coord.lon
        val futureResponse = FutureRetrofitInstance.api.getFutureForecast(latitude, longitude)
        futureWeatherForecast.postValue(handleFutureWeatherForecastResponse(futureResponse))
    }

    private suspend fun getFutureForecastFromDB() {
        val dbResponse = weatherForecastRepository.weatherDao.getFutureWeatherForecast()
        futureWeatherForecast.postValue(handleFutureForecastFromDB(dbResponse))
    }

    private suspend fun handleFutureWeatherForecastResponse(futureResponse: Response<FutureForecastResponse>): Resource<FutureForecastResponse> {
        if (futureResponse.isSuccessful) {
            futureResponse.body()?.let {
                insertFutureData(it)
                return Resource.Success(it)
            }
        }
        return Resource.Error("FutureResponse is wrong")
    }

    private fun handleFutureForecastFromDB(futureResponse: FutureForecastResponse): Resource<FutureForecastResponse> {
        // Log.d("WeatherViewModel", "${futureResponse.lat}")
        return Resource.Success(futureResponse)
        // return Resource.Error("Database is empty :(")
    }

    fun hasInternetConnection(): Boolean {
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
