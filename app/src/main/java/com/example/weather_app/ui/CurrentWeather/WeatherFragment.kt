package com.example.weather_app.ui.CurrentWeather

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weather_app.R
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.databinding.WeatherFragmentBinding
import com.example.weather_app.db.WeatherForecastDatabase
import com.example.weather_app.models.WeatherForecastResponse
import com.example.weather_app.utils.Resource

class WeatherFragment : Fragment() {

    val TAG = "Weather Fragment"
    private lateinit var binding: WeatherFragmentBinding
    lateinit var viewModel: WeatherViewModel

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
        val weatherForecastRepository = WeatherForecastRepository(WeatherForecastDatabase(this))
        val viewModelFactory = WeatherViewModelProviderFactory(weatherForecastRepository, this.requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)
        setUI()
        //testCheckForConnection()
        return binding.root
    }

//    private fun testCheckForConnection() {
//        viewModel.internetConnection.observeForever {
//            if (it) {
//                binding.tvStateOfSky.text = "Available"
//            } else {
//                binding.tvStateOfSky.text = "Unavailable"
//            }
//        }
//    }

    private fun setUI() {
        viewModel.weatherForecast.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar(binding)
                        response.data?.let {
                            bindViews(it, binding, context)
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar(binding)
                    }
                    is Resource.Error -> {
                        hideProgressBar(binding)
                        response.message?.let { message ->
                            Log.e(TAG, "An error occured: $message")
                        }
                    }
                }
            }
        )
    }
}

private fun bindViews(response: WeatherForecastResponse, binding: WeatherFragmentBinding, context: Context?) {
    binding.tvNameOfCity.text = response.name
    binding.tvCountryCode.text = response.sys.country
    binding.tvTemperature.text = "${response.main.temp}°С"
    binding.tvTemperatureFeelsLike.text = "Feels like ${response.main.feels_like}°С"
    binding.tvStateOfSky.text = "${response.weather[0].description}"
    Glide.with(context!!).load("http://openweathermap.org/img/w/${response.weather[0].icon}.png").into(binding.ivTestImage)
}

private fun hideProgressBar(binding: WeatherFragmentBinding) {
    binding.progressBar.visibility = View.INVISIBLE
}

private fun showProgressBar(binding: WeatherFragmentBinding) {
    binding.progressBar.visibility = View.VISIBLE
}

// lifecycleScope.launchWhenCreated {
//    val response = try {
//        RetrofitInstance.api.getWeatherForecast("Kryvyi Rih")
//    } catch (e: IOException) {
//        Log.e(TAG, "IOException")
//        return@launchWhenCreated
//    } catch (e: HttpException) {
//        Log.e(TAG, "HttpException")
//        return@launchWhenCreated
//    }
//    if (response.isSuccessful && response.body() != null) {
//        binding.tvNameOfCity.text = response.body()!!.name
//        Glide.with(this@WeatherFragment).load("http://openweathermap.org/img/w/${response.body()!!.weather[0].icon}.png").into(binding.ivTestImage)
//    } else {
//        Log.e(TAG, "Response was not successful")
//    }
// }
