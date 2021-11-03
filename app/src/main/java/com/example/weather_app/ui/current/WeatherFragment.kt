package com.example.weather_app.ui.current

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.weather_app.R
import com.example.weather_app.api.FutureRetrofitInstance
import com.example.weather_app.api.repositories.WeatherForecastRepository
import com.example.weather_app.databinding.WeatherFragmentBinding
import com.example.weather_app.db.WeatherForecastDatabase
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.utils.Resource
import kotlin.math.roundToInt

class WeatherFragment : Fragment() {

    val TAG = "Weather Fragment"
    private lateinit var binding: WeatherFragmentBinding
    lateinit var viewModel: WeatherViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        binding.btnSearch.setOnClickListener {
            val searchText = binding.etSearch.text
            if (searchText != null) {
                if (viewModel.hasInternetConnection()) {
                    viewModel.safeWeatherForecastResponse(searchText.toString())
                } else {
                    Toast.makeText(requireContext(), "No Internet connection", Toast.LENGTH_SHORT).show()
                }
            }
            binding.etSearch.hideKeyboard()
            binding.etSearch.text.clear()
        }

//        val dateTime = LocalDateTime.now().atOffset(ZoneOffset.UTC)
//        val currentTime = LocalDateTime.now().toLocalTime()
//        binding.tvTime.text = dateTime.toString()
//        binding.tvDate.text = currentTime.toString()

        viewModel.weatherForecast.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                            bindViews(it)
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Log.e(TAG, "An error occured: $message")
                        }
                    }
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val response = FutureRetrofitInstance.api.getFutureForecast()
            if (response.isSuccessful && response.body().toString().isNotEmpty()) {
               // binding.tvTest.text = response.body().toString()
            }
        }

        // testCheckForConnection()
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
    }

    private fun bindViews(response: WeatherForecastResponse,) {
        binding.tvNameOfCity.text = response.name
        binding.tvCountryCode.text = response.sys.country
        binding.tvTemperature.text = "${(response.main.temp - 273.15).roundToInt()}°С"
        binding.tvTemperatureFeelsLike.text = "Feels like ${(response.main.feels_like - 273.15).roundToInt()}°С"
        binding.tvStateOfSky.text = response.weather[0].description
        binding.etSearch.text.toString()
        Glide.with(this).load("http://openweathermap.org/img/w/${response.weather[0].icon}.png").into(binding.ivTestImage)
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
