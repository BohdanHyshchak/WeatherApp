package com.example.weather_app.ui.fragments

import android.Manifest
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.example.weather_app.ui.viewmodels.CurrentWeatherViewModel
import com.example.weather_app.utils.Constants
import com.example.weather_app.utils.WeatherUtility
import com.example.weather_app.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.roundToInt

@AndroidEntryPoint
class WeatherFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    val TAG = "Weather Fragment"
    private var _binding: WeatherFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CurrentWeatherViewModel by viewModels()
    private val weatherAdapter = FutureWeatherAdapter()

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
            viewLifecycleOwner, { response ->
                bindViews(response)
            }
        )

        viewModel.futureWeatherForecast.observe(
            viewLifecycleOwner, { futureResponse ->
                bindViewsFuture(futureResponse)
                bindCircles(futureResponse)
            }
        )

        viewModel.isProgressBarShown.observe(
            viewLifecycleOwner, {
                binding.progressBar.isVisible = it
            }
        )

        viewModel.isGeoOn.observe(
            viewLifecycleOwner, {
                binding.btnTestGeo.text = it.toString()
                if (it) {
                    requestPermissions()
                }
            }
        )

        viewModel._latLng.observe(
            viewLifecycleOwner, {
                Log.d(TAG, "${it.latitude}, ${it.longitude}")
            }
        )
    }

    private fun setupRecyclerView() {
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
                    binding.relativeLayout.background = resource
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

        binding.btnTestGeo.setOnClickListener {
            viewModel.isGeoOn.postValue(!viewModel.isGeoOn.value!!)
        }
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
            findNavController().navigate(R.id.futureWeatherFragment)
        }
    }

    private fun bindViews(response: WeatherForecastResponse) {
        binding.tvNameOfCity.text = response.name
        binding.tvCountryCode.text = response.sys.country
        binding.tvTemperature.text = "${response.main.temp.roundToInt()}????"
        binding.tvTemperatureFeelsLike.text =
            "Feels like ${response.main.feels_like.roundToInt()}????"
        binding.tvStateOfSky.text = response.weather[0].description
        binding.etSearch.text.toString()
        Log.d(TAG, "http://openweathermap.org/img/w/${response.weather[0].icon}.png")
        Glide.with(this).load("http://openweathermap.org/img/w/${response.weather[0].icon}.png")
            .into(binding.ivTestImage)
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
        val listOfDirections = listOf(
            "N",
            "NNE",
            "NE",
            "ENE",
            "E",
            "ESE",
            "SE",
            "SSE",
            "S",
            "SSW",
            "SW",
            "WSW",
            "W",
            "WNW",
            "NW",
            "NNW",
            "N"
        )
        val number = (windDeg / 22.5).roundToInt() + 1
        return listOfDirections[number - 1]
    }

    private fun bindViewsFuture(response: FutureForecastResponse) {
        val listOfDays: MutableList<Daily> = response.daily.toMutableList()
        listOfDays.removeAt(0)
        weatherAdapter.submitList(listOfDays.toList())
    }

    private fun requestPermissions() {
        if (WeatherUtility.hasLocationPermissions(requireContext())) {
            viewModel.getGeoResponse()
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions if you want to use geolocation.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions if you want to use geolocation.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        viewModel.getGeoResponse()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
