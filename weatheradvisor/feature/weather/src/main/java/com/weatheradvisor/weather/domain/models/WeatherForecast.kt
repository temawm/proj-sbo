package com.weatheradvisor.weather.domain.models

data class WeatherForecast(
    val cityId: Int,
    val cityName: String,
    val countryCode: String,
    val forecasts: List<DailyForecast>
) {
    data class DailyForecast(
        val timestamp: Long,
        val temperature: Double,
        val pressure: Int,
        val humidity: Int,
        val windSpeed: Double,
        val windDirection: Int,
        val condition: WeatherCondition
    )

    data class WeatherCondition(
        val type: String,
        val description: String,
        val iconCode: String
    )
}