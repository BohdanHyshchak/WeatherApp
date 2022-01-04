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
import androidx.core.view.isVisible
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
    private var _binding: WeatherFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    lateinit var weatherAdapter: FutureWeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.weather_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        blurBackground()
        bindButtons()
        initObservers()

    }

    override fun onStart() {
        super.onStart()
        setBackground()
    }

    private fun initObservers() {
        viewModel.weatherForecast.observe(
            viewLifecycleOwner,
            { response ->
                bindViews(response)
            }
        )

        viewModel.futureWeatherForecast.observe(
            viewLifecycleOwner,
            { futureResponse ->
                bindViewsFuture(futureResponse)
                bindCircles(futureResponse)
            }
        )

        viewModel.isProgressBarShown.observe(
            viewLifecycleOwner,
            {
                binding.progressBar.isVisible = it
            }
        )
    }

    private fun setupRecyclerView() {
        weatherAdapter = FutureWeatherAdapter()
        binding.rvFutureWeatherSmall.apply {
            adapter = weatherAdapter
            // layoutManager = lm
        }
    }

    private fun setBackground() {
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
                            binding.relativeLayout.background = resource
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
                    binding.relativeLayout.background = resource
                }
            })
        }
    }

    private fun bindButtons() {
        binding.btnSearch.setOnClickListener {
            binding.gCity.isVisible = false
            binding.gSearch.isVisible = true
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId != 0 || event.action == KeyEvent.ACTION_DOWN) {
                val searchText = binding.etSearch.text
                if (searchText.isNotBlank()) {
                    viewModel.setNameOfCity(searchText.toString())
                }
                binding.etSearch.hideKeyboard()
                binding.etSearch.text.clear()
                binding.gCity.isVisible = true
                binding.gSearch.isVisible = false
                true
            } else {
                false
            }
        }

        binding.btnBackForSearch.setOnClickListener {
            binding.gCity.isVisible = false
            binding.gSearch.isVisible = true
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
        weatherAdapter.submitList(listOfDays.toList())
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
