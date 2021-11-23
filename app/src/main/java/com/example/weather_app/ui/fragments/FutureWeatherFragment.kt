package com.example.weather_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.weather_app.R
import com.example.weather_app.databinding.FutureWeatherFragmentBinding

class FutureWeatherFragment : Fragment() {

    val TAG = "Test Fragment"
    private lateinit var binding: FutureWeatherFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.future_weather_fragment,
            container,
            false
        )
        return binding.root
    }
}
