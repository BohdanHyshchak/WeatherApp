package com.example.weather_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weather_app.R
import com.example.weather_app.adapters.DetailFutureAdapter
import com.example.weather_app.databinding.FutureWeatherFragmentBinding
import com.example.weather_app.databinding.RcvDetailDailyForecastBinding
import com.example.weather_app.models.future.Daily
import com.example.weather_app.models.future.FutureForecastResponse
import com.example.weather_app.ui.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FutureWeatherFragment : Fragment() {

    val TAG = "Future Weather Fragment"
    private lateinit var binding: FutureWeatherFragmentBinding
    private lateinit var rcvBinding: RcvDetailDailyForecastBinding
    private val viewModel: WeatherViewModel by activityViewModels()
    lateinit var weatherAdapter: DetailFutureAdapter

    // Todo: Replace RCV with ViewPager
    // Todo: rename XML layouts
    // Todo: make buttons with days instead back next in ViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.future_weather_fragment,
            container,
            false
        )
        rcvBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.rcv_detail_daily_forecast,
            container,
            false
        )

//        binding.btnNext.setOnClickListener {
//            val index = binding.rvFutureWeatherForecast.getCurrentPosition()
//            Log.d(TAG, index.toString())
//            binding.rvFutureWeatherForecast.smoothScrollToPosition(index + 1)
//        }
//
//        binding.btnBack.setOnClickListener {
//            val index = binding.rvFutureWeatherForecast.getCurrentPosition()
//            Log.d(TAG, index.toString())
//            if (index != 0) {
//                val test = rcvBinding.tvNameOfDay.text
//                Log.d(TAG, test.toString())
//                binding.rvFutureWeatherForecast.smoothScrollToPosition(index - 1)
//                val day = binding.rvFutureWeatherForecast.findViewHolderForAdapterPosition(index) // try to access childItem
//            }
//        }

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
//        binding.rvFutureWeatherForecast.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                return true
//            }
//        })
    }

    private fun bindButtons() {
    }
}
