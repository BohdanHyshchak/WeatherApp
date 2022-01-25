package com.example.weather_app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.utils.Constants
import com.example.weather_app.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Implementation of App Widget functionality.
 */
@AndroidEntryPoint
@InternalCoroutinesApi
class WeatherWidget : AppWidgetProvider() {

    private val job = SupervisorJob()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    @Inject
    lateinit var mainRepository: WeatherForecastRepository

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        coroutineScope.launch {
            mainRepository.getWeatherForecastFromDB().collectLatest { response ->
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val man = AppWidgetManager.getInstance(context)
                val ids = man.getAppWidgetIds(context?.let { ComponentName(it, WeatherWidget::class.java) })

                when (response) {
                    is Resource.Success -> {
                        val data = response.data!!
                        for (appWidgetId in ids) {
                            context?.let { updateAppWidget(it, appWidgetManager, appWidgetId, data) }
                        }
                    } else -> {}
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    data: WeatherForecastResponse
) {
    val views = RemoteViews(context.packageName, R.layout.weather_widget)
    views.setTextViewText(R.id.tvCityWidget, data.name)
    views.setTextViewText(R.id.tvTemperatureWidget, "${data.main.temp.roundToInt()}°С")

    val awt: AppWidgetTarget = object : AppWidgetTarget(context.applicationContext, R.id.ivIconWidget, views, appWidgetId) {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            super.onResourceReady(resource, transition)
        }
    }
    val options = RequestOptions().override(50, 50).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background)
    Glide.with(context.applicationContext)
        .asBitmap()
        .load("http://openweathermap.org/img/w/${data.weather[0].icon}.png")
        .skipMemoryCache(true)
        .apply(options)
        .into(awt)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
