package com.weatheradvisor.network.data.models

data class WeatherForecastResponse(
    val city: City,
    val list: List<Forecast>
)

data class City(
    val id: Int,
    val name: String,
    val country: String
)

data class Forecast(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Int
)
