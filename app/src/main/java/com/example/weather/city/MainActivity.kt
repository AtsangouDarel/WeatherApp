package com.example.weather.city

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.weather.WeatherActivity
import com.example.weather.weather.WeatherFragment

class MainActivity : AppCompatActivity(), CityFragment.CityFragmentListener {
    private lateinit var cityFragment: CityFragment
    private var weatherFragment: WeatherFragment? = null
    private var currentCity: City? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityFragment = supportFragmentManager.findFragmentById(R.id.city_fragment) as CityFragment
        cityFragment.listener = this

        weatherFragment = supportFragmentManager.findFragmentById(R.id.weather_fragment) as WeatherFragment?
    }

    override fun onResume() {
        super.onResume()
        if(!isHandsetLayout() && currentCity != null){
            weatherFragment?.updateWeatherForCity((currentCity!!.name))
        }
    }

    override fun onCitySelected(city: City) {
        currentCity = city
        if(isHandsetLayout()) {
        startWeatherActivity(city)} else {
            weatherFragment?.updateWeatherForCity(city.name)
        }
    }

    override fun onSelectionCleared() {
        cityFragment.selectFirstCity()
    }


    override fun onEmptyCities() {
        weatherFragment?.clearUi()
    }

    private fun isHandsetLayout(): Boolean = weatherFragment == null


    private fun startWeatherActivity(city: City) {
        val intent = Intent (this, WeatherActivity::class.java)
        intent.putExtra(WeatherFragment.EXTRA_CITY_NAME, city.name)
        startActivity(intent)
    }
}