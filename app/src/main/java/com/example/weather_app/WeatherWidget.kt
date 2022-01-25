package com.example.weather_app

import android.app.PendingIntent
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
import com.example.weather_app.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Implementation of App Widget functionality.
 */
@AndroidEntryPoint
@InternalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.S)
class WeatherWidget : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    @Inject
    lateinit var mainRepository: WeatherForecastRepository

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        loadData(context)
    }

    private fun loadData(context: Context?) {
        coroutineScope.launch {
            mainRepository.getWeatherForecastFromDB().collectLatest { response ->
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val man = AppWidgetManager.getInstance(context)
                val ids = man.getAppWidgetIds(
                    context?.let {
                        ComponentName(
                            it,
                            WeatherWidget::class.java
                        )
                    }
                )
                for (appWidgetId in ids) {
                    context?.let { updateAppWidget(it, appWidgetManager, appWidgetId, response) }
                }
            }
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        job.cancel()
    }
}

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
    val options = RequestOptions().override(35, 35).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background)
    Glide.with(context.applicationContext)
        .asBitmap()
        .load("http://openweathermap.org/img/w/${data.weather[0].icon}.png")
        .skipMemoryCache(true)
        .apply(options)
        .into(awt)

    views.setOnClickPendingIntent(R.id.weatherWidget, getPendingIntentActivity(context))
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun getPendingIntentActivity(context: Context): PendingIntent {
    // Construct an Intent which is pointing this class.
    val intent = Intent(context, MainActivity::class.java)
    // And this time we are sending a broadcast with getBroadcast
    return PendingIntent.getActivity(context, 0, intent, 0)
}
