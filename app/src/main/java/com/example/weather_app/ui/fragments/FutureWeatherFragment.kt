package com.example.weather_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.weather_app.R
import com.example.weather_app.adapters.DetailFutureAdapter
import com.example.weather_app.databinding.FutureWeatherFragmentBinding
import com.example.weather_app.models.future.Daily
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.ui.viewmodels.FutureWeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FutureWeatherFragment : Fragment() {

    val TAG = "Future Weather Fragment"
    private var _binding: FutureWeatherFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FutureWeatherViewModel by activityViewModels()
    lateinit var weatherAdapter: DetailFutureAdapter

    // Todo: Replace RCV with ViewPager
    // Todo: rename XML layouts
    // Todo: make buttons with days instead back next in ViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.future_weather_fragment,
            container,
            false
        )

        bindButtons()
        setupRecycleView()

        viewModel.futureWeatherForecast.observe(
            viewLifecycleOwner,
            { futureResponse ->
                bindViewsFuture(futureResponse)
            }
        )

        return binding.root
    }

    private fun bindButtons() {
        binding.btnNext.setOnClickListener {
            val index = binding.rvFutureWeatherForecast.currentItem
            Log.d(TAG, index.toString())
            binding.rvFutureWeatherForecast.currentItem = index + 1
        }

        binding.btnBack.setOnClickListener {
            val index = binding.rvFutureWeatherForecast.currentItem
            Log.d(TAG, index.toString())
            if (index != 0) {
                binding.rvFutureWeatherForecast.currentItem = index - 1
            }
        }

        viewModel.backButtonTitle.observe(
            viewLifecycleOwner,
            {
                setBtnTitle(binding.btnBack, it)
            }
        )

        viewModel.nextButtonTitle.observe(
            viewLifecycleOwner,
            {
                setBtnTitle(binding.btnNext, it)
            }
        )
    }

    private fun setBtnTitle(btn: Button, title: String?) {
        btn.text = title
        btn.isVisible = (title != null)
    }

    private fun bindViewsFuture(response: FutureForecastResponse) {
        val listOfDays: MutableList<Daily> = response.daily.toMutableList()
        // listOfDays.removeAt(0)
        weatherAdapter.setData(listOfDays.toTypedArray())
    }

    private fun setupRecycleView() {
        weatherAdapter = DetailFutureAdapter()
        binding.rvFutureWeatherForecast.apply {
            adapter = weatherAdapter
            // layoutManager = lm
        }
        binding.rvFutureWeatherForecast.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    Log.d("HELP", "HELP")
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    viewModel.sendViewPagerPosition(position)
                }
            })
    }
}
