package com.example.weather_app.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.example.weather_app.R

class WeatherWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WeatherWidgetServiceFactory(this.application, intent)
    }
}

class WeatherWidgetServiceFactory(
    private val context: Context,
    private val intent: Intent,
) : RemoteViewsService.RemoteViewsFactory {

    private var days = mutableListOf<String>()
    private var images = mutableListOf<String>()
    private var temperatures = mutableListOf<String>()

    override fun onCreate() {
        Log.d("TAG", "OnCreate")
        days = intent.getStringArrayExtra("days")?.toMutableList() ?: mutableListOf()
        images = intent.getStringArrayExtra("images")?.toMutableList() ?: mutableListOf()
        temperatures = intent.getStringArrayExtra("temps")?.toMutableList() ?: mutableListOf()
    }

    override fun onDataSetChanged() {
        Log.d("TAG", "onDataChanged")
        days.clear()
        images.clear()
        temperatures.clear()
        days = intent.getStringArrayExtra("days")?.toMutableList() ?: mutableListOf()
        images = intent.getStringArrayExtra("images")?.toMutableList() ?: mutableListOf()
        temperatures = intent.getStringArrayExtra("temps")?.toMutableList() ?: mutableListOf()
        Log.d("TAG", temperatures[0].toString())
    }

    override fun onDestroy() {
        Log.d("TAG", "OnDestroy")
        days.clear()
        images.clear()
        temperatures.clear()
    }

    override fun getCount(): Int {
        return days.count()
    }

    override fun getViewAt(position: Int): RemoteViews {
        Log.d("TAG", temperatures.get(position).toString())
        return RemoteViews(context.packageName, R.layout.list_view_item).apply {
            setTextViewText(R.id.tvNameOfDayListView, days?.get(position).toString())
            setTextViewText(R.id.tvTemperatureListView, temperatures?.get(position))
            val bitMap = Glide
                .with(context)
                .asBitmap()
                .load("http://openweathermap.org/img/w/${images?.get(position)}.png")
                .submit()
                .get()
            setImageViewBitmap(R.id.ivIconListView, bitMap)
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
