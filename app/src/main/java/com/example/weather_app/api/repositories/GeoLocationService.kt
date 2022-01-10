package com.example.weather_app.api.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.weather_app.utils.WeatherUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GeoLocationService @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext context: Context
) {

    private val hasPermission = WeatherUtility.hasLocationPermissions(context)

    @SuppressLint("MissingPermission")
    fun getGetGeoResponse(): LatLng? {
        Log.d("HELP", "getGeoResponse")
        var latLng: LatLng? = null
        if (hasPermission) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("HELP", "getGeoResponse success")
                    latLng = LatLng(location.latitude, location.longitude)
                } else {
                    Log.d("HELP", "getGeoResponse error")
                    Log.d("GeoLocationService", "Smth went wrong")
                    latLng = null
                }
            }
        } else {
            Log.d("GeoLocationService", "No permission")
            latLng = null
        }
        return latLng
    }
}
