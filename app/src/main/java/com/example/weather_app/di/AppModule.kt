package com.example.weather_app.di

import android.content.Context
import androidx.room.Room
import com.example.weather_app.api.WeatherService
import com.example.weather_app.db.WeatherForecastDatabase
import com.example.weather_app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Todo: remanage packages
    @Singleton
    @Provides
    fun provideForecastDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        WeatherForecastDatabase::class.java,
        "weather_forecast_db"
    ).build()

    @Provides
    fun provideForecastDao(db: WeatherForecastDatabase) = db.getWeatherForecastDao()

    @Singleton
    @Provides
    fun provideWeatherClient(): Retrofit {
        val logging = HttpLoggingInterceptor()

        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val interceptor = Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().url(
                    chain.request().url.newBuilder()
                        .addQueryParameter("appid", Constants.API_KEY).build()
                ).build()
            )
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(interceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun provideWeatherService(retrofit: Retrofit): WeatherService = retrofit.create()

    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context) = context.getSharedPreferences("NAME_OF_CITY", Context.MODE_PRIVATE)
}
