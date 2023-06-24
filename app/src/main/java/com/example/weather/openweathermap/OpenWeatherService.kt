package com.example.weather.openweathermap

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "c1161fa5c0ff7a42228234b7cc12d2e6"
interface OpenWeatherService {
    @GET("data/2.5/weather?units=metric&lang=fr")

    fun getWeather(@Query("q") CityName: String,
                   @Query("appid")apikey: String = API_KEY) : Call<WeatherWrapper>
}