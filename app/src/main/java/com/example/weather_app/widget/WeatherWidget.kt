package com.example.weather_app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weather_app.R
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.ui.MainActivity
import com.example.weather_app.utils.unixTimestampToDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
@InternalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.S)
class WeatherWidget : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var mainRepository: WeatherForecastRepository
    private var isListShown = false

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG", "onReceive")
        super.onReceive(context, intent)

        loadData(context, intent)
    }

//    override fun onUpdate(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetIds: IntArray
//    ) {
//        // Update each of the widgets with the remote adapter.
//        appWidgetIds.forEach { appWidgetId ->
//
//            val listOfDays = mutableListOf<String>()
//            val listOfImages = mutableListOf<String>()
//            val listOfTemp = mutableListOf<String>()
//            coroutineScope.launch {
//                mainRepository.getFutureWeatherForecastFromDB().collectLatest { futureResponse ->
//                    val currentResponse = mainRepository.getWeatherForecastFromDB()
//                    for (i in 1..5) {
//                        listOfDays.add(futureResponse.daily[i].dt.unixTimestampToDateString("EEEE"))
//                        listOfImages.add(futureResponse.daily[i].weather[0].icon)
//                        listOfTemp.add("${futureResponse.daily[i].temp.day.roundToInt()}°   ${futureResponse.daily[i].temp.night.roundToInt()}°")
//                    }
//                    val intent = Intent(context, WeatherWidgetService::class.java).apply {
//                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//                        putExtra("days", listOfDays.toTypedArray())
//                        putExtra("images", listOfImages.toTypedArray())
//                        putExtra("temps", listOfTemp.toTypedArray())
//                        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
//                    }
//
//                    val views = RemoteViews(context.packageName, R.layout.weather_widget).apply {
//                        // Set up the RemoteViews object to use a RemoteViews adapter.
//                        // This adapter connects to a RemoteViewsService through the
//                        // specified intent.
//                        // This is how you populate the data.
//                        setRemoteAdapter(R.id.listViewWidget, intent)
//                    }
//                    appWidgetManager.updateAppWidget(appWidgetId, views)
//                }
//            }
//        }
//        super.onUpdate(context, appWidgetManager, appWidgetIds)
//    }

    private fun loadData(context: Context?, intent: Intent?) {
        coroutineScope.launch {
            mainRepository.getFutureWeatherForecastFromDB().collectLatest { futureResponse ->
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
                val currentResponse = mainRepository.getWeatherForecastFromDB()
                for (appWidgetId in ids) {
                    context?.let {
                        updateAppWidget(
                            it,
                            appWidgetManager,
                            appWidgetId,
                            currentResponse.first(),
                            futureResponse,
                        )
                    }
                }
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        val views = RemoteViews(context?.packageName, R.layout.weather_widget)
        val minWidth = newOptions?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        if (minWidth != null) {
            if (minWidth < 300) {
                isListShown = false
                views.setViewVisibility(R.id.listViewWidget, View.INVISIBLE)
            } else {
                isListShown = true
                views.setViewVisibility(R.id.listViewWidget, View.VISIBLE)
            }
        }
        appWidgetManager?.updateAppWidget(appWidgetId, views)
        appWidgetManager?.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget)
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
    currentResponse: WeatherForecastResponse,
    futureResponse: FutureForecastResponse,
) {
    Log.d("TAG", "updateAppWidget")
    val intent = Intent(context, WeatherWidgetService::class.java).apply {
        // Add the widget ID to the intent extras.
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        putExtra("test", "testString")
        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
    }
    val currentHour = futureResponse.hourly[0]
    val listOfDays = mutableListOf<String>()
    val listOfImages = mutableListOf<String>()
    val listOfTemp = mutableListOf<String>()
    for (i in 1..5) {
        listOfDays.add(futureResponse.daily[i].dt.unixTimestampToDateString("EEEE"))
        listOfImages.add(futureResponse.daily[i].weather[0].icon)
        listOfTemp.add("${futureResponse.daily[i].temp.day.roundToInt()}°   ${futureResponse.daily[i].temp.night.roundToInt()}°")
    }
    val views = RemoteViews(context.packageName, R.layout.weather_widget).apply {
        setTextViewText(R.id.tvCityWidget, currentResponse.name)
        setTextViewText(R.id.tvTemperatureWidget, "${currentResponse.main.temp.roundToInt()}°С")
        setTextViewText(R.id.tvCloudsDescriptionWidget, "${futureResponse.daily[0].clouds} %")
        setTextViewText(R.id.tvHumidityDescriptionWidget, currentHour.humidity.toString() + "%")
        setTextViewText(R.id.tvPressureDescriptionWidget, currentHour.pressure.toString() + " mb")
        intent.putExtra("days", listOfDays.toTypedArray())
        intent.putExtra("images", listOfImages.toTypedArray())
        intent.putExtra("temps", listOfTemp.toTypedArray())

        setRemoteAdapter(R.id.listViewWidget, intent)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget)
    }

    val awt: AppWidgetTarget = object :
        AppWidgetTarget(context.applicationContext, R.id.ivIconWidget, views, appWidgetId) {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            super.onResourceReady(resource, transition)
        }
    }
    val options = RequestOptions().override(30, 30).placeholder(R.drawable.ic_launcher_background)
        .error(R.drawable.ic_launcher_background)
    Glide.with(context.applicationContext)
        .asBitmap()
        .load("http://openweathermap.org/img/w/${currentResponse.weather[0].icon}.png")
        .skipMemoryCache(true)
        .apply(options)
        .into(awt)

    views.setOnClickPendingIntent(R.id.weatherWidget, getPendingIntentActivity(context))
    // Instruct the widget manager to update the widget
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun getPendingIntentActivity(context: Context): PendingIntent {
    // Construct an Intent which is pointing this class.
    val intent = Intent(context, MainActivity::class.java)
    // And this time we are sending a broadcast with getBroadcast
    return PendingIntent.getActivity(context, 0, intent, 0)
}
