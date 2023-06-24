package com.example.weather.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.App
import com.example.weather.R
import com.example.weather.openweathermap.WeatherWrapper
import com.example.weather.openweathermap.mapOpenWeatherDataToWeather
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherFragment : Fragment() {


    companion object {
        val EXTRA_CITY_NAME = "EXTRA_CITY_NAME"

        fun newInstance() : WeatherFragment = WeatherFragment()
    }
    private val TAG = WeatherFragment::class.java.simpleName

    private lateinit var cityName: String

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var city: TextView
    private lateinit var weatherDescription: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var temperature: TextView
    private lateinit var humidity: TextView
    private lateinit var pressure: TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        refreshLayout = view.findViewById(R.id.swipe_refresh)
        city = view.findViewById(R.id.city)
        weatherIcon = view.findViewById(R.id.weather_icon)
        weatherDescription = view.findViewById(R.id.weather_description)
        temperature = view.findViewById(R.id.temperature)
        humidity = view.findViewById(R.id.humidity)
        pressure = view.findViewById(R.id.pressure)

        refreshLayout.setOnRefreshListener { refreshWeather() }
        return view
    }



         fun updateWeatherForCity(cityName: String?) {
        this.cityName = cityName!!
        this.city.text = cityName

        if(!refreshLayout.isRefreshing){
            refreshLayout.isRefreshing = true
        }
        val call = App.weatherService.getWeather("$cityName,fr, cm, us")
        call.enqueue(object: Callback<WeatherWrapper> {

            override fun onResponse(
                call: Call<WeatherWrapper>,
                response: Response<WeatherWrapper>?) {
                refreshLayout.isRefreshing = false
                response?.body()?.let {
                    val weather = mapOpenWeatherDataToWeather(it)
                    updateUi(weather)
                }
            }

            override fun onFailure(call: Call<WeatherWrapper>, t: Throwable) {
                Log.e(TAG, "Could not load city weather", t)
                Toast.makeText(activity,
                getString(R.string.weather_message_error_could_not_load_message),
                Toast.LENGTH_SHORT).show()
                refreshLayout.isRefreshing = false
            }

        })
    }

    private fun updateUi(weather: Weather) {

        Picasso.get()
            .load(weather.iconUrl)
            .placeholder(R.drawable.off_cloud_24px)
            .into(weatherIcon)

            weatherDescription.text = weather.description
        temperature.text = getString(R.string.weather_temperature_value, weather.temperature.toInt())
        humidity.text = getString(R.string.weather_humidity_value, weather.humidity)
        pressure.text = getString(R.string.weather_pressure_value, weather.pressure)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent!!.hasExtra(EXTRA_CITY_NAME)){
            updateWeatherForCity(requireActivity().intent.getStringExtra(EXTRA_CITY_NAME))
        }
    }

    private fun refreshWeather() {
        updateWeatherForCity(cityName)
    }

    fun clearUi() {
        weatherIcon.setImageResource(R.drawable.off_cloud_24px)
        cityName = ""
        city.text = ""
        weatherDescription.text = ""
        temperature.text = ""
        humidity.text = ""
        pressure.text = ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_weather, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh_weather -> {
            refreshWeather()
            true
        }
        else -> false
    }
}