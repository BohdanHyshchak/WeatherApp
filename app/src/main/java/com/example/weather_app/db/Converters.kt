package com.example.weather_app.db

import androidx.room.TypeConverter
import com.example.weather_app.models.current.Weather
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
}
