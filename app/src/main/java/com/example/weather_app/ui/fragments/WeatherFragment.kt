package com.example.weather_app.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weather_app.R
import com.example.weather_app.adapters.FutureWeatherAdapter
import com.example.weather_app.databinding.WeatherFragmentBinding
import com.example.weather_app.models.current.WeatherForecastResponse
import com.example.weather_app.models.future.Daily
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.ui.viewmodels.WeatherViewModel
import com.example.weather_app.utils.Resource
import com.example.weather_app.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    val TAG = "Weather Fragment"
    private lateinit var binding: WeatherFragmentBinding
    private val viewModel: WeatherViewModel by activityViewModels()
    lateinit var weatherAdapter: FutureWeatherAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.weather_fragment,
            container,
            false
        )

        setupRecycleView()
        blurBackground()
        bindButtons()

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

        viewModel.futureWeatherForecast.observe(
            viewLifecycleOwner,
            Observer { futureResponse ->
                when (futureResponse) {
                    is Resource.Success -> {
                        hideProgressBar()
                        futureResponse.data?.let {
                            bindViewsFuture(it)
                            bindCircles(it)
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        futureResponse.message?.let { message ->
                            Log.e(TAG, "An error occured: $message")
                        }
                    }
                }
            }
        )
        return binding.root
    }

    private fun setupRecycleView() {
        // set background
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            Glide.with(this@WeatherFragment).load(R.drawable.background_main)
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        binding.relativeLayout.setBackground(resource)
                    }
                })
        }
        weatherAdapter = FutureWeatherAdapter()
        binding.rvFutureWeatherSmall.apply {
            adapter = weatherAdapter
            // layoutManager = lm
        }
    }

    private fun blurBackground() {
        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                setBlurWithGlide(scrollY)
            }
        )
    }

    private fun setBlurWithGlide(scrollY: Int) {
        if (scrollY == 0) {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                Glide.with(this@WeatherFragment).load(R.drawable.background_main)
                    .into(object : SimpleTarget<Drawable?>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                        ) {
                            binding.relativeLayout.setBackground(resource)
                        }
                    })
            }
        } else {
            Glide.with(this@WeatherFragment).load(R.drawable.background_main).apply(
                RequestOptions.bitmapTransform(
                    BlurTransformation((scrollY / 20), 1) // if sampling = 0 -> blur doesn't exist
                )
            ).into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    binding.relativeLayout.setBackground(resource)
                }
            })
        }
    }

    private fun bindButtons() {
        binding.btnSearch.setOnClickListener {
            binding.etSearch.visibility = View.VISIBLE
            binding.btnBackForSearch.visibility = View.VISIBLE
            binding.tvNameOfCity.visibility = View.GONE
            binding.tvCountryCode.visibility = View.GONE
        }

//        val searchText = binding.etSearch.text
//        if (searchText != null) {
//            if (viewModel.hasInternetConnection()) {
//                viewLifecycleOwner.lifecycleScope.launch {
//                    viewModel.getWeatherForecastFromAPI(searchText.toString())
//                }
//            } else {
//                Toast.makeText(requireContext(), "No Internet connection", Toast.LENGTH_SHORT).show()
//            }
//        }
//        binding.etSearch.hideKeyboard()
//        binding.etSearch.text.clear()
//        binding.tvNameOfCity.visibility = View.VISIBLE
//        binding.tvCountryCode.visibility = View.VISIBLE
//        binding.etSearch.visibility = View.GONE
//        binding.btnBackForSearch.visibility = View.GONE

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId != 0 || event.action == KeyEvent.ACTION_DOWN) {
                val searchText = binding.etSearch.text
                if (searchText != null) {
                    if (viewModel.hasInternetConnection()) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.getWeatherForecastFromAPI(searchText.toString())
                        }
                    } else {
                        Toast.makeText(requireContext(), "No Internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.etSearch.hideKeyboard()
                binding.etSearch.text.clear()
                binding.tvNameOfCity.visibility = View.VISIBLE
                binding.tvCountryCode.visibility = View.VISIBLE
                binding.etSearch.visibility = View.GONE
                binding.btnBackForSearch.visibility = View.GONE
                true
            } else {
                false
            }
        }

        binding.btnBackForSearch.setOnClickListener {
            binding.tvNameOfCity.visibility = View.VISIBLE
            binding.tvCountryCode.visibility = View.VISIBLE
            binding.etSearch.visibility = View.GONE
            binding.btnBackForSearch.visibility = View.GONE
            binding.etSearch.hideKeyboard()
        }

        binding.btnDetailForecast.setOnClickListener {
            findNavController().navigate(R.id.action_weatherFragment_to_futureWeatherFragment)
        }
    }

    private fun bindViews(response: WeatherForecastResponse) {
        binding.tvNameOfCity.text = response.name
        binding.tvCountryCode.text = response.sys.country
        binding.tvTemperature.text = "${response.main.temp.roundToInt()}°С"
        binding.tvTemperatureFeelsLike.text = "Feels like ${response.main.feels_like.roundToInt()}°С"
        binding.tvStateOfSky.text = response.weather[0].description
        binding.etSearch.text.toString()
        Log.d(TAG, "http://openweathermap.org/img/w/${response.weather[0].icon}.png")
        Glide.with(this).load("http://openweathermap.org/img/w/${response.weather[0].icon}.png").into(binding.ivTestImage)
    }

    private fun bindCircles(futureForecastResponse: FutureForecastResponse) {
        val currentDay = futureForecastResponse.daily[0]
        val currentHour = futureForecastResponse.hourly[0]

        binding.tvProbability.text = "${(currentHour.pop * 100).roundToInt()}%"
        binding.tvPressure.text = currentHour.pressure.toString() + " mb"
        binding.tvWindSpeed.text = currentHour.wind_speed.toString() + " km/h"
        binding.tvWindDirection.text = getWindDirection(currentHour.wind_deg)
        binding.tvHumidity.text = currentHour.humidity.toString() + "%"
        binding.tvVisibility.text = "${(currentHour.visibility / 1000)} km"
        binding.tvPrecipitation.text = currentDay.rain.roundToInt().toString() + " mm"
    }

    private fun getWindDirection(windDeg: Int): String {
        val listOfDirections = listOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N")
        val number = (windDeg / 22.5).roundToInt() + 1
        return listOfDirections[number - 1]
    }

    private fun bindViewsFuture(response: FutureForecastResponse) {
        val listOfDays: MutableList<Daily> = response.daily.toMutableList()
        listOfDays.removeAt(0)
        weatherAdapter.differ.submitList(listOfDays.toList())
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
}
