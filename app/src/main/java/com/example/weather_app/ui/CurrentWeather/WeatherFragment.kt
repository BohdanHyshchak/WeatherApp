package com.example.weather_app.ui.CurrentWeather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.weather_app.R
import com.example.weather_app.api.RetrofitInstance
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.databinding.WeatherFragmentBinding
import com.example.weather_app.db.WeatherForecastDatabase
import retrofit2.HttpException
import java.io.IOException

class WeatherFragment : Fragment() {

    val TAG = "Weather Fragment"
    lateinit var binding: WeatherFragmentBinding
    lateinit var viewModel: WeatherViewModel
    lateinit var viewModelFactory: WeatherViewModelProviderFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.weather_fragment,
            container,
            false
        )

        val weatherForecastRepository = WeatherForecastRepository(WeatherForecastDatabase(this@WeatherFragment))
        viewModelFactory = WeatherViewModelProviderFactory(weatherForecastRepository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(WeatherViewModel::class.java)

        return binding.root
    }

    private fun testImage() {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.api.getWeatherForecast("Kryvyi Rih")
            } catch (e: IOException) {
                Log.e(TAG, "IOException")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException")
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                binding.tvTestTextView.text = response.body()!!.name
                Glide.with(this@WeatherFragment).load("http://openweathermap.org/img/w/${response.body()!!.weather[0].icon}.png").into(binding.ivTestImage)
            } else {
                Log.e(TAG, "Response was not successful")
            }
        }
    }
}
