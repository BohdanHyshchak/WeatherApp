package com.example.weather_app.db

import androidx.room.TypeConverter
import com.example.weather_app.models.current.Weather
import com.example.weather_app.models.future.Daily
import com.example.weather_app.models.future.Hourly
import com.example.weather_app.models.future.WeatherX
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    val gson = Gson()

    @TypeConverter
    fun arrayListToJson(list: List<Weather>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun jsonToArrayList(jsonData: String?): List<Weather>? {
        return if (jsonData == null) null else gson.fromJson(jsonData, object : TypeToken<List<Weather>?>() {}.type)
    }

    @TypeConverter
    fun dayListToJson(list: List<Daily>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun jsonToDayList(jsonData: String?): List<Daily>? {
        return if (jsonData == null) null else gson.fromJson(jsonData, object : TypeToken<List<Daily>?>() {}.type)
    }

    @TypeConverter
    fun hourlyListToJson(list: List<Hourly>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun jsonToHourlyList(jsonData: String?): List<Hourly>? {
        return if (jsonData == null) null else gson.fromJson(jsonData, object : TypeToken<List<Hourly>?>() {}.type)
    }

    @TypeConverter
    fun weatherXListToJson(list: List<WeatherX>?): String? {
        return if (list == null) null else gson.toJson(list)
    }

    @TypeConverter
    fun jsonToWeatherXList(jsonData: String?): List<WeatherX>? {
        return if (jsonData == null) null else gson.fromJson(jsonData, object : TypeToken<List<WeatherX>?>() {}.type)
    }
}
