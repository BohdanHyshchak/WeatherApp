package com.example.weather_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.weather_app.R
import com.example.weather_app.databinding.TestFragmentBinding
import com.example.weather_app.databinding.WeatherFragmentBinding

class TestFragment : Fragment() {

    val TAG = "Test Fragment"
    private lateinit var binding: TestFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.test_fragment,
            container,
            false
        )
        return binding.root
    }
}
