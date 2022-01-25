package com.example.weather_app.api.repositories

import android.util.Log
import com.example.weather_app.api.WeatherService
import com.example.weather_app.db.WeatherForecastDao
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.utils.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastRepository @Inject constructor(
    private val weatherDao: WeatherForecastDao,
    private val weatherService: WeatherService,
    private val networkConnectionService: NetworkConnectionService,
    private val geoLocationService: GeoLocationService,
) {

    fun getWeatherForecast(nameOfCity: String): Flow<Resource<WeatherForecastResponse>> =
        flow<Resource<WeatherForecastResponse>> {
            emit(Resource.Loading())
            if (networkConnectionService.hasInternetConnection()) {
                val weatherForecastResponse =
                    weatherService.getWeatherForecastWithCityName(nameOfCity)
                if (weatherForecastResponse.isSuccessful) {
                    emit(Resource.Success(weatherForecastResponse.body()!!))
                    weatherDao.insertCurrent(weatherForecastResponse.body()!!)
                } else {
                    emit(Resource.Error(weatherForecastResponse.message()))
                }
            } else {
                emit(Resource.Success(weatherDao.getCurrentWeatherForecast().first()))
                Log.d("TAG", weatherDao.getCurrentWeatherForecast().first().name)
            }
        }

    suspend fun saveCurrentWeatherForecast(response: WeatherForecastResponse) =
        weatherDao.insertCurrent(response)

    fun getWeatherForecastWithGeo(
        lat: Double,
        lon: Double
    ): Flow<Resource<WeatherForecastResponse>> =
        flow<Resource<WeatherForecastResponse>> {
            emit(Resource.Loading())
            if (networkConnectionService.hasInternetConnection()) {
                val weatherForecastResponse = weatherService.getWeatherForecastWithGeo(lat, lon)
                if (weatherForecastResponse.isSuccessful) {
                    emit(Resource.Success(weatherForecastResponse.body()!!))
                    saveCurrentWeatherForecast(weatherForecastResponse.body()!!)
                } else {
                    emit(Resource.Error(weatherForecastResponse.message()))
                }
            } else {
                emit(Resource.Success(weatherDao.getCurrentWeatherForecast().first()))
            }
        }

    fun getFutureWeatherForecast(lat: Double, lon: Double): Flow<Resource<FutureForecastResponse>> =
        flow<Resource<FutureForecastResponse>> {
            emit(Resource.Loading())
            if (networkConnectionService.hasInternetConnection()) {
                val weatherForecastResponse = weatherService.getFutureForecast(lat, lon)
                if (weatherForecastResponse.isSuccessful) {
                    emit(Resource.Success(weatherForecastResponse.body()!!))
                    weatherDao.insertFuture(weatherForecastResponse.body()!!)
                } else {
                    emit(Resource.Error(weatherForecastResponse.message()))
                }
            } else {
                emit(Resource.Success(weatherDao.getFutureWeatherForecast().first()))
            }
        }

    fun getWeatherForecastFromDB(): Flow<WeatherForecastResponse> {
        return weatherDao.getCurrentWeatherForecast()
    }

    fun getFutureWeatherForecastFromDB(): Flow<FutureForecastResponse> {
        return weatherDao.getFutureWeatherForecast()
    }

    fun getLongLat(): Flow<Resource<LatLng>> =
        flow<Resource<LatLng>> {
            Log.d("HELP", "getLongLat success")
            emit(Resource.Loading())
            val data = geoLocationService.getGetGeoResponse()
            if (data != null) {
                Log.d("HELP", "getLongLat success")
                emit(Resource.Success(data))
            } else {
                Log.d("HELP", "getLongLat null")
                emit(Resource.Error("data is null"))
            }
        }
}
